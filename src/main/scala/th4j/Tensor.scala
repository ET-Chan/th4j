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


import com.sun.jna.Pointer
import th4j.generate._
import th4j.Storage._
import th4j.util.SingletonPair

import scala.language.experimental.macros
import th4j.func._
import th4j.util.BeautifulPrinter._
import th4j.util.Helper._

import scala.util.Try
import th4j.util._


@GenerateAllTypes("Factory", "", "0")
abstract class Tensor [T<:AnyVal, U<:AnyVal, Z<:Device]{
//  implicit def array2LongStorage(arr:Array[Long]):LongStorage = {
//    new LongStorage(arr)
//  }

  protected def getOps():func.TensorFunc[T, U, Z]
  protected def getPointerOps():func.PointerFunc[T, U, Z]
  protected def getCopyOps():func.TensorCopyFunc[T, U, Z]
  protected def getMathOps():func.TensorMathFunc[T, U, Z]
  protected def getRandomOps():func.TensorRandomFunc[T, U, Z]
  protected def getValOps():func.ValFunc[T, U, Z]
  protected def getStorageOps():func.StorageFunc[T, U, Z]
  val ops = getOps()
  val copyOps = getCopyOps()
  val ptrOps = getPointerOps()
  val storageOps = getStorageOps()
  val randomOps = getRandomOps()
  val valOps = getValOps()
  val mathOps = getMathOps()
  protected [th4j] var ptr = ops.Tensor_new()


  /*------------------------------------------*/
  //function with prefix `ctor' is treated differently
  //be cautious while overriding
  //specifically, the macro will automatically generate
  //a auxiliary constructor for each ctor

  //existential type i.e. this.type does not work well
  //with current macro paradise. This type signature
  //will be change to this.type once paradise handles
  //this better.
  protected def ctor1(tensor: Tensor[T, U, Z]) = {
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

  protected def ctor6(size: LongStorage, stride: LongStorage) ={
    freePtr()
    ptr = ops.Tensor_newWithSize(size.ptr, Try(stride.ptr).getOrElse(null))
  }

  protected def ctor7(size: LongStorage) = {
    ctor6(size, null)
  }


  protected def ctor8(storage:Storage[T, U, Z],
            storageOffset:Long,
            size: LongStorage,
            stride: LongStorage) = {
    freePtr()
    ptr = ops.Tensor_newWithStorage(
      storage.ptr,
      storageOffset,
      Try(size.ptr).getOrElse(null),
      Try(stride.ptr).getOrElse(null)
    )
  }

  protected def ctor9(storage:Storage[T, U, Z])={
    ctor8(storage, 0, null, null)
  }

  protected def ctor10(storage:Storage[T, U, Z],
             storageOffset:Long,
             sizes: LongStorage) ={
    ctor8(storage, storageOffset, sizes, null)
  }


  protected def ctor11(arr:Array[T]):Unit ={
    buildTensorFromArray(arr)
  }

  protected def ctor12(arr:Array[Array[T]]):Unit ={
    buildTensorFromArray(arr)
  }
  protected def ctor13(arr:Array[Array[Array[T]]]):Unit ={
    buildTensorFromArray(arr)
  }

  protected def ctor14(arr:Array[Array[Array[Array[T]]]]):Unit  = {
    buildTensorFromArray(arr)
  }

  //This method is very dangeous and advised not to use.
  protected def ctor15(srcPtr: Pointer) = {
    freePtr()
    ptr = srcPtr
  }
  def create():this.type = {
    ptrToTensor(ops.Tensor_new())
  }

  protected def ptrToTensor(ptr:Pointer):this.type = {
    ptrOps.Tensor(ptr).asInstanceOf[this.type]
  }


  private def buildTensorFromArray (arr:Array[_]):Unit = {
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

  def storage():Storage[T, U, Z] = {
    val storagePtr = ops.Tensor_storage(ptr)
    storageOps.Storage_retain(storagePtr)
    ptrOps.Storage(storagePtr)
  }

  def unary_~ = size()


/*----------------------------------------------------*/
  def nDimension():Int={
    ops.Tensor_nDimension(ptr)
  }

  def dim() = nDimension()

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
  def contiguous():this.type={
    ptrToTensor(ops.Tensor_newContiguous(ptr))
  }

  def isSize(targetSize: LongStorage):Boolean = {
    ops.Tensor_isSize(ptr, targetSize.ptr) == 1
  }
  def isSameSizeAs(targetTensor:Tensor[_, _, _]):Boolean = {
    ops.Tensor_isSameSizeAs(ptr, targetTensor.ptr) == 1
  }
  def nElement():Long = {
    ops.Tensor_nElement(ptr)
  }
  override def clone():this.type = {
    ptrToTensor(ops.Tensor_newClone(ptr))
  }
  /*--------------------------------------------------*/
  def set(targetTensor:this.type): this.type ={
    ops.Tensor_set(ptr, targetTensor.ptr)
    this
  }
  def set(storage:Storage[T, U, Z], storageOffset:Long, sizes:LongStorage, strides:LongStorage):this.type = {
    ops.Tensor_setStorage(ptr,
      storage.ptr,
      storageOffset,
      Try(sizes.ptr).getOrElse(null),
      Try(strides.ptr).getOrElse(null))
    this
  }
  def set(storage:Storage[T, U, Z]):this.type = set(storage, 0, null, null)
  def set(storage:Storage[T, U, Z], storageOffset:Long, sizes:LongStorage):this.type = set(storage, storageOffset, sizes, null)
  def set(storage:Storage[T, U, Z], storageOffset:Long, szAndSt: Long*):this.type={
    //check the length of szAndSt
    if (szAndSt.isEmpty) set(storage, storageOffset, null, null)
    else {
      val argLen = szAndSt.length
      val paddedSzAndSt = if (argLen % 2 != 0) szAndSt ++ List(-1L) else szAndSt
      val (sizes, strides) = paddedSzAndSt.zipWithIndex.partition{case(_,i)=>i%2==0}
      set(storage, storageOffset, new LongStorage(sizes.map(_._1).toArray), new LongStorage(strides.map(_._1).toArray))
    }
  }

  def resizeAs(src:this.type)={
    ops.Tensor_resizeAs(ptr, src.ptr)
    this
  }
  def resize(sizes:LongStorage)={
    ops.Tensor_resize(ptr, sizes.ptr, null)
  }
  def resize(sizes:LongStorage, strides:LongStorage) ={
    ops.Tensor_resize(
      ptr,
      sizes.ptr,
      Try(strides.ptr).getOrElse(null))
    this
  }
  def resize(size0:Long)={
    ops.Tensor_resize1d(ptr, size0)
    this
  }
  def resize(size0:Long, size1:Long)={
    ops.Tensor_resize2d(ptr, size0, size1)
    this
  }
  def resize(size0:Long, size1:Long, size2:Long)={
    ops.Tensor_resize3d(ptr, size0, size1, size2)
  }
  def resize(size0:Long, size1:Long, size2:Long, size3:Long)={
    ops.Tensor_resize4d(ptr, size0, size1, size2, size3)
  }
  def resize(size0:Long, size1:Long, size2:Long, size3:Long, size4:Long)={
    ops.Tensor_resize5d(ptr, size0, size1, size2, size3, size4)
  }


  /*--------------------------------------------------*/


  /*--------------------------------------------------*/
  //Random utility, they are only opened to certain subtype,
  //as specified in th4j.package.scala

  import th4j.Generator._

  protected def rand(): this.type ={
    mathOps.Tensor_rand(ptr, DefaultGenerator.ptr, size().ptr)
    this
  }

  //n => normal gaussian
  protected def randn(): this.type ={
    mathOps.Tensor_rand(ptr, DefaultGenerator.ptr, size().ptr)
    this
  }

  def bernoulli(p:Double = 0.5) :this.type= {
    randomOps.Tensor_bernoulli(ptr, DefaultGenerator.ptr, p)
    this
  }

  //exclusive range
  //in scala, everything is left inclusive, right exclusive
  def range(xmin:U, xmax:U, step:U) :this.type= {
    mathOps.Tensor_range(ptr, xmin, valOps.APlus(xmax, -1), step)
    this
  }

  def range(xmin:U, xmax:U):this.type = {
    range(xmin, xmax, valOps.APlus(valOps.AZero(), 1))
  }



  import th4j.Tensor._
  /*--------------------------------------------------*/
  def narrow(dim:Int, index:Long, size:Long)={
    ptrToTensor(ops.Tensor_newNarrow(ptr, dim, index, size))
  }

  def select(dim:Int, index:Long)={
    ptrToTensor(ops.Tensor_newSelect(ptr, dim, index))
  }

  def index(dim:Int, index:LongTensor):this.type={
    val tensorPtr = ops.Tensor_new()
    mathOps.Tensor_indexSelect(tensorPtr, ptr, dim, index.ptr)
    ptrToTensor(tensorPtr)
  }
  def index(src:this.type, dim:Int, index:LongTensor):this.type={
    mathOps.Tensor_indexSelect(ptr, src.ptr, dim, index.ptr)
    this
  }

  def indexCopy(dim:Int, index: LongTensor, tensor: this.type):this.type={
    mathOps.Tensor_indexCopy(ptr, dim, index.ptr, tensor.ptr)
    this
  }

  def indexFill(dim:Int, index: LongTensor, value: T):this.type ={
    mathOps.Tensor_indexFill(ptr, dim, index.ptr, value)
    this
  }


  def alignLongTensor(index:LongTensor) = new LongTensor(index.iterator().map(_ + 1).toArray).viewAs(index)

  def gather(dim:Int, index:LongTensor) ={
    val resPtr = ops.Tensor_newWithSize(index.size().ptr, null)
    //the following is to adapt TH 1-started index scheme
    //in scala, everything *is* 0 to size exclusive.
    val addedIndex = alignLongTensor(index)
    mathOps.Tensor_gather(resPtr, ptr, dim, addedIndex.ptr)
    ptrToTensor(resPtr)
  }

  def gather(src:this.type, dim:Int, index:LongStorage):this.type = {
    mathOps.Tensor_gather(ptr, src.ptr, dim, index.ptr)
    this
  }

  def scatter(dim:Int, index: LongTensor, src:this.type):this.type = {
    val addedIndex = alignLongTensor(index)
    mathOps.Tensor_scatter(ptr, dim, addedIndex.ptr, src.ptr)
    this
  }

  def scatter(dim:Int, index:LongTensor, value:T):this.type={
    val addedIndex = alignLongTensor(index)
    mathOps.Tensor_scatterFill(ptr, dim, addedIndex.ptr, value)
    this
  }
  /*------------------------------------------------------------*/
  def copy(src:Tensor[_, _, _]):this.type = {

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

  def fill(value: T):this.type = {
    mathOps.Tensor_fill(ptr, value)
    this
  }

  def zero():this.type = {
    mathOps.Tensor_zero(ptr)
    this
  }
  /*--------------------------------------------------*/

  def maskedSelect(mask: ByteTensor):this.type ={
    val tensorPtr = ops.Tensor_new()
    mathOps.Tensor_maskedSelect(tensorPtr, ptr, mask.ptr)
    ptrToTensor(tensorPtr)
  }

  def maskedSelect(src: this.type, mask:ByteTensor):this.type={
    mathOps.Tensor_maskedSelect(ptr, src.ptr, mask.ptr)
    this
  }

  def maskedCopy(mask:ByteTensor, src: this.type):this.type ={
    mathOps.Tensor_maskedCopy(ptr, mask.ptr, src.ptr)
    this
  }

  def maskedFill(mask:ByteTensor, value:T):this.type = {
    mathOps.Tensor_maskedFill(ptr, mask.ptr, value)
    this
  }


  def update(ranges:List[(Long, Long)], src:Tensor[_,_, _]):this.type = {
    val sub = get(ranges)
    sub.copy(src)
  }

  def update(ranges:List[(Long, Long)], value:T):this.type = {
    val sub = get(ranges)
    sub.fill(value)
  }
  def get(ranges:List[(Long, Long)]):this.type = {
    val nd = nDimension()
    //sanity check

    if (ranges.length > nd)
      throw new Exception(s"Ranges dimension exceed the tensor's dimension: $nd")
    val data = storage()
    //check if it is tensor subtraction mode or selecting mode
    val res = ops.Tensor_new()

    val sizes = size()
    if(ranges.forall(_.isInstanceOf[SingletonPair])){
      //selection mode
      if (ranges.length == nd){
          ops.Tensor_setStorage1d(res, ops.Tensor_storage(ptr), getPos(ranges.map(_._1):_*),1, 1)
      }else {
        ops.Tensor_set(res, ptr)
        ranges
          .zip(sizes.iterator().toList)
          .foreach { case ((start, _), _) => {
            ops.Tensor_select(res, null, 0, start)
          }}
      }
    }else{//subtraction mode, ndimension is the same
      ops.Tensor_set(res, ptr)

      ranges.zipWithIndex
      .foreach{
        case ((start, end), index)=>{
          val size = sizes(index)
          val posStart = if(start < 0) start + size + 1 else start
          val posEnd = if (end < 0) end + size + 1 else end
          ops.Tensor_narrow(res, null, index, posStart, posEnd - posStart)
        }}
    }

    ptrToTensor(res)
  }

  def get(mask:ByteTensor):this.type = {
    maskedSelect(mask)
  }

  def apply(ranges:List[(Long, Long)]) = get(ranges)
  def apply(mask:ByteTensor) = maskedSelect(mask)

  /*--------------------------------------------------*/
  def nonzero():LongTensor ={
    val subscript = new LongTensor()
    nonzero(subscript)
  }
  def nonzero(subscript:LongTensor):LongTensor = {
    mathOps.Tensor_nonzero(subscript.ptr, ptr)
    subscript
  }
  /*--------------------------------------------------*/

  def expand(sizes:Long*):this.type = {
    val result = ptrToTensor(ops.Tensor_new())
    expand(result, sizes:_*)
  }

  def expand(result:this.type, sizes:Long*):this.type={
    //sanity check
    val _nd = nDimension()
    if (sizes.length != _nd){
      throw new Exception("")
    }
    val _strides = stride()
    val _sizes = size()


    for{i<- 0 until _nd}{
      if (_sizes(i) == 1){
        _sizes(i) = sizes(i)
        _strides(i) = 0
      }else if (_sizes(i) != sizes(i))
        throw new Exception("Incorrect size: only singleton dimension can be expanded")
    }
    result.set(storage(),storageOffset(),_sizes, _strides)
  }

  def expand(sizes:LongStorage):this.type = {
   expand(sizes.iterator().toList:_*)
  }

  def expand(result: this.type, sizes:LongStorage):this.type = {
    expand(result, sizes.iterator().toList:_*)
  }

  def expandAs(result:this.type, tensor: this.type)={
    expand(result,tensor.size())
  }

  def expandAs(tensor: this.type) = expand(tensor.size())

  def squeeze():this.type ={
    val tensorPtr = ops.Tensor_new()
    ops.Tensor_squeeze(tensorPtr, ptr)
    ptrToTensor(tensorPtr)
  }

  def squeeze(dim:Int):this.type = {
    val tensorPtr = ops.Tensor_new()
    ops.Tensor_squeeze1d(tensorPtr, ptr, dim)
    ptrToTensor(tensorPtr)
  }

  def viewAs(template: Tensor[_,_,_]):this.type = {
    view(template.size())
  }

  def view(sizes:Long*):this.type={
    view(new LongStorage(sizes.toArray))
  }
  def view(result: this.type, sizes:Long*):this.type={
    view(result, new LongStorage(sizes.toArray))
  }
  def view(sizes:LongStorage):this.type={
    val res = create()
    view(res, sizes)
  }
  def view(result:this.type, sizes:LongStorage): this.type ={
    val origElements = nElement()

    //sanity check
    val (negDim, accElem) = size()
      .iterator()
      .zipWithIndex
    .foldLeft((None:Option[Int], 1L)){
      case ((negDim, acc), (size, curDim))=>{
        if (size < 0 ){
          if (negDim.isDefined) throw new Exception("Only allowed one dimension with negative value")
          (Some(curDim), acc)
        }else
          (None, acc * size)
    }}

    negDim match {
      case None=>
        assert(origElements == accElem)
        //do nothing
      case Some(dim) =>{
        assert(origElements % accElem == 0)
        sizes(dim) = origElements / accElem
      }
    }
    assert(isContiguous())
    result.set(storage(),storageOffset(), sizes)
  }

  def transpose(dim1: Int, dim2:Int):this.type={
      ptrToTensor(
        ops.Tensor_newTranspose(ptr, dim1, dim2))
  }

  def t():this.type={
    assert(nDimension() == 2)
    transpose(0, 1)
  }

  def permute(dim:Int*):this.type={
    val nd = nDimension()
    assert(dim.length == nd)
    val aux = dim.toArray
    val res = ops.Tensor_new()
    ops.Tensor_set(res, ptr)

    for{i<- 0 until nd}{
      var j = 0
      if(aux(i) != i & aux(i) >= 0){
        j = i
        while (i!=aux(j)){
          assert(0<=aux(j) & aux(j)<nd)
          ops.Tensor_transpose(res, null, j, aux(j))
          val (a1, a2) = (aux(j), -1);j = a1;aux(j) = a2
        }
        aux(j) = j
      }
    }
    ptrToTensor(res)
  }

  def unfold(dim:Int, size:Long, step:Long):this.type={
    ptrToTensor(
      ops.Tensor_newUnfold(ptr, dim, size, step))
  }
  /*--------------------------------------------------*/
  override def toString():String={
    val sb = new StringBuilder
    printnd(this, sb)
    val dims = if (nDimension() == 0) "0" else size().iterator().mkString("x")
    sb ++= s"[${typeName()} of size $dims]\n"
    sb.mkString
  }
  val selfName = this.getClass.getSimpleName
  def typeName():String = selfName


  /*-----------------------------------------------------------*/

  val prefixR = """^(\w*?)Tensor$""".r

  def getPrefix():String = {
    typeName() match {
      case prefixR(prefix)=>
        prefix
      case _ =>
        ""
    }
  }


  /*-----------------------------------------------------------*/
  def mul(res:this.type, value:T):this.type = {
    mathOps.Tensor_mul(res.ptr, ptr, value)
    res
  }
  def mul(value:T):this.type = {
    val res = create()
    mul(res, value)
  }
  //inplace
  def floor()={

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
