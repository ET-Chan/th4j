/**
 * Created by et on 25/09/15.
 */
package jth

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

object ImplMacro {

  def overrideFunc[T](clazz:Class[T], prefix: String):T= macro overrideFuncImpl[T]

  def overrideFuncImpl[T:c.WeakTypeTag](c:Context)
                                       (clazz:c.Expr[Class[T]], prefix:c.Expr[String]):c.Expr[T]={
    import c.universe._

    val tpeList = weakTypeOf[T]
    val (override_methods, native_methods) = tpeList.decls.filter(_.isAbstract).map(s=>{
      val method = s.asMethod
      val methodName = method.name
      val gTpeList = appliedType(tpeList, List(typeOf[Any], typeOf[Any]))
      val MethodType(genericParams, genericRetType) = method.typeSignatureIn(gTpeList)
      val MethodType(params,retType) = method.typeSignatureIn(tpeList)
      val (valDefParams, callParams) = params.map(s=>{
        val vd = internal.valDef(s)
        (vd, vd.name)
      }).unzip
      val realPrefix = c.eval(prefix)
      val nativeMethodName = TermName(realPrefix + methodName.toString)
      val native_method = q"""@native def $nativeMethodName (..$valDefParams):$retType"""
      val override_method = q"""override def $methodName(..$valDefParams) = $nativeMethodName(..$callParams)"""
      (override_method, native_method)
    }).unzip
    //    val methods = weakTypeOf[T].decls.filter(_.isMethod).foreach(println)
    //    val methods = Class.forName()

    c.Expr[T](q""" val ret = new $tpeList{
                    ..$native_methods
                    ..$override_methods
                   }
      ret""")
  }
  
  
  def generateTypeOf() = macro generateTypeTupleImpl
  def generateTypeTupleImpl(c:Context)():c.Expr[Any]={
    import c.universe._
    val ret = allTypes.map{case (r, (ar, pre))=>{
      val rt = TypeApply(Ident(TermName("typeOf")), List(Ident(TypeName(r))))
      val at = TypeApply(Ident(TermName("typeOf")), List(Ident(TypeName(ar))))
      q"""($rt, $at, $pre)"""
    }}
    
//    val ret = ls.tree.collect{case q"""$_(...${tuples:List[List[Tree]]})""" if tuples.nonEmpty=>
//
//      tuples.flatten.collect{
//        case q"""$_(${real:String}, ${accReal:String}, ${prefix:String})"""=>{
//          val rtq = TypeApply(Ident(TermName("typeOf")), List(Ident(TypeName(real))))
//          val atq = TypeApply(Ident(TermName("typeOf")), List(Ident(TypeName(accReal))))
//          q"""($rtq, $atq,$prefix)"""
//      }}
//    }.flatten
    c.Expr[Any](q"List(..$ret)")
  }
}







