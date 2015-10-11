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


import sun.reflect.generics.reflectiveObjects.NotImplementedException
import th4j.Storage.LongStorage
import th4j.generate._
import scala.collection
import scala.collection.parallel.mutable
import scala.language.experimental.macros
import th4j.func._
import th4j.util.BeautifulPrinter._
import th4j.util.Helper._

import scala.util.Try

@GenerateAllTypes("Factory", "", "0")
abstract class Tensor [T<:AnyVal, U<:AnyVal]{
  protected def getOps():func.TensorFunc[T, U]
  protected def getPointerOps():func.PointerFunc[T, U]
  protected def getCopyOps():func.TensorCopyFunc[T, U]
  protected def getMathOps():func.TensorMathFunc[T, U]
  def getStorageOps():func.StorageFunc[T, U]
  val ops = getOps()
  val copyOps = getCopyOps()
  val ptrOps = getPointerOps()
  val storageOps = getStorageOps()
  def mathOps = getMathOps()
  protected [th4j] var ptr = ops.Tensor_new()

  /*------------------------------------------*/
  //function with prefix `ctor' is treated differently
  //be cautious while overriding
  //specifically, the macro will automatically generate
  //a auxiliary constructor for each ctor
  protected def ctor1(tensor: Tensor[T, U]) = {
    freePtr()
    ptr = ops.Tensor_newWithTensor(tensor.ptr)
  }

  protected def ctor2(size0: Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize1d(size0)
  }

  protected def ctor3(size0: Long, size1:Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize2d(size0, size1)
  }

  protected def ctor4(size0: Long, size1:Long, size2:Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize3d(size0, size1, size2)
  }

  protected def ctor5(size0: Long, size1:Long, size2:Long, size3:Long) = {
    freePtr()
    ptr = ops.Tensor_newWithSize4d(size0, size1, size2, size3)
  }

  protected def ctor6(size: LongStorage, stride: Storage[Long, _]) ={
    freePtr()
    ptr = ops.Tensor_newWithSize(size.ptr, Try(stride.ptr).getOrElse(null))
  }

  protected def ctor7(size: LongStorage) = {
    ctor6(size, null)
  }

  protected def ctor8(storage:Storage[T, U],
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

  protected def ctor9(storage:Storage[T, U])={
    ctor8(storage, 0, null, null)
  }

  protected def ctor10(storage:Storage[T, U],
             storageOffset:Long,
             sizes: Storage[Long, _]) ={
    ctor8(storage, storageOffset, sizes, null)
  }


  protected def ctor11(arr:Array[T]) ={
    buildTensorFromArray(arr)
  }

  protected def ctor12(arr:Array[Array[T]])={
    buildTensorFromArray(arr)
  }
  protected def ctor13(arr:Array[Array[Array[T]]])={
    buildTensorFromArray(arr)
  }

  protected def ctor14(arr:Array[Array[Array[Array[T]]]]) = {
    buildTensorFromArray(arr)
  }

  private def buildTensorFromArray (arr:Array[_]) = {
    /*this method is not opened, as it is not safe to use directly.
    * Specifically, the type checking
    * is too loose. */

    //(1). check if the array is legal array
    freePtr()
    val sizes = checkArray(arr)
    sizes match {
      case Some(ls) => {
        //this is a legal array
        //if this is empty, then
        val lastDim = ls.last
        if (lastDim == 0) ptr = ops.Tensor_new()
        else{
          //it is a non-empty tensor, build it recursively, firstly,
          // allocate a storage with appropiate size
          val size = ls.product
          var curOffset = 0L
          import Storage._
          val sizes = new LongStorage(ls.toArray)
          val es = storageOps.Storage_elementSize()
          ptr = ops.Tensor_newWithSize(sizes.ptr, null)
          val dataPtr = ops.Tensor_data(ptr)
          buildTensorRecursive(arr)

          def buildTensorRecursive(r: Array[_]):Unit = {
            if (r.isInstanceOf[Array[Array[_]]]) {
              r.asInstanceOf[Array[Array[_]]].foreach(buildTensorRecursive)
            }else{ //already checked, we can do whatever we want now
                ptrOps.Write(dataPtr,curOffset * es ,r.asInstanceOf[Array[T]],0, lastDim.toInt)
                curOffset = curOffset + lastDim
            }
          }
        }
      }
      case None =>
        throw new Exception("Illegal Array, check the type and check if each sub arrays is with same dimension")

    }
  }

  private def checkArray(arr:Array[_]):Option[List[Long]] = {
    //recursively check if an array is legal array
    arr match {
      case Array()=>
        Some(0 :: Nil)
      case arr:Array[_] if arr.isInstanceOf[Array[Array[_]]]=>
        //check recursively
        val lss = arr
          .asInstanceOf[Array[Array[_]]]
          .map(checkArray)

        if (lss.forall(p => p.isDefined & p == lss.head))
          Some(arr.length :: lss.head.get)//head must exist, other wise, previous guarding takes effect
        else
          None
      case arr:Array[Any]=>
        //reject any array
        None
      case arr:Array[_] =>
        //if this is not a nested array, then we have to verify that,
        //this array is with the same type with the current one
        val prefix = getPrefix()
        //then, check the real type in allTypes
        val real = allTypes(prefix)._1
        if (real != arr.head.getClass.getSimpleName)
          None
        else{
          //return the length of the array
          Some(arr.length :: Nil)
        }

    }


  }

  /*--------------------------------------------------*/

//Still not sure if it is a good way to go
//  def get(xs:Long*):T={
//    val nd = nDimension()
//    if(xs.length > nd) throw new Exception(s"Quering is out of bound on this ${nd}d tensors")
//    xs match {
//      case x0 :: Nil =>
//        ops.Tensor_get1d(ptr, x0)
//      case x0::x1::Nil =>
//        ops.Tensor_get2d(ptr, x0, x1)
//      case x0::x1::x2::Nil=>
//        ops.Tensor_get3d(ptr, x0, x1, x2)
//      case x0::x1::x2::x3::Nil=>
//        ops.Tensor_get4d(ptr, x0, x1, x2, x3)
//      case _=>
//        throw new NotImplementedException()
//    }
//  }
//  def apply(xs:Long*):T =  get(xs:_*)

//  def get()

  def iterator() = {

    val nd = nDimension()
    if (nd == 0)
      Iterator()
    else {
      //    val endDim = nDimension() - 1
      val storage = this.storage()
      val storageOffset = this.storageOffset()
      def iterateSub(curDim: Int, start: Long):Iterator[T] = {
        if (curDim == nd) {//impossible
          throw new Exception("Impossible")
        } else if (curDim == nd - 1) {
          IterateL(0, size(nd - 1)).map(i => storage(start + stride(nd - 1) * i))
        } else{
          IterateL(0L,size(curDim)).map(i=>{
//            aux.push(i * stride(curDim) + start)
            iterateSub(curDim + 1, i * stride(curDim) + start)
          }).flatten
        }
      }
      iterateSub(0, storageOffset)
    }
  }




  private def getPos(xs:Long*):Long = {
    //get the index of storage given the the list of coordinates
    assert(xs.length == nDimension())
    val sizes = size()
    val strides = stride()
    for{(x, idx)<-xs.zipWithIndex}{
      assert(sizes.get(idx) > x)
    }
    strides
      .iterator()
      .zip(xs.iterator)
      .foldLeft(storageOffset()){case (cum, (stride, x))=> cum + stride * x}
  }


  /*--------------------------------------------------*/
  def size(dim: Int):Long = {
    ops.Tensor_size(ptr, dim)
  }

  def size():LongStorage = {
    val ls = ops.Tensor_newSizeOf(ptr)
    new LongStorage(ls)
  }

  def storage():Storage[T, U] = {
    val storagePtr = ops.Tensor_storage(ptr)
    storageOps.Storage_retain(storagePtr)
    ptrOps.Storage(storagePtr)
  }

  def nDimension():Int={
    ops.Tensor_nDimension(ptr)
  }

  def stride(dim:Int):Long={
    ops.Tensor_stride(ptr, dim)
  }
  def stride():LongStorage = {
    val ls = ops.Tensor_newStrideOf(ptr)
    new LongStorage(ls)
  }
  def storageOffset():Long = {
    ops.Tensor_storageOffset(ptr)
  }
  def isContiguous():Boolean = {
    ops.Tensor_isContiguous(ptr) == 1
  }

  def isSize(targetSize: LongStorage):Boolean = {
    ops.Tensor_isSize(ptr, targetSize.ptr) == 1
  }
  def isSameSizeAs(targetTensor:Tensor[_, _]):Boolean = {
    ops.Tensor_isSameSizeAs(ptr, targetTensor.ptr) == 1
  }
  def nElement():Long = {
    ops.Tensor_nElement(ptr)
  }
  /*--------------------------------------------------*/
  def set(targetTensor:Tensor[T, U]): Tensor[T, U] ={
    ops.Tensor_set(ptr, targetTensor.ptr)
    this
  }
  def set(storage:Storage[T, U], storageOffset:Long, sizes:LongStorage, strides:LongStorage):Tensor[T, U] = {
    ops.Tensor_setStorage(ptr,
      storage.ptr,
      storageOffset,
      Try(sizes.ptr).getOrElse(null),
      Try(strides.ptr).getOrElse(null))
    this
  }
  def set(storage:Storage[T, U]):Tensor[T, U] = set(storage, 0, null, null)
  def set(storage:Storage[T, U], storageOffset:Long, sizes:LongStorage):Tensor[T, U] = set(storage, storageOffset, sizes, null)
  /*--------------------------------------------------*/
  def copy(src:Tensor[_, _]) = {
    import th4j.Tensor._
    src match {
      case src: IntTensor=>
        copyOps.Tensor_copyInt(ptr, src.ptr)
      case src: FloatTensor=>
        copyOps.Tensor_copyFloat(ptr, src.ptr)
      case src: ByteTensor =>
        copyOps.Tensor_copyByte(ptr, src.ptr)
      case src:CharTensor=>
        copyOps.Tensor_copyChar(ptr, src.ptr)
      case src:ShortTensor=>
        copyOps.Tensor_copyShort(ptr, src.ptr)
      case src:LongTensor=>
        copyOps.Tensor_copyLong(ptr, src.ptr)
      case src:DoubleTensor=>
        copyOps.Tensor_copyDouble(ptr, src.ptr)
      case _=>
        throw new Exception("Unknown type of tensor.")
    }
    this
  }

  def fill(value: T) = {
    mathOps.Tensor_fill(ptr, value)
    this
  }


  /*--------------------------------------------------*/
  override def toString():String={
    val sb = new StringBuilder
    printnd(this, sb)
    sb ++= s"[${this.getClass.getSimpleName} of size ${size().iterator().mkString("x")}]\n"
    sb.mkString
  }


  /*-----------------------------------------------------------*/

  val prefixR = """^(\w*?)Tensor$""".r

  def getPrefix():String = {
    this.getClass.getSimpleName match {
      case prefixR(prefix)=>
        prefix
      case _ =>
        ""
    }
  }
  /*-----------------------------------------------------------*/
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
