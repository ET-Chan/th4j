package jth

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.collection.mutable
import ImplMacro._
import scala.collection.mutable.Stack
/**
 * Created by et on 01/10/15, modified from 
 * http://stackoverflow.com/questions/19791686/type-parameters-on-scala-macro-annotations
 */

/*
 * Generate all types for the annotated traits
 * There are two modes for generating abstract methods
 * 1). Native mode, in native mode, the library will bind all methods onto native library 
 * 2). Factory mode, the library will bind unimplemented mehthod (usually only one) into provided class getter function.
 * In native mode, binderName is the native library providing the necessary libraries, 
 * prefix is the prefix needed to be prepended to generated helper instances.
 * 
 * In factory mode, binderName is the object name, providing the getter functions,
 * prefix is not used.
 * 
 * */
@compileTimeOnly("enable macro paradise to expand macro annotations")
class GenerateAllTypes(mode: String, binderName: String, prefix: String) extends StaticAnnotation{
  def macroTransform(annottees: Any*):Any = macro generateAllTypesImpl.impl
}





object generateAllTypesImpl{
  def impl(c:Context)(annottees: c.Expr[Any]*):c.Expr[Any]={
    import c.universe._
    import jth.util.MacroHelper._
    
    val mode :: binderName :: prefix :: Nil = getAnnnotationArgs(c)
    
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

      allTypes.map{case (real, (accReal, prefix))=>{
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
         //fix this boiler plate
         (q"""
            @GenerateType("Native",${defaultPrefix + prefix}, "TH") object $instanceName extends $parents
          """, q"""def $getterName() = $instanceName""")
        }else if (mode == "Factory"){
          val clazzName = TypeName(prefix + classDecl.name)
          
         (q"""
            @GenerateType("Factory", ${"get" + prefix}, $binderName) class $clazzName extends $parents
          """,q"""def $getterName = new $clazzName""")
          
        }else{
          (q"",q"")
        }
      }}.unzip
            
      
    }
    
    
    /*-----------------------------------*/
    
    
    annottees.map(_.tree).toList match {
      
      case (classDecl:ClassDef) :: Nil if classDecl.mods.hasFlag(Flag.TRAIT)=> {
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


