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
import scala.annotation.StaticAnnotation
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import com.sun.jna._
import th4j.generate._

/**
 * Created by et on 10/10/15.
 */
@GenerateAllTypes("Native", "TH", "TH")
trait TensorCopyFunc[T<:AnyVal, U<:AnyVal] {
  def Tensor_copy(tensor:Pointer, src: Pointer)
  def Tensor_copyByte(tensor:Pointer, src: Pointer)
  def Tensor_copyChar(tensor:Pointer, src: Pointer)
  def Tensor_copyShort(tensor:Pointer, src: Pointer)
  def Tensor_copyInt(tensor:Pointer, src: Pointer)
  def Tensor_copyLong(tensor:Pointer, src: Pointer)
  def Tensor_copyFloat(tensor:Pointer, src: Pointer)
  def Tensor_copyDouble(tensor:Pointer, src: Pointer)
}
