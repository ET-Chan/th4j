
//import StorageFunc
//
///**
// * Created by et on 23/09/15.
// */
//class Storage[T] {
//  //actual storage holder
////  val holderPtr:Long
////  val func = implicitly[StorageFunc[T]]
//  import func._
//
//
//}
package jth

//do I need this ?



import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import jth.util.MacroHelper._
import jth.func.{StorageCopyFunc, StorageFunc, PointerFunc}
import jth.generate._
import scala.NotImplementedError

@GenerateAllTypes("Factory", "", "0")
abstract class Storage[T <: AnyVal, U <: AnyVal]{
  //abstract method that needed to be implemented
  def getOps():StorageFunc[T, U]
  def getPointerOps():PointerFunc[T]
  def getStorageCopyOps():StorageCopyFunc[T, U]
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
//
  def ctor3(t:Storage[T, U], offset:Int, size:Int)={
    /*Is this constructor really worthwhile?
    * Are people normally want to take a partial view on the storage?
    * */
    throw new NotImplementedError
  }

  def ctor4(t:Storage[T, U]) = ctor3 (t, 0, 0)

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
      case _=>{
        throw new Exception("Unknown type of storage.")
      }
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


