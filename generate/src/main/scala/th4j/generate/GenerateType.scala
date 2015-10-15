

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iat Chong Chan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package th4j.generate

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

import th4j.util.MacroHelper._
import th4j.util.CPU
/*
 * Generate an object (or class) abstract methods automatically according 
 * annotation arguments
 * There are two modes for generating abstract methods
 * 1). Native mode, in native mode, the library will bind all methods onto native library 
 * 2). Factory mode, the library will bind unimplemented mehthod  into provided class getter function.
 * 3). Template mode, the abstract methods will be declared by a template.
 * In native mode, affix is the native library functions prefix, implSource is the native library name (e.g. "libTH.so", TH is the name)
 * templates carry prefix, real and accReal information.
 *
 *
 * In factory mode, affix is the suffix for getter functions,
 * implSource are the Objects providing the getter function (e.g. StorageFunc.getInt)
 * separated by ","
 * if implSource is an empty string, or some implSource are not provided, 
 * (e.g.) "StorageFunc",,"PointerFunc", then the companion objects of the return type
 * of the corresponding method will be implSource, templates carry prefix, real and accReal information.
 *
 * In Template Mode, affix is the "prefix, real, accReal", implSource and isAppendMethodName is not used
  * templates is the template supplying for implementation of the abstract methods
  * they are strings waiting to be interpolated. The available argument for interpolation from the first one is
  *   (1)prefix, (2) real, (3) accReal
 * */
@compileTimeOnly("enable macro paradise to expand macro annotations")
class GenerateType(mode: String,
                   affix:String,
                   implSource: String,
                   isAppendMethodName: String,
                   templates: String*) extends StaticAnnotation{
  def macroTransform(annottees: Any*):Any = macro generateType.impl
}

object generateType{
  def impl(c:Context)(annottees: c.Expr[Any]*):c.Expr[Any]={
    import c.universe._


    val mode :: affix :: implSource :: isAppendMethodName :: templates = getAnnnotationArgs(c)

    assert(mode == "Native" | mode == "Factory" | mode == "Template")


    def checkIfRealMatch(s:Symbol):Option[List[String]]={
      if (s.annotations.nonEmpty && s.annotations.head.tree.tpe == typeOf[IfRealMatch]){
        Some(s.annotations.head.tree.children.tail.map{
          case Literal(Constant(str))=>str.toString
        })

      }else
        None
    }


    def getMethodInfo(parent:Type, s: Symbol): (TermName, Type, List[ValDef], List[TermName], TermName) = {
      val method = s.asMethod
      val methodName = method.name
      val MethodType(params, retType) = method.typeSignatureIn(parent)
      val (valDefParams, callParams) = params.map { s => {
        val vd = internal.valDef(s)
        (vd, vd.name)
      }
      }.unzip
      val binderMethodName = TermName(affix + {
        if (isAppendMethodName == "1") methodName.toString else ""
      })
      (methodName, retType, valDefParams, callParams, binderMethodName)
    }
    def expandObject(moduleDecl:Any):c.Expr[Any]={
      //Fix this boilerplate code
      val (mods, name, parents, self, body) = moduleDecl match {
        case ModuleDef(_mods, _name, Template(_parents, _self, _body)) => {
          val m = ModuleDef(_mods,_name, Template(_parents, _self, _body))
          (_mods, _name, _parents, _self, _body)
        }
        case ClassDef(_mods, _name, _, Template(_parents, _self, _body))=>{
          (_mods, _name,_parents, _self, _body)
        }
      }

      //      val ModuleDef(mods, name, Template(parents, self, body)) = moduleDecl
      assert(parents.length == 1, "Only support single inheritance")
      //Why this magic number 1 exist?
      //This is just a dummy integer to let the compiler type checked parents.head properly
      //and can be whatever you want.
      val t = TypeName(templates(3))

      val parent = c.typecheck(
        q"""
            import th4j.util._
           1.asInstanceOf[${parents.head}]""").tpe

      /*
       * A hack to trick the compiler, let it believe our impls are behind the default ctor
       * 
       * */
      val defaultImplPos = c.enclosingPosition
      val newImplPos = defaultImplPos
        .withEnd(defaultImplPos.endOrPoint + 1)
        .withStart(defaultImplPos.startOrPoint + 1)
        .withPoint(defaultImplPos.point + 1)


      val implSources = implSource.split(",")
      var idx = 0
      //(1), implement abstract method first
//      println("*"*5, parent.baseClasses)
      val sortedDecls = parent.decls.sorted
//      parent.baseClasses.head.typeSignature.decls.sorted.foreach(s=>{
//        val matchReal = checkIfRealMatch(s)
//        if (matchReal.isDefined) println("*"*5, matchReal.get)
//      })
//
//      sortedDecls.foreach(s=>println(s.annotations))


      val abstractMethods = sortedDecls
        .filter(_.isAbstract)
        .flatMap(s=>{
          val (methodName, retType, valDefParams, callParams, binderMethodName) = getMethodInfo(parent, s)
          if (mode == "Native"){
            val matchReal = checkIfRealMatch(s)
            if (matchReal.isDefined && !matchReal.get.contains(templates(1))){
              List(q"""override def $methodName(..$valDefParams) = throw new Exception("Not implemented")""")
            }else {
              List(
                q"""@native def $binderMethodName (..$valDefParams):$retType""",
                q"""override def $methodName(..$valDefParams) = $binderMethodName(..$callParams)""")
            }
          }else if (mode == "Factory"){
            val source = {
              if (implSource != "" && implSources(idx) != "")
                implSources(idx)
              else{
                //I only come up with this super ugly trick
                //if you have better idea, please do notify me
                val TypeRef(_,sym,_) = retType
                showRaw(sym).toString().split("\\.").last
              }
            }
            idx = idx + 1
            val provider =  Apply(Select(Ident(TermName(source)), binderMethodName), List())
            List(q"""override def $methodName(..$valDefParams) = $provider""")
          }else if (mode == "Template"){
            val template = templates(idx)
            idx = idx + 1
            val Array(prefix, real, accReal, device) = affix.split(",")
            val parsedTemplate = c.parse(template.format(prefix, real, accReal, device))
            List(q"""override def $methodName(..$valDefParams) = $parsedTemplate""")
          }else{
            List()
          }
        })

      val ctors =
      {if (mode != "Factory") List() else{
        sortedDecls.filter(_.name.toString.startsWith("ctor")).flatMap(s=>{
          val (methodName, retType, valDefParams, callParams, binderMethodName) = getMethodInfo(parent, s)
          List(q"""
                def this(..$valDefParams) = {
                  this()
                  $methodName(..$callParams)
                }
              """,
            q"""
                 def create(..$valDefParams) = {
                    new ${name.toTypeName}(..$callParams)
                 }

               """)
        })
      }}
      import th4j._


      val restriction = restrictions.getOrElse(showRaw(parent.typeSymbol).toString.split("\\.").last, Map())

      val protectedMethods = {if (mode!="Factory")List() else{
        for{s<- sortedDecls
            if s.isMethod && s.asMethod.isProtected
            isFulfill = restriction.get(s.name.toString).map(_.contains(templates(1)))
            if isFulfill.contains(true)
            (methodName, retType, valDefParams, callParams, binderMethodName) = getMethodInfo(parent, s)} yield{
          q"""override def $methodName(..$valDefParams) = super.$methodName(..$callParams)"""
        }
      }}

      val moddedMethods = (abstractMethods ++ ctors ++ protectedMethods).map(s=> atPos(newImplPos)(s))

      if (mode == "Native"){
        c.Expr[Any](
            ModuleDef(mods, name.toTermName, Template(parents, self, (
              q"""Native.register($implSource)
                 Native.setProtected(true)
               """ :: body) ++ moddedMethods)))
      }else if (mode == "Factory"){
        val ret =  c.Expr[Any](
          ClassDef(mods, name.toTypeName, List(), Template(parents, self, body ++ moddedMethods.toList)) 
        )
        ret
      }else if (mode == "Template"){
        c.Expr[Any](
          ModuleDef(mods, name.toTermName, Template(parents, self, body ++ moddedMethods.toList))
        )
      }else {//impossible to be here
        c.Expr[Any](q"")
      }

    }

    /*-------------------------------------------*/

    annottees.map(_.tree).toList match{
      case (moduleDecl:ModuleDef) :: Nil => {
        val expandedDecl = expandObject(moduleDecl)
        expandedDecl
      }

      case (classDecl:ClassDef) :: Nil =>{
        expandObject(classDecl)
      }

      case _ =>
        c.abort(c.enclosingPosition, "Invalid Annottee, only support object instantiation")

    }

    //    c.Expr[Any](q"""""")
  }
}

