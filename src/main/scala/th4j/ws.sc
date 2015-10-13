import th4j.Tensor.DoubleTensor

val x = new DoubleTensor(4, 5).zero()
x.isContiguous()
val y = x.select(2, 3)
y.isContiguous()

