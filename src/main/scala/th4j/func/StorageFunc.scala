

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

import th4j.generate.GenerateAllTypes
import th4j.util._

import scala.language.experimental.macros
import th4j.generate._
import com.sun.jna._

@GenerateAllTypes("Native", "TH", "TH")
trait StorageFunc[T <: AnyVal, U <: AnyVal, Z<:Device] {
  def Storage_data(self:Pointer):Pointer
  def Storage_size(self:Pointer):Long
  def Storage_elementSize():Long
  def Storage_get(self:Pointer, idx:Long):T
  def Storage_set(self:Pointer, idx:Long, value:T)
  def Storage_new():Pointer
  def Storage_newWithSize(size:Long):Pointer
  def Storage_newWithAllocator(size:Long, allocator:Pointer, allocatorContext:Pointer):Pointer
//  def Storage_newWithMapping(filename:String, size:Long, shared:Int):Pointer
//  def Storage_newWithSize1(data0:T):Pointer
//  def Storage_newWithSize2(data0:T, data1:T):Pointer
//  def Storage_newWithSize3(data0:T, data1:T, data2:T):Pointer
//  def Storage_newWithSize4(data0:T, data1:T, data2:T, datat3:T):Pointer
//  def Storage_setFlag(storage:Pointer, flag:Byte):Unit
//  def Storage_clearFlag(storage:Pointer, flag:Byte):Unit
  def Storage_retain(storage:Pointer):Unit
  def Storage_free(storage:Pointer):Unit
//  def Storage_newWithData(data: Array[T], size: Long):Pointer <--this is problematic
//  def Storage_newWithDataAndAllocator(data: Array[T], size:Long, allocator:Pointer, allocatorContext:Pointer):Pointer
//  def Storage_resize(storage:Pointer, size:Long)
  def Storage_fill(storage:Pointer, value:T):Unit
}



//@GenerateType("THInt") object Test extends StorageFunc[Int, Long]

