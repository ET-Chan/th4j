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

/**
  * Created by et on 01/11/15.
  */

import com.naef.jnlua.LuaState

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
//import scala.reflect.runtime.universe.Flag._

object GenerateLuaFunc {
  def impl [T:c.WeakTypeTag](c:Context)(luaFunc: c.Expr[String], L: c.Expr[LuaState]):c.Expr[Any] ={
    import c.universe._
    import c.universe.Flag._
    //1), get the tpe of T (e.g. String=>Int)
    val tpe = weakTypeOf[T]
    //2) get argument and return types
    val types = tpe.typeArgs
    val (params, retTpe) = types.splitAt(types.length - 1)
    val retTypes = {
      val h = retTpe.head
      if (retTpe.head.typeArgs.isEmpty)
        List(h)
      else
        retTpe.head.typeArgs
    }
    //3), construct method args
    val valDefParams, pushDef = params.zipWithIndex.map{case (s, idx)=>{
      val vd = ValDef(Modifiers(PARAM), TermName("arg" + idx), TypeTree(s) , EmptyTree)
      val pd = s match {
        case tq"Int"=>
          q"$L.pushInteger(${vd.name})"
        case tq"Double" | tq"Float" | tq"Short" | tq"Byte" | tq"Char" =>
          q"$L.pushNumber(${vd.name}.toDouble)"
        case tq"String" =>
          q"$L.pushString(${vd.name})"
        case _=>
          c.abort(c.enclosingPosition, "Invalid Types, " +
            "only supports AnyVal, and List[T], where T<:AnyVal")
      }
      (vd, pd)
    }}.unzip
    //4), construct pushing, support list to be pushed.


    val funcbody =
      q"""
        $L.getGlobal("th4j.javaWrapper")

       """
    //4), construct return values

//    c.Expr[Any](
//      q"""
//         new LuaFunction[$tpe]($funcName){
//          override def apply:$tpe = (..$valDefParams)=>$funcbody
//         }
//
//
//       """)
    c.Expr[Any](q"")
  }

}
