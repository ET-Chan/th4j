

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
import th4j._
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import th4j.util.MacroHelper._
/**
 * Created by et on 01/10/15, modified from 
 * http://stackoverflow.com/questions/19791686/type-parameters-on-scala-macro-annotations
 */

/*
 * Generate all types for the annotated traits
 * There are two modes for generating abstract methods
 * 1). Native mode, in native mode, the library will bind all methods onto native library 
 * 2). Factory mode, the library will bind unimplemented mehthod into provided class getter function.
 * In native mode, binderName is the native library providing the necessary libraries, 
 * affix is the prefix needed to be prepended to generated helper instances.
 * 
 * In factory mode, binderName are the provider names, if there are multiple abstract methods
 * needed to be implemented, then the binderName is a string with provider names, separated by ","
 * e.g. StorageFunc, PointerFunc, if empty string is provided as binderName, or some binderNames are missing, 
 * then the companion object of the return type of the corresponding methods will be used as binder.
 * affix is used to indicate
 * whether appended original methodName to the generated method
 * 
 * 
 * */
@compileTimeOnly("enable macro paradise to expand macro annotations")
class GenerateAllTypes(mode: String, binderName: String, affix: String) extends StaticAnnotation{
  def macroTransform(annottees: Any*):Any = macro generateAllTypesImpl.impl
}





object generateAllTypesImpl{
  def impl(c:Context)(annottees: c.Expr[Any]*):c.Expr[Any]={
    import c.universe._

    val mode :: binderName :: affix :: Nil = getAnnnotationArgs(c)
    
//    /*-----------------------------------*/
    /*
     * Automatically generate companion object for a given trait
     * Generate all predefined types for the companion object
     * Default behaviour of modifiedCompanions is native mode,
     * that is all abstract methods are linked to native methods
     * 
     * */
    
    assert(mode == "Native" | mode == "Factory", "Not recognized mode")
    
    def modifiedCompanion(classDecl:ClassDef) = {
      //iterate through every type
      allTypes.map{case (prefix, (real, accReal))=>{
        val parents = 
           AppliedTypeTree(
             Ident(
                 classDecl.name), 
             List(
                 Ident(TypeName(real)), 
                 Ident(TypeName(accReal))))
                 
        val getterName = TermName("get"+prefix)
        
        if (mode == "Native"){
          
         val instanceName = TermName(prefix + "Instance")
         
         (q"""
            @GenerateType("Native",${affix + prefix}, $binderName, "1") object $instanceName extends $parents
          """, q"""def $getterName() = $instanceName""")
        }else if (mode == "Factory"){
          val clazzName = TypeName(prefix + classDecl.name)
         (q"""
            @GenerateType("Factory", ${"get" + prefix}, $binderName, $affix) class $clazzName extends $parents
          """,q"""def $getterName = new $clazzName""")
          
        }else{
          (q"",q"")
        }
      }}.unzip
            
      
    }
    
    
    /*-----------------------------------*/
    
    
    annottees.map(_.tree).toList match {
      
      case (classDecl:ClassDef) :: Nil=> {
        //start to modify the structure
//        val compDeclOpt = Nil
        val (compDecl, getterDecls) = modifiedCompanion(classDecl)
        c.Expr(q"""
          $classDecl
          object ${classDecl.name.toTermName} {
            ..$compDecl
            ..$getterDecls
          }
          """)
        
      }
      case _ => {c.abort(c.enclosingPosition, "Invalid Annottee, only support trait expansion")}
    }
    
    
  }
}



//object GenerateMacro {
//  //Generate All types based on the implementation of TH library
//  //This is also to be used by each generic functions, that's why I cannot
//  
//
//
//
////  def generateAllTypes[T](clazz:Class[T]):Map[String, Any] = macro generateAllTypesImpl[T]
//  def generateAllTypesImpl[T:c.WeakTypeTag](c:Context)
//                                           (clazz:c.Expr[Class[T]]):c.Expr[Map[String, Any]]={
//    //This method generates all types availabe in TH
//
//    import c.universe._
//    val gTpeList = weakTypeOf[T]
//
//    val ret = mutable.Stack[Tree]()
//
//    val defaultTypes = TypeMacro
//    .generateAllTypeTags()
//    .asInstanceOf[List[(Type, Type, String)]]
//    for{(r, aR, prefix)<-defaultTypes}{
//      val extendPrefix = defaultPrefix + prefix
//      val tpeList = appliedType(gTpeList, List(r, aR))
//
//      ret.push(q"""(${r.toString + aR.toString}, overrideFunc(classOf[$tpeList], $extendPrefix))""")
//    }
//    
//
////    for{(realName, (accRealName, prefix))<-defaultTypes))}{
////      
////      
////    }
//    
//    c.Expr[Map[String, T]](q"""List(..${ret.toList}).toMap""")
//
//  }
//}


