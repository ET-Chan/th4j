
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

import scala.reflect.runtime.universe._
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import jth.util.MacroHelper._
import jth.func.StorageFunc
import jth.func.PointerFunc
import com.sun.jna._
import jth.generate._
import scala.NotImplementedError

@GenerateAllTypes("Factory", "", "0") 
abstract class Storage[T <: AnyVal, U <: AnyVal]{

  //abstract method that needed to be implemented
  def getOps():StorageFunc[T, U]
  def getPointerOps():PointerFunc[T]
  //Constructor
  val ops = getOps()
//  val pointerOps = getPointerOps()
  protected var ptr = ops.Storage_new()
  //function with prefix `ctor' is treated differently
  //be cautious while overwritting.
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
    throw new NotImplementedError
  }
  
  def ctor4(t:Storage[T, U]) = ctor3 (t, 0, 0)
//  
//  val d = jth.func.StorageFunc.getInt   
  /*------------------------------------------*/
  def size() = ops.Storage_size(ptr)
  def apply(idx:Long) = ops.Storage_get(ptr, idx)
  def fill(value:T) = ops.Storage_fill(ptr, value)
  override def toString():String={
    val sz = size().toInt
    val element_sz = ops.Storage_elementSize().toInt
    val str = ops.Storage_data(ptr).getByteArray(0, element_sz * sz).asInstanceOf[Array[T]].take(sz).mkString("\n")
    s"$str\n[${this.getClass().getSimpleName()} of size $sz]"
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



//object Storage {
//  def first[T:TypeTag] = new {
//    val d = GenerateMacro.defaultTypesT(typeTag[T])
//  }
//
//  def apply[T, U]()(implicit ev : (T, U)): Storage[T,U] ={
//
//  }
//}
