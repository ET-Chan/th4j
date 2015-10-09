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

/**
 * Created by et on 09/10/15.
 */

import th4j.generate._
import scala.language.experimental.macros
import th4j.func

import scala.util.Try


abstract class Tensor [T<:AnyVal, U<:AnyVal]{
  def getOps():func.TensorFunc[T, U]
  def getPointerOps():func.PointerFunc[T]

  val ops = getOps()

  protected [th4j] var ptr = ops.Tensor_new()

  /*------------------------------------------*/
  //function with prefix `ctor' is treated differently
  //be cautious while overriding
  //specifically, the macro will automatically generate
  //a auxiliary constructor for each ctor
  def ctor1(tensor: Tensor[T, U]) = {
    freePtr()
    ptr = ops.Tensor_newWithTensor(tensor.ptr)
  }

  def ctor2(size0: Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize1d(size0)
  }

  def ctor3(size0: Long, size1:Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize2d(size0, size1)
  }

  def ctor4(size0: Long, size1:Long, size2:Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize3d(size0, size1, size2)
  }

  def ctor5(size0: Long, size1:Long, size2:Long, size3:Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize4d(size0, size1, size2, size3)
  }

  def ctor6(size: Storage[Long, _], stride: Storage[Long, _]) ={
    freePtr()
    ptr = ops.Tensor_newWithSize(size.ptr, Try(stride.ptr).getOrElse(null))
  }

  def ctor7(size: Storage[Long, _]) = {
    ctor6(size, null)
  }

  def ctor8(storage:Storage[T, U],
            storageOffset:Long,
            size: Storage[Long, _],
            stride: Storage[Long, _]) = {
    freePtr()
    ptr = ops.Tensor_newWithStorage(
      storage.ptr,
      storageOffset,
      Try(size.ptr).getOrElse(null),
      Try(stride.ptr).getOrElse(null)
    )
  }

  def ctor9(storage:Storage[T, U])={
    ctor8(storage, 0, null, null)
  }

  def ctor10(storage:Storage[T, U],
             storageOffset:Long,
             sizes: Storage[Long, _]) ={
    ctor8(storage, storageOffset, sizes, null)
  }


  def ctor11(arr:Array[T]) ={
    freePtr()
  }

  def ctor12(arr:Array[Array[T]])={
    freePtr()
  }
  def ctor13(arr:Array[Array[Array[T]]])={
    freePtr()
  }

  def ctor14(arr:Array[Array[Array[Array[T]]]]) = {
    freePtr()
  }

  private def buildTensorFromArray (arr:Array[_]) = {
    /*this method is not opened, as it is not safe to use directly.
    * Specifically, the type checking
    * is too loose. */

    //(1). check if the array is legal array
    val size = checkArray(arr)
    //wait for impl.
  }

  private def checkArray(arr:Array[_]):Option[List[Long]] = {
    //recursively check if an array is legal array
//    if (arr)
    arr match {
      case Array()=>
        Some(0 :: Nil)
      case arr:Array[Array[_]]=>
      case _ =>

    }

    None
  }

  override def finalize(): Unit ={
    freePtr()
    super.finalize()
  }

  /*Only for internal use*/
  protected def freePtr() ={
    ops.Tensor_free(ptr)
    ptr = null
  }
}
