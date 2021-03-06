

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

package th4j

import java.nio.ByteOrder
import java.nio.file.{Paths, Path, Files}

import com.naef.jnlua.LuaState
import com.sun.jna._
import th4j.Storage._
import th4j.Tensor._
import th4j.func._
import th4j.util._
import scala.sys.SystemProperties
import scala.util.{Random, Try}

object Main extends App {
  val prob = new SystemProperties()
  prob("jna.library.path") = "./nativeLib"
  prob("java.library.path") = "./nativeLib:" + prob("java.library.path")
  val fieldSysPath = classOf[ClassLoader].getDeclaredField( "sys_paths" )
  fieldSysPath.setAccessible( true )
  fieldSysPath.set( null, null )
  Native.loadLibrary("libjnlua5.1.so",
    classOf[Library])

//
//  L.openLibs()
////  println(L.getTop)

//  L.call(0,0)
//  L.getGlobal("readFromTensor")
//  val t = new DoubleTensor(4, 5).fill(1.0)
//  L.pushIn
//  L.pushNumber(t.getPeerPtr())
//  L.call(1,0)
//  L.getGlobal("newTensor")
//  L.call(0, 1)
//  val ptr = new Pointer(L.toNumber(1).toLong)

//  val t = new DoubleTensor(ptr)
//  print(t)


  val L = new LuaState()
  L.openLibs()
  L.load("""require 'th4j' """, "=wrapper")
  L.call(0, 0)
  L.load(Files.newInputStream(Paths.get("lua/test.lua"))
    , "=hello")
  L.call(0, 0)

//
//  L.getGlobal("test")
//  L.pushInteger(1)
//  L.call(1, 2)
//  println(L.toInteger(-1), L.toInteger(-2))

  val test = LuaFunction.create[(DoubleTensor, DoubleTensor, String)=>(LongStorage, String)]("test", L)
  println(test()(new DoubleTensor(3, 4), new DoubleTensor(), "Hello From Java"))
//  println(Pointer.nativeValue(a.ptr))
//  println(a)
}
