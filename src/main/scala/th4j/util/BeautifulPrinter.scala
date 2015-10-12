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

import scala.collection.mutable

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
      val endcol = math.min((i+1)*lastcol, size1)
      if (lastcol != size1)
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


  def printnd(tensor:Tensor[_,_], sb:StringBuilder)={
    //printn arbitrary tensor

    val it = tensor.iterator()
    val nd = tensor.nDimension()
    val sizes = tensor.size()
    lazy val size0 = sizes(nd - 2)
    lazy val size1 = sizes(nd - 1)

    def recurPrint(aux: collection.mutable.Stack[Long]):Unit = {
      val curDim = aux.length
      if (curDim == nd - 1) {
        //this only occurs while the original tensor is 1d.
        print1d(it, sb)
      }else if(curDim == nd - 2) {
        if (nd > 2)
          sb ++= "(" + aux.mkString(",") + ",.,.)=\n"
        print2d(it,size0.toInt, size1.toInt, sb)
      }else{//curDim < nd - 2
        val curSize = sizes(curDim)
        (0L until curSize).foreach(i=>{
          aux.push(i)
          recurPrint(aux)
          aux.pop()
        })
      }
    }

    recurPrint(new mutable.Stack[Long]())



//    def recurPrint(curDim:Int): Unit ={
//      if (nd)
//    }

  }
}
