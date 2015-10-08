

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

import jth.Storage._
import jth.Storage.FloatStorage
import com.sun.jna._
import th4j.func.{StorageFunc, TensorFunc}


object Main extends App {

  val d= new DoubleStorage(100)
  val t = (0 until 100 map{_.toShort}).toArray
  val l = new ShortStorage(100)
  l.copy(t)
  d.copy(l)
  println(l)
  println(d)
  TensorFunc.IntInstance
  //  d.fill(0)

//  println(d.toString)
//  println(d)

//  val t = new Storage.IntStorage(10)
//  val s = new IntStorage(10)
//  s.fill(1)
//  println(s)
//  i.fill(30)
//  println(i)
//  println(Array[Double]().isInstanceOf[Array[Int]])
}
