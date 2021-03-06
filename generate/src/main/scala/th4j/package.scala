

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

package object th4j {
  
  val defaultPrefix = "TH"
  
  val allTypes = Map(
    "Int"   ->("Int","Long", "CPU"),
    "Float" ->("Float","Double", "CPU"),
    "Byte"  ->("Byte", "Long", "CPU"),
    "Char"  ->("Char","Long", "CPU"),
    "Short" ->("Short","Long", "CPU"),
    "Long" -> ("Long","Long", "CPU"),
    "Double" -> ("Double","Double", "CPU")
  )



  val restrictions = Map(
    "Tensor" -> Map(
      "abs"          -> List("Double","Float","Int","Long"),
      "acos"         -> List("Double","Float"),
      "asin"         -> List("Double","Float"),
      "atan"         -> List("Double","Float"),
      "ceil"         -> List("Double","Float"),
      "cos"          -> List("Double","Float"),
      "cosh"         -> List("Double","Float"),
      "exp"          -> List("Double","Float"),
      "log"          -> List("Double","Float"),
      "log1p"        -> List("Double","Float"),
      "pow"          -> List("Double","Float"),
      "round"        -> List("Double","Float"),
      "sin"          -> List("Double","Float"),
      "sinh"         -> List("Double","Float"),
      "sqrt"         -> List("Double","Float"),
      "tan"          -> List("Double","Float"),
      "tanh"         -> List("Double","Float"),
      "linspace"     -> List("Double","Float"),
      "logspace"     -> List("Double", "Float"),
      "rand"         -> List("Double", "Float"),
      "randn"        -> List("Double", "Float"),
      "multinomial"  -> List("Double", "Float"),
      "floor"        -> List("Double", "Float")
    )
  )

}