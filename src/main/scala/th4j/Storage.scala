


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


import java.io.{BufferedWriter, PrintWriter}
import java.nio.file.Files

import com.sun.jna._
import th4j.util.Helper._

import scala.collection.IterableView
import scala.io.Source
import scala.language.experimental.macros
import th4j.func._
import th4j.generate._
import th4j.util._

import scala.reflect.ClassTag


@GenerateAllTypes("Factory", "", "0")
abstract class Storage[T <: AnyVal, U <: AnyVal, Z<:Device]{
  //abstract method that needed to be implemented
  protected def getOps():func.StorageFunc[T, U, Z]
  protected def getPointerOps():func.PointerFunc[T, U, Z]
  protected def getStorageCopyOps():func.StorageCopyFunc[T, U, Z]
  val ops= getOps()
  val ptrOps = getPointerOps()
  val copyOps = getStorageCopyOps()
  protected[th4j] var ptr = ops.Storage_new()

  /*--------------------------------------------*/
  //function with prefix `ctor' is treated differently
  //be cautious while overriding.
  protected def ctor1(size:Long){
    freePtr()
    ptr = ops.Storage_newWithSize(size)
  }

  protected def ctor2(arr:Array[T]){
    freePtr()
    ptr = ops.Storage_newWithSize(arr.length)
    copyOps.Storage_rawCopy(ptr, arr)
  }

  protected def ctor3(ref:Pointer) ={
    //This constructor is very dangerous and should not use normally.
    freePtr()
    ptr = ref
  }

  /*------------------------------------------*/
  def size() = ops.Storage_size(ptr)
  def indicies = 0L until size()
  def elementSize() = ops.Storage_elementSize()
  def get(idx:Long) = {ops.Storage_get(ptr, idx)}
  def apply(idx:Long) = get(idx)

  def set(idx:Long, value:T) = {ops.Storage_set(ptr, idx, value);this}
  def update(idx:Long, value:T) = set(idx, value)

  //I only come up with this solution, as type erasure erase all
  //type information
  def copy(src: Storage[_, _, _]) ={
    //Why I need to import? may be the macro messes something with the compiler.
    import th4j.Storage._

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

  def iterator(from:Long = 0)=IterateL(from, size()).map(get)

  def fill(value:T) = {ops.Storage_fill(ptr, value);this}
  override def toString:String={
    val sb = new StringBuilder()
    import th4j.util.BeautifulPrinter._
    print1d(iterator(),sb)
    sb ++= s"[${this.getClass.getSimpleName} of size ${size}]\n"
    sb.mkString
  }
  /*------------------------------------------*/
  //The following method is not safe to use for normal user
  def getRawData()={
    ops.Storage_data(ptr)
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


