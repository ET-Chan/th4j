
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
import jth.func.StorageFunc
@GenerateAllTypes("Factory", "StorageFunc", "") trait Storage[T <: AnyVal, U <: AnyVal]{
  //abstract method that needed to be implemented
  def getOps():StorageFunc[T, U]
  val ops = getOps()
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
