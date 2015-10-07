package jth.func

import com.sun.jna._
import jth.generate._


//for this trait, I won't bother using macro, it is overkilling

trait PointerFunc[T <: AnyVal] {
  def Array(ptr: Pointer, offset:Long, size:Int) : Array[T]
}

object PointerFunc{
  object IntInstance extends PointerFunc[Int]{
    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getIntArray(offset, size)
  }
  object FloatInstance extends PointerFunc[Float]{
    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getFloatArray(offset, size)
  }
  object ByteInstance extends PointerFunc[Byte]{
    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getByteArray(offset, size)
  }
  object ShortInstance extends PointerFunc[Short]{
    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getShortArray(offset, size)
  }
  object DoubleInstance extends PointerFunc[Double]{
    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getDoubleArray(offset, size)
  }  
  object LongInstance extends PointerFunc[Long]{
    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getLongArray(offset, size)
  }
  object CharInstance extends PointerFunc[Char]{
    override def Array(ptr:Pointer, offset:Long, size:Int) = ptr.getCharArray(offset, size)
  }
  
  def getFloat() = FloatInstance
  def getInt()  = IntInstance
  def getChar() = CharInstance
  def getLong() = LongInstance
  def getDouble() = DoubleInstance
  def getByte() = ByteInstance
  
}

