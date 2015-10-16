

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
import th4j.Tensor._
import th4j.func._
import th4j.util._
import scala.util.{Random, Try}

object Main extends App {
  val x = new FloatTensor().range(0, 4).resize(1, 4)
  new FloatTensor(4, 5).rand()
  println(x)
  val mask = new ByteTensor(2, 2).bernoulli()
  println(mask)
  x.maskedFill(mask, -1)
  println(x)
}
