package jth
//this is for all type generating functions
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

object TypeMacro {
  def generateAllTypeTags():List[Any]= macro generateAllTypeTagsImpl
  def generateAllTypeTagsImpl(c:Context)():c.Expr[List[Any]]={
    import c.universe._
    
    val ret = allTypes.map{case (r, (ar, prefix))=>{
      val rtq = TypeApply(Ident(TermName("typeOf")), List(Ident(TypeName(r))))
      val atq = TypeApply(Ident(TermName("typeOf")), List(Ident(TypeName(ar))))
      q"""($rtq, $atq, $prefix)"""
    }}
    c.Expr[List[Any]](q"List(..$ret)")
  }
  
}