package jth.func


import scala.annotation.StaticAnnotation
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.Context
import com.sun.jna._
import jth.generate._
import com.sun.jna.Pointer

@GenerateAllTypes("Native", "TH", "TH") trait StorageFunc[T <: AnyVal, U <: AnyVal] {
//just playing around
  def Storage_data(self:Pointer):Pointer
  def Storage_size(self:Pointer):Long 
  def Storage_elementSize():Long 
  def Storage_get(self:Pointer, idx:Long):T
  def Storage_set(self:Pointer, idx:Long, value:T)
  def Storage_new():Pointer
  def Storage_newWithSize(size:Long):Pointer
  def Storage_newWithAllocator(size:Long, allocator:Pointer, allocatorContext:Pointer):Pointer
//  def Storage_newWithMapping(filename:String, size:Long, shared:Int):Pointer
//  def Storage_newWithSize1(data0:T):Pointer
//  def Storage_newWithSize2(data0:T, data1:T):Pointer
//  def Storage_newWithSize3(data0:T, data1:T, data2:T):Pointer
//  def Storage_newWithSize4(data0:T, data1:T, data2:T, datat3:T):Pointer
//  def Storage_setFlag(storage:Pointer, flag:Byte):Unit
//  def Storage_clearFlag(storage:Pointer, flag:Byte):Unit
  def Storage_retain(storage:Pointer):Unit
  def Storage_free(storage:Pointer):Unit
  def Storage_newWithData(data: Array[T], size: Long):Pointer
//  def Storage_newWithDataAndAllocator(data: Array[T], size:Long, allocator:Pointer, allocatorContext:Pointer):Pointer
//  def Storage_resize(storage:Pointer, size:Long)
  def Storage_fill(storage:Pointer, value:T):Unit
}



//@GenerateType("THInt") object Test extends StorageFunc[Int, Long]

