


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

import th4j.func
import th4j.generate.GenerateAllTypes

import scala.language.experimental.macros
import th4j.func._
import th4j.generate._
import scala.NotImplementedError

@GenerateAllTypes("Factory", "", "0")
abstract class Storage[T <: AnyVal, U <: AnyVal]{
  //abstract method that needed to be implemented
  def getOps():func.StorageFunc[T, U]
  def getPointerOps():func.PointerFunc[T]
  def getStorageCopyOps():func.StorageCopyFunc[T, U]
  val ops= getOps()
  val ptrOps = getPointerOps()
  val copyOps = getStorageCopyOps()
  protected[Storage] var ptr = ops.Storage_new()

  /*--------------------------------------------*/
  //function with prefix `ctor' is treated differently
  //be cautious while overriding.
  def ctor1(size:Long){
    freePtr()
    ptr = ops.Storage_newWithSize(size)
  }

  def ctor2(arr:Array[T]){
    freePtr()
    ptr = ops.Storage_newWithData(arr, arr.length)
  }

  /*------------------------------------------*/
  def size() = ops.Storage_size(ptr)

  def get(idx:Long) = {ops.Storage_get(ptr, idx);this}
  def apply = get _

  def set(idx:Long, value:T) = {ops.Storage_set(ptr, idx, value);this}
  def update = set _

  //I only come up with this solution, as type erasure erase all
  //type information
  def copy(src: Storage[_, _]) ={
    //Why I need to import? may be the macro messes something with the compiler.
    import jth.Storage._

    src match  {
      case src: IntStorage=>
        copyOps.Storage_copyInt(ptr, src.ptr)

      case src: FloatStorage=>
        copyOps.Storage_copyFloat(ptr, src.ptr)

      case src: ByteStorage =>
        copyOps.Storage_copyByte(ptr, src.ptr)

      case src:CharStorage=>
        copyOps.Storage_copyChar(ptr, src.ptr)
      case src:ShortStorage=>
        copyOps.Storage_copyShort(ptr, src.ptr)
      case src:LongStorage=>
        copyOps.Storage_copyLong(ptr, src.ptr)
      case src:DoubleStorage=>
        copyOps.Storage_copyDouble(ptr, src.ptr)
      case _=>
        throw new Exception("Unknown type of storage.")

    }
    this
  }

  def copy(src:Array[T])={
    copyOps.Storage_rawCopy(ptr, src)
    this
  }

  def fill(value:T) = {ops.Storage_fill(ptr, value);this}
  override def toString:String={
    val sz = size().toInt
    val str = ptrOps.Array(ops.Storage_data(ptr), 0, sz).mkString("\n")
    s"$str\n[${this.getClass.getSimpleName} of size $sz]"
  }
  /*------------------------------------------*/


  override def finalize(){
    freePtr()
    super.finalize()
  }
  /*Only for internal use*/
  protected def freePtr(){
    ops.Storage_free(ptr)
    ptr = null
  }

//  val ops = StorageFuncsCollection.col(combineTypeTags[T, U]()).asInstanceOf[StorageFunc[T, U]]
}


