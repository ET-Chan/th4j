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
  def impl [T:c.WeakTypeTag](c:Context)(luaFunc: c.Tree, L: c.Tree):c.Tree ={
    import c.universe._
    import c.universe.Flag._
    //1), get the tpe of T (e.g. String=>Int)
    val tpe = weakTypeOf[T]
    println(showRaw(tpe))
    //2) get argument and return types
    val types = tpe.typeArgs
    val (params, retTpe) = types.splitAt(types.length - 1)
//    println(showRaw(retTpe.head.typeArgs))
    val retTypes = {
      val h = retTpe.head
      if (h.typeArgs.isEmpty)
        List(h)
      else
        retTpe.head.typeArgs
    }
//    println(showRaw(retTypes))
    //3), construct method args
    def pushtoLuaState(s:Type, name:Tree):Tree ={

      if (s =:= typeOf[Int]){
        q"$L.pushInteger($name)"
      }else if (s =:= typeOf[Float] | s=:= typeOf[Double] |
        s=:= typeOf[Short] | s=:= typeOf[Byte] | s=:=typeOf[Char] | s=:=typeOf[Long]){
        q"$L.pushNumber($name.toDouble)"
      }else if (s =:= typeOf[String]){
        q"$L.pushString($name)"
      }else {
        c.abort(c.enclosingPosition, "Invalid argument types, " +
                    "only supports AnyVal and String")
      }
    }

    def popFromLuaState(s:Type, idx:Int):Tree = {
      println(showRaw(s))
      if (s =:= typeOf[Int]){
        q"$L.toInteger($idx)"
      }else if (s =:= typeOf[Float] | s=:= typeOf[Double] |
        s=:= typeOf[Short] | s=:= typeOf[Byte] | s=:=typeOf[Char] | s=:=typeOf[Long]) {
        q"$L.toNumber($idx).asInstanceOf[$s]"
      }else if (s =:= typeOf[String]){
        q"$L.toString($idx)"
      }else{
        c.abort(c.enclosingPosition, "Invalid return types, only supports AnyVal and String)")
      }
    }
    val (valDefParams, pushDef) = params.zipWithIndex.map{case (s, idx)=>{
      val vd = ValDef(Modifiers(PARAM), TermName("arg" + idx), TypeTree(s) , EmptyTree)
      val pd = pushtoLuaState(s, q"${vd.name}")
      (vd, pd)
    }}.unzip
    //4), construct return args
    val retDef = retTypes.zip(-retTypes.length until 0).map{case (s, idx)=>{
     popFromLuaState(s, idx)
    }}
    //5), construct types information TODO: should we handle the type conversions in Java side
    //    or in Lua Side (current)?
    val typeDef = params.zip(1 to params.length).map{ case (s, idx)=>{
      q"""
         $L.pushInteger($idx)
         $L.pushString(${s.toString})
         $L.setTable(-3)
       """
    }}

    //6), construct methods body
    val funcbody =
      q"""
        $L.getGlobal("th4j")
        $L.getField(-1, "javaWrapper")
        $L.getGlobal($luaFunc)
        $L.newTable(${params.length}, 0)
         ..$typeDef
        ..$pushDef
        $L.call(${params.length + 2}, ${retTypes.length})
        val r = (..$retDef)
        $L.pop(${retTypes.length + 1})
        r
       """
    //7), plug everything together

      q"""
         new LuaFunction[$tpe]($luaFunc, $L){
          override def apply:$tpe = (..$valDefParams)=>$funcbody
         }


       """
  }

}
