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

package th4j.func

import th4j.generate._
import th4j.util._
/**
 * Created by et on 15/10/15.
 */
@GenerateAllTypes("Template","","",
  "(lhs + rhs).asInstanceOf[%2$s]",
  "(lhs + rhs).asInstanceOf[%3$s]",
  "(lhs * rhs).asInstanceOf[%2$s]",
  "(lhs * rhs).asInstanceOf[%3$s]",
  "0.asInstanceOf[%2$s]",
  "0.asInstanceOf[%3$s]")
trait ValFunc[T<:AnyVal, U<:AnyVal, Z<:Device] {
  def Plus(lhs:T, rhs: Int):T
  def APlus(lhs:U, rhs:Int):U
  def Mul(lhs:T, rhs:Int):T
  def AMul(lhs:U, rhs:Int):U
  def Zero():T
  def AZero():U
  def One() = Plus(Zero(), 1)
  def AOne() = APlus(AZero(), 1)
}
