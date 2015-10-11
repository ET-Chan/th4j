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

package th4j.util


import java.text.{DecimalFormatSymbols, DecimalFormat, NumberFormat}

import th4j.{Storage, Tensor}

/**
 * Created by et on 10/10/15.
 */
object BeautifulPrinter {
  //printing is very slow, but I don't care currently.
  val formatter = new DecimalFormat("#.####E0")
  val widthPerObj = 8
  def format(obj:Any):String={
    if (obj.toString.length < widthPerObj) obj.toString
    else formatter.format(obj)
  }
  val widthPerLine = 80
  val gapBetweenColumn = 2
  def print1d(iter:Iterator[_], sb:StringBuilder)={
    val col = iter.map(format).toList

    val maxwidth = col.iterator
      .map(str=>{
        if (str.head != '-') str.length
        else str.length - 1
      }).max + gapBetweenColumn
    col.foreach(str=>{
      sb ++= " " * (maxwidth - str.length)
      sb ++= str
      sb += '\n'
    })
  }
  def print1d(tensor:Tensor[_, _], sb:StringBuilder): Unit ={
    val nd = tensor.nDimension()
    if (nd != 1) {
      throw new Exception(s"print1d, only can be invoked on 1d tensor, is called on ${nd}d tensor")
    }
    val sz = tensor.size(0)
    import Helper._
    IterateL(0, sz).map(tensor(_))
  }
  def print2d(tensor:Tensor[_,_], sb:StringBuilder):Unit = {
    val nd = tensor.nDimension()
    if (nd != 2){
      throw new Exception(s"print2d, only can be invoked on 2d tensor, is called on ${nd}d tensor")
    }
    import Helper._

  }
  def print2d(it:Iterator[_], size0:Int, size1:Int, sb:StringBuilder) = {
    val strs = Array.ofDim[String](size0, size1)
    val maxwidths = Array.ofDim[Int](size1)

    for{i<- 0 until size0
        j<- 0 until size1}{
        val formatStr = format(it.next)
        strs(i)(j) = formatStr
        val length = {
          if (formatStr.head != '-') formatStr.length
          else formatStr.length - 1
        } + gapBetweenColumn
        maxwidths(j) = math.max(maxwidths(j), length)
    }
    //scan the maxwidths and find the first one exceed the limit
    val lastcol = maxwidths
      .iterator
      .scanLeft(0)(_ + _)
      .zipWithIndex
      .find{case (cumWidth, idx)=>cumWidth > widthPerLine} match {
      case None=>
        size1
      case Some((_, idx))=>
        idx
      }
    //now aggregate the columns after last col into maxwidths
    for{i<- lastcol until size1}{
      val ridx = i % lastcol
      maxwidths(ridx) = math.max(maxwidths(i), maxwidths(ridx))
    }

    //populate strings to sb
    for{i<- 0 until size1/lastcol} {
      val startcol =  i * lastcol
      val endcol = math.max(i*lastcol, size1)
      sb ++= s"Columns from ${startcol} to ${endcol - 1}\n"
      for{k<- 0 until size0}{
        for{j<- startcol until endcol}{
          sb ++= " " * (maxwidths(j % lastcol) - strs(k)(j).length)
          sb ++= strs(k)(j)
        }
        sb += '\n'
      }
    }
  }
}
