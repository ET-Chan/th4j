package jth.util

import scala.reflect.macros.whitebox.Context

object MacroHelper {
   def getAnnnotationArgs(c:Context):List[String]={
      import c.universe._
      c.prefix.tree match {
        case q"""new $clazz(..$expParams)""" => {
          expParams.map{case Literal(Constant(arg)) => arg.toString}
        } 
        
      }
    }
}