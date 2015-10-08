package jth.func

import scala.annotation.StaticAnnotation
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import com.sun.jna._
import jth.generate._
/**
 * Created by et on 08/10/15.
 */

@GenerateAllTypes("Native", "TH", "TH")
trait StorageCopyFunc[T<:AnyVal, U<:AnyVal] {
  def Storage_rawCopy(storage:Pointer, src: Array[T])
  def Storage_copy(storage:Pointer, src: Pointer)
  def Storage_copyByte(storage:Pointer, src: Pointer)
  def Storage_copyChar(storage:Pointer, src: Pointer)
  def Storage_copyShort(storage:Pointer, src: Pointer)
  def Storage_copyInt(storage:Pointer, src: Pointer)
  def Storage_copyLong(storage:Pointer, src: Pointer)
  def Storage_copyFloat(storage:Pointer, src: Pointer)
  def Storage_copyDouble(storage:Pointer, src: Pointer)
}
