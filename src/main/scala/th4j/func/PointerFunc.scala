

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

import com.sun.jna._
import th4j.Storage
import th4j.Storage._
import th4j.generate._




@GenerateAllTypes("Template","","","ptr.get%2$sArray(offset, size)", "ptr.write(offset, buf, index, length)" ,"new %1$sStorage(ptr)")
trait PointerFunc[T <: AnyVal, U<:AnyVal] {
  def Array(ptr: Pointer, offset:Long, size:Int) : Array[T]
  def Write(ptr: Pointer, offset:Long, buf:Array[T], index:Int, length:Int)
  def Storage(ptr: Pointer):Storage[T, U]
}

//object PointerFunc{
//  object IntInstance extends PointerFunc[Int]{
//    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getIntArray(offset, size)
//  }
//  object FloatInstance extends PointerFunc[Float]{
//    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getFloatArray(offset, size)
//  }
//  object ByteInstance extends PointerFunc[Byte]{
//    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getByteArray(offset, size)
//  }
//  object ShortInstance extends PointerFunc[Short]{
//    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getShortArray(offset, size)
//  }
//  object DoubleInstance extends PointerFunc[Double]{
//    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getDoubleArray(offset, size)
//  }
//  object LongInstance extends PointerFunc[Long]{
//    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getLongArray(offset, size)
//  }
//  object CharInstance extends PointerFunc[Char]{
//    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getCharArray(offset, size)
//  }
//
//  def getFloat() = FloatInstance
//  def getInt()  = IntInstance
//  def getChar() = CharInstance
//  def getLong() = LongInstance
//  def getDouble() = DoubleInstance
//  def getByte() = ByteInstance
//  def getShort() = ShortInstance
//}

