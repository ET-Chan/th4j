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

package th4j.util

import th4j.generate.GenerateLuaFunc
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import com.naef.jnlua.LuaState


/**
  * Created by et on 01/11/15.
  * Lua Function help users build
  * adaptor quickly between Lua and JVM
  * without repeatedly rewriting the same
  * piece of boiler plate code. Noticeably, it automatically
  * transforms the JVM tensors to Lua Tensors and vice versa.
  * Reference counters are handled carefully as well.
  * Warning: Calling on LuaFunction on the same LuaState
  * is not thread-safe.
  */
abstract class LuaFunction[T] (luaFunc: String, L: LuaState) {
  def apply():T
}

object LuaFunction {
  def create[T](luaFunc: String, L: LuaState):LuaFunction[T] = macro GenerateLuaFunc.impl[T]
//  def create[T](luaFunc:String, L:LuaState) = _create[T](luaFunc, L).asInstanceOf[LuaFunction[T]]
//  def newSample = new LuaFunction[Int=>Int]("test") {
//    override def apply: (Int) => Int = (a)=>1
//  }
}