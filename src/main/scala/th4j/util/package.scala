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
 * Created by et on 12/10/15.
 */
package object util {

  class SingletonPair(num:Long) extends Tuple2[Long, Long](num, num + 1)

//  type L[X] = X =>Long

  implicit def toLong(from:Int):Long = from.toLong

  implicit def numToSingeletonPair[A](a:A)(implicit ev1:A=>Long):(Long, Long) = new SingletonPair(a)
  implicit def pairToList[A](a:A)(implicit ev1:A=>(Long, Long)):List[(Long, Long)] = List(a)
  implicit def arrToLongArr[A](a:Array[A])(implicit ev1:A=>Long):Array[Long] = a.map(ev1)

//  implicit def listToLongArr[A](a:List[A])(implicit ev1:A=>Long):Array[Long] = a.map(ev1).toArray

  implicit def longToList[A](a:A)(implicit ev1:A=>Long):List[Long] = List(a)
  implicit def toLong(from:Long):Long = from

  implicit def listToArray(a:List[Long]):Array[Long] = a.toArray
//  implicit def numToSingletonPair[A:L](from:A):SingletonPair = new SingletonPair(from)

//  implicit def pairToList(from: (Long, Long)):List[(Long, Long)] = List(from)
}
