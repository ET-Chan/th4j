package jth.func


import scala.annotation.StaticAnnotation
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.Context
import com.sun.jna._


import jth.GenerateAllTypes

@GenerateAllTypes trait StorageFunc[T, U] {
//just playing around
  def Storage_new():Pointer
  def Storage_newWithSize2(data0:T, data1:T):Pointer
  def Storage_newWithSize4(data0:T, data1:T, data2:T, datat3:T):Pointer
  
}


