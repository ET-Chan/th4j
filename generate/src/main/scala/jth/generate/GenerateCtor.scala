package jth.generate



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import jth.util.MacroHelper._


@compileTimeOnly("enable macro paradise to expand macro annotations")
class GenerateCtor extends StaticAnnotation  {
  def macroTransform(annottees: Any*):Any = macro generateCtor.impl
}

object generateCtor{
  def impl(c:Context)(annottees:c.Expr[Any]*):c.Expr[Any]={
    import c.universe._
    /*-------------------------------------------------*/
    def expandMethod(methodDef:DefDef):c.Expr[Any]={
      //extract everything out
      val DefDef(_, name, tparams, vparamss, tpt, rhs) = methodDef
      //we make three assumptions
      //(1), no modifiers
      //(2), no type parameters
      //(3), no currying
      //(4), primary constructor takes no parameter.
      //Now, we generate corresponding constructor and appoint to a this function
      
      val callParams = vparamss.map(_.map(_.name))
      
      val thisDef =q"""
                      def this(...$vparamss)={
                         this()
                         $name(...$callParams)
                      }
                    """
      val modMethodDef = DefDef(
          Modifiers(NoFlags, 
              typeNames.EMPTY, 
              List(Apply(Select(New(Ident(TypeName("GeneratedCtor"))),
                 termNames.CONSTRUCTOR), List()))),
                 name, tparams, vparamss, tpt, rhs)
      c.Expr[Any](q"""
          $modMethodDef
          $thisDef
          """)
    }
    
    /*-------------------------------------------------*/
    annottees.map(_.tree).toList match{
      case (methodDef : DefDef) :: _ =>{
        expandMethod(methodDef)
      }
      case _ =>{
        c.abort(c.enclosingPosition, "Invali annottee, only support method def.")
      }
      
    }
    
    
    /*-------------------------------------------------*/
    
    
  }
  
}