package jth.util

/**
 * Created by et on 02/10/15.
 */
object TypeHelper {

  import scala.reflect.runtime.universe._
  def combineTypeTags[T:TypeTag, U:TypeTag]():String={
    val t1 = typeTag[T].toString()
    val t2 = typeTag[U].toString()
    t1+t2

  }
}
