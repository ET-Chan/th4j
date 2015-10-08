package jth


import jth.Storage._
import jth.Storage.FloatStorage
import jth.func.StorageFunc
import com.sun.jna._


object Main extends App {

  val d= new DoubleStorage(100)
  val t = (0 until 100 map{_.toShort}).toArray
  val l = new ShortStorage(100)
  l.copy(t)
  d.copy(l)
  println(l)
  println(d)
  //  d.fill(0)

//  println(d.toString)
//  println(d)

//  val t = new Storage.IntStorage(10)
//  val s = new IntStorage(10)
//  s.fill(1)
//  println(s)
//  i.fill(30)
//  println(i)
//  println(Array[Double]().isInstanceOf[Array[Int]])
}
