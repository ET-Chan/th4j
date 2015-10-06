package jth



import jth.func.StorageFunc
import com.sun.jna._

object Main extends App {
  
  val d = StorageFunc.getFloat()
  val t = d.Storage_new()
  println(t)
}