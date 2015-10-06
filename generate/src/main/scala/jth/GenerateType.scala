package jth

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import javax.naming.Name
import jth.util.MacroHelper._



/*
 * Generate an object (or class) abstract methods automatically according 
 * annotation arguments
 * There are two modes for generating abstract methods
 * 1). Native mode, in native mode, the library will bind all methods onto native library 
 * 2). Factory mode, the library will bind unimplemented mehthod (usually only one) into provided class getter function.
 * 
 * In native mode, affix is the native library functions prefix, implSource is the native library name (e.g. "libTH.so", TH is the name)
 * 
 * In factory mode, affix is the suffix for getter functions, implSource is the Object providing the getter function (e.g. StorageFunc.getInt)
 * 
 * */
@compileTimeOnly("enable macro paradise to expand macro annotations")
class GenerateType(mode: String, affix:String, implSource: String) extends StaticAnnotation{
  def macroTransform(annottees: Any*):Any = macro generateType.impl
}

object generateType{
  def impl(c:Context)(annottees: c.Expr[Any]*):c.Expr[Any]={
    import c.universe._
    
   
    val mode :: affix :: implSource :: Nil = getAnnnotationArgs(c)
    
    assert(mode == "Native" | mode == "Factory")
    
    def expandObject(moduleDecl:Any):c.Expr[Any]={
      //Fix this boilerplate code
      val (mods, name, parents, self, body) = moduleDecl match {
        case ModuleDef(mods, name, Template(parents, self, body)) => {
          (mods, name, parents, self, body)
        }
        case ClassDef(mods, name, _, Template(parents, self, body))=>{
          (mods, name, parents, self, body)
        }
      }
      
//      val ModuleDef(mods, name, Template(parents, self, body)) = moduleDecl
      assert(parents.length == 1, "Only support single inheritance")
      //Why this magic number 1 exist?
      //This is just a dummy integer to let the compiler type checked parents.head properly
      //and can be whatever you want.
      val parent = c.typecheck(q"1.asInstanceOf[${parents.head}]").tpe
      val (overrideMethods, binderMethods) = parent
      .decls
      .filter(_.isAbstract)
      .map { s => {
        val method = s.asMethod
        val methodName = method.name
        val MethodType(params, retType) = method.typeSignatureIn(parent)
        val (valDefParams, callParams) = params.map{s=>{
          val vd = internal.valDef(s)
          (vd, vd.name)
        }}.unzip
        val binderMethodName = TermName(affix  + methodName.toString)
        
        
        
        val (binderMethod, overrideMethod) = 
          if (mode == "Native"){
            (q"""@native def $binderMethodName (..$valDefParams):$retType""",
                q"""override def $methodName(..$valDefParams) = $binderMethodName(..$callParams)""")}
          else if (mode == "Factory"){
//            q"""def $binderMethodName (..$valDefParams):$retType = """ 
            val provider =  Apply(Select(Ident(TermName(implSource)), TermName(affix)), List())
            (q"",q"""override def $methodName(..$valDefParams) = $provider""")
          }else{ //impossible to reach
            (q"",q"")
           }
            
            
        (overrideMethod, binderMethod)
      } }.unzip
      if (mode == "Native"){
        c.Expr[Any](
            ModuleDef(mods, name.toTermName, Template(parents, self, (q"""Native.register($implSource)""" :: body) ++ binderMethods ++ overrideMethods)))
      }else if (mode == "Factory"){
        c.Expr[Any](
          ClassDef(mods, name.toTypeName, List(), Template(parents, self, body ++ overrideMethods))    
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

