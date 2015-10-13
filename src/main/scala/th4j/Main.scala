

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
import th4j.Tensor.{FloatTensor, DoubleTensor, IntTensor}
import th4j.func._
import th4j.util._
import scala.util.{Random, Try}

object Main extends App {
  val x = new FloatTensor(5, 6).zero()
  val y = x.select(0, 1).fill(2)
  println(y)
  println(x)
  val z = x.select(1, 4).fill(5)
  println(z)
  println(x)
}
