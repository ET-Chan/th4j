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

package th4j.func

import com.sun.jna._
import th4j.generate._

/**
 * Created by et on 11/10/15.
 */
@GenerateAllTypes("Native", "TH", "TH")
trait TensorMathFunc [T<:AnyVal, U<:AnyVal]{
  def Tensor_fill(r:Pointer, value:T):Unit
  def Tensor_zero(r:Pointer):Unit

  def Tensor_maskedFill(tensor:Pointer, mask: Pointer, value:T):Unit
  def Tensor_maskedCopy(tensor:Pointer, mask:Pointer, src:Pointer):Unit
  def Tensor_maskedSelect(tensor:Pointer, src:Pointer, mask:Pointer):Unit

  def Tensor_nonzero(subscript:Pointer, tensor:Pointer):Unit

  def Tensor_indexSelect(tensor:Pointer,src:Pointer, dim:Int, index:Pointer):Unit
  def Tensor_indexCopy(tensor:Pointer, dim:Int, index:Pointer, src:Pointer):Unit
  def Tensor_indexFill(tensor:Pointer, dim:Int, index:Pointer, value:T):Unit

  def Tensor_gather(tensor:Pointer, src:Pointer, dim:Int, index:Pointer):Unit
  def Tensor_scatter(tensor:Pointer, dim:Int, index:Pointer, src:Pointer):Unit
  def Tensor_scatterFill(tensor:Pointer, dim:Int, index:Pointer, value:T):Unit

  def Tensor_dot(t:Pointer, src:Pointer):U
  def Tensor_minall(t:Pointer):T
  def Tensor_maxall(t:Pointer):T
  def Tensor_sumall(t:Pointer):U
  def Tensor_prodall(t:Pointer):U

  def Tensor_add(r: Pointer, t:Pointer, value: T):Unit
  def Tensor_mul(r: Pointer, t:Pointer, value: T):Unit
  def Tensor_div(r: Pointer, t:Pointer, value: T):Unit
  def Tensor_clamp(r: Pointer, t:Pointer, min_value:T, max_value:T):Unit

  def Tensor_cadd(r: Pointer, t:Pointer, value:T, src:Pointer):Unit
  def Tensor_cmul(r:Pointer, t:Pointer, src:Pointer):Unit
  def Tensor_cpow(r:Pointer, t:Pointer, src:Pointer):Unit
  def Tensor_cdiv(r:Pointer, t:Pointer, src:Pointer):Unit

  def Tensor_addcmul(r:Pointer, t:Pointer, value:T, src1: Pointer, src2:Pointer):Unit
  def Tensor_addcdiv(r:Pointer, t:Pointer, value:T, src1:Pointer, src2:Pointer):Unit

  def Tensor_addmv(r:Pointer, beta:T, t:Pointer, alpha:T, mat:Pointer, vec:Pointer):Unit
  def Tensor_addmm(r:Pointer, beta:T, t:Pointer, alpha:T, mat1:Pointer, mat2:Pointer):Unit
  def Tensor_addr(r:Pointer, beta:T, t:Pointer, alpha:T, vec1:Pointer, vec2:Pointer):Unit

  def Tensor_addbmm(r:Pointer, beta: T, t:Pointer, alpha:T, batch1:Pointer, batch2:Pointer):Unit
  def Tensor_baddbmm(r:Pointer, beta: T, t:Pointer, alpha:T, batch1:Pointer, batch2:Pointer)

  def Tensor_match(r:Pointer, m1:Pointer, m2:Pointer, gain:T):Unit

  def Tensor_numel(t:Pointer):Long
  def Tensor_max(values:Pointer, indicies:Pointer, t:Pointer, dimension:Int):Unit
  def Tensor_min(values:Pointer, indicies:Pointer, t:Pointer, dimension:Int):Unit
  def Tensor_kthvalue(values:Pointer, indicies:Pointer, t:Pointer, k:Long, dimension:Int):Unit
  def Tensor_median(values:Pointer, indicies:Pointer, t:Pointer, dimension:Int)
  def Tensor_sum(r:Pointer, t:Pointer, dimension:Int):Unit
  def Tensor_prod(r:Pointer, t:Pointer, dimension:Int):Unit
  def Tensor_cumsum(r:Pointer, t:Pointer, dimension:Int):Unit
  def Tensor_cumprod(r:Pointer, t:Pointer, dimension:Int):Unit
  def Tensor_sign(r:Pointer, t:Pointer):Unit
  def Tensor_trace(t:Pointer):U
  def Tensor_cross(r:Pointer, a:Pointer, b:Pointer, dimension:Int):Unit

  def Tensor_cmax(r:Pointer, t:Pointer, src:Pointer):Unit
  def Tensor_cmin(r:Pointer, t:Pointer, src:Pointer):Unit
  def Tensor_cmaxValue(r:Pointer, t:Pointer, value:T):Unit
  def Tensor_cminValue(r:Pointer, t:Pointer, value:T):Unit

  def Tensor_zeros(r:Pointer, size:Pointer):Unit
  def Tensor_ones(r:Pointer, size:Pointer):Unit
  def Tensor_diag(r:Pointer, t:Pointer, k:Int):Unit
  def Tensor_eye(r:Pointer, n:Long, m:Long):Unit
  def Tensor_range(r:Pointer, xmin:U, xmax:U, step:U):Unit
  def Tensor_randperm(r:Pointer, generator:Pointer, n:Long):Unit

  def Tensor_reshape(r:Pointer, t:Pointer, size:Pointer):Unit
  def Tensor_sort(rt:Pointer, ri:Pointer, t:Pointer, dimension:Int, descendingOrder:Int):Unit
  def Tensor_tril(r:Pointer, t:Pointer, k:Long):Unit
  def Tensor_triu(r:Pointer, t:Pointer, k:Long):Unit
  def Tensor_cat(r:Pointer, ta:Pointer, tb:Pointer, dimension:Int):Unit
//  def Tensor_catArray(result:Pointer, inputs:Pointer, numInputs:Int, dimension:Int):Unit

  def Tensor_ltValue(r:Pointer, t:Pointer, value:T):Unit
  def Tensor_leValue(r:Pointer, t:Pointer, value:T):Unit
  def Tensor_gtValue(r:Pointer, t:Pointer, value:T):Unit
  def Tensor_geValue(r:Pointer, t:Pointer, value:T):Unit
  def Tensor_neValue(r:Pointer, t:Pointer, value:T):Unit
  def Tensor_eqValue(r:Pointer, t:Pointer, value:T):Unit

  def Tensor_ltTensor(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_leTensor(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_gtTensor(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_geTensor(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_neTensor(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_eqTensor(r:Pointer, ta:Pointer, tb:Pointer):Unit

  def Tensor_ltTensorT(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_leTensorT(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_gtTensorT(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_geTensorT(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_neTensorT(r:Pointer, ta:Pointer, tb:Pointer):Unit
  def Tensor_eqTensorT(r:Pointer, ta:Pointer, tb:Pointer):Unit
}
