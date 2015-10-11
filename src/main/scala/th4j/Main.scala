

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

import com.sun.jna._
import th4j.Storage._
import th4j.Tensor.{DoubleTensor, IntTensor}
import th4j.func._

import scala.util.{Random, Try}


object Main extends App {

//  val d = new DoubleStorage(10)
//  for {i<- 0 until 10}{
//    d(i) = Random.nextDouble() * 10
//    d(i) = -0.01
//  }
//  import th4j.util.BeautifulPrinter._
//  val sb = new StringBuilder
//  print2d((0 until 24).iterator, 3, 8, sb)
//  println(sb)
//  val arr = Array.ofDim[Double](12)
//  for{i<-0 until 3
//      j<-0 until 4}{
//    arr(i)(j) = i*4 + j
////    println(arr(i)(j))
//  }
  val arr = Array.tabulate[Double](3, 4, 5){case (i, j, k)=> i*20 + j*5 + k}
//  val arr = Array.tabulate[Double](12)(i=>i)
  val t = new DoubleTensor(arr)
//  t.iterator().foreach(println)
//  println(t.size(), t.stride(), t.storage())
//  println(t.storage())
//  val t = new DoubleTe
  // nsor(3, 4)
  println(t)
}

