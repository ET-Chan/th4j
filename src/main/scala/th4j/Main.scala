

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

  val L = new LuaState()

  L.openLibs()
//  println(L.getTop)
  L.load(Files.newInputStream(Paths.get("lua/hello.lua")),"=hello")
  L.call(0,0)
  L.getGlobal("readFromTensor")
  val t = new DoubleTensor(4, 5).fill(1.0)

  L.pushNumber(t.getPeerPtr())
  L.call(1,0)
//  L.getGlobal("newTensor")
//  L.call(0, 1)
//  val ptr = new Pointer(L.toNumber(1).toLong)

//  val t = new DoubleTensor(ptr)
//  print(t)

//  L.rawGet(1)



//  val L = LuaStateFactory.newLuaState()
//  L.openLibs()
//
//  val retcode = L.LdoFile("lua/hello.lua")
//  val errstr = if (retcode!=0) L.toString(-1) else ""
//  println(errstr)
//
//  L.getField(LuaState.LUA_GLOBALSINDEX, "test")
//  val a = 0.3d
//  val b = 0.5d
//  L.pushNumber(a)
//  L.pushNumber(b)
//  L.call(2, 1)
//  val obj = L.getLuaObject(-1)
//  val (ret1, ret2) = (L.getLuaObject(obj, 1),L.getLuaObject(obj, 2))
//
//  println(ret1.getNumber, ret2.getNumber)


}
