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
import th4j.generate._
import th4j.generate.GenerateAllTypes

/**
 * Created by et on 08/10/15.
 */
@GenerateAllTypes("Native", "TH", "TH")
trait TensorFunc [T<:AnyVal, U<:AnyVal] {

  //access func
  def Tensor_storage(self:Pointer):Pointer
  def Tensor_storageOffset(self:Pointer):Long
  def Tensor_nDimension(self:Pointer):Int
  def Tensor_size(self:Pointer, dim:Int):Long
  def Tensor_stride(self:Pointer, dim:Int):Long
  def Tensor_newSizeOf(self:Pointer):Pointer
  def Tensor_newStrideOf(self:Pointer):Pointer
  def Tensor_data(self:Pointer):Pointer
  def Tensor_setFlag(self:Pointer, flag:Char)
  def Tensor_clearFlag(self:Pointer, flag:Char)

  //creation func
  def Tensor_new():Pointer
  def Tensor_newWithTensor(tensor:Pointer):Pointer
  def Tensor_newWithStorage(storage:Pointer,
                            storageOffset:Long,
                            size:Pointer,
                            stride:Pointer):Pointer
  def Tensor_newWithStorage1d(storage:Pointer,
                              storageOffset:Long,
                              size0:Long,
                              stride0:Long):Pointer
  def Tensor_newWithStorage2d(storage:Pointer,
                              storageOffset:Long,
                              size0:Long, stride0:Long,
                              size1:Long, stride1:Long):Pointer
  def Tensor_newWithStorage3d(storage:Pointer,
                              storageOffset:Long,
                              size0:Long, stride0:Long,
                              size1:Long, stride1:Long,
                              size2:Long, stride2:Long):Pointer
  def Tensor_newWithStorage4d(storage:Pointer,
                              storageOffset:Long,
                              size0:Long, stride0:Long,
                              size1:Long, stride1:Long,
                              size2:Long, stride2:Long,
                              size3:Long, stride3:Long):Pointer


  def Tensor_newWithSize(size:Pointer,
                         stride:Pointer):Pointer

  def Tensor_newWithSize1d(size0:Long):Pointer
  def Tensor_newWithSize2d(size0:Long, size1:Long):Pointer
  def Tensor_newWithSize3d(size0:Long, size1:Long, size2:Long):Pointer
  def Tensor_newWithSize4d(size0:Long, size1:Long, size2:Long, size3:Long):Pointer


  def Tensor_newClone(self:Pointer):Pointer
  def Tensor_newContiguous(tensor:Pointer):Pointer
  def Tensor_newSelect(tensor:Pointer, dimension:Int, sliceIndex:Long):Pointer
  def Tensor_newNarrow(tensor:Pointer, dimension:Int, firstIndex:Long, size:Long):Pointer
  def Tensor_newTranspose(tensor:Pointer, dimension1:Int, dimension2:Int):Pointer
  def Tensor_newUnfold(tensor:Pointer, dimension:Int, size:Long, step:Long):Pointer

  //resizing
  def Tensor_resize(tensor: Pointer, size: Pointer, stride: Pointer)
  def Tensor_resizeAs(tensor: Pointer, src: Pointer)
  def Tensor_resize1d(tensor: Pointer, size0:Long)
  def Tensor_resize2d(tensor: Pointer, size0:Long, size1:Long)
  def Tensor_resize3d(tensor: Pointer, size0:Long, size1:Long, size2:Long)
  def Tensor_resize4d(tensor: Pointer, size0:Long, size1:Long, size2:Long, size3:Long)
  def Tensor_resize5d(tensor: Pointer, size0:Long, size1:Long, size2:Long, size3:Long, size4:Long)

  //setting storage
  def Tensor_set(self:Pointer, src:Pointer)
  def Tensor_setStorage(self:Pointer,
                        storage:Pointer,
                        storageOffset:Long,
                        size:Pointer,
                        stride:Pointer)

  def Tensor_setStorage1d(self:Pointer, storage:Pointer, storageOffset:Long,
                          size0:Long, stride0:Long)
  def Tensor_setStorage2d(self:Pointer, storage:Pointer, storageOffset:Long,
                          size0:Long, stride0:Long,
                          size1:Long, stride1:Long)
  def Tensor_setStorage3d(self:Pointer, storage:Pointer, storageOffset:Long,
                          size0:Long, stride0:Long,
                          size1:Long, stride1:Long,
                          size2:Long, stride2:Long)
  def Tensor_setStorage4d(self:Pointer, storage:Pointer, storageOffset:Long,
                          size0:Long, stride0:Long,
                          size1:Long, stride1:Long,
                          size2:Long, stride2:Long,
                          size3:Long, stride3:Long)


  def Tensor_narrow(self:Pointer, src:Pointer, dimension:Int, firstIndex:Long, size:Long)
  def Tensor_select(self:Pointer, src:Pointer, dimension:Int, sliceIndex:Long)
  def Tensor_transpose(self:Pointer, src:Pointer, dimension1:Int, dimension2:Int)
  def Tensor_unfold(self:Pointer, src:Pointer, dimension:Int, size:Long, step:Long)

  def Tensor_squeeze(self:Pointer, src:Pointer)
  def Tensor_squeeze1d(self:Pointer, src:Pointer, dimension:Int)

  def Tensor_isContiguous(self:Pointer):Int
  def Tensor_isSameSizeAs(self:Pointer, src:Pointer):Int
  def Tensor_isSize(self:Pointer, dims:Pointer):Int
  def Tensor_nElement(self:Pointer):Long

  def Tensor_retain(self:Pointer)
  def Tensor_free(self:Pointer)
  def Tensor_freeCopyTo(self:Pointer, dst:Pointer)


  def Tensor_set1d(tensor:Pointer, x0:Long, value:T)
  def Tensor_set2d(tensor:Pointer, x0:Long, x1:Long, value:T)
  def Tensor_set3d(tensor:Pointer, x0:Long, x1:Long, x2:Long, value:T)
  def Tensor_set4d(tensor:Pointer, x0:Long, x1:Long, x2:Long, x3:Long, value:T)

  def Tensor_get1d(tensor:Pointer, x0:Long):T
  def Tensor_get2d(tensor:Pointer, x0:Long, x1:Long):T
  def Tensor_get3d(tensor:Pointer, x0:Long, x1:Long, x2:Long):T
  def Tensor_get4d(tensor:Pointer, x0:Long, x1:Long, x2:Long, x3:Long):T
}
