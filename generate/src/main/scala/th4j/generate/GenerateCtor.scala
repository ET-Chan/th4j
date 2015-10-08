

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