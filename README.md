# TH4J
A wrapper of torch TH library for Java (and other JVM langauges), implemented in Scala.

# Prerequisites
1. Torch 7 (Have libTH.so in LD_LIBRARY_PATH)
2. JVM 8
3. cutorch (Optional, Have libTHC.so in LD_LIBRARY_PATH)
4. sbt 0.13.8 (Just for building)
5. jnlua (Optional, for Lua interpolation)

# Building
1. `git clone https://github.com/ET-Chan/th4j.git`
2. `cd th4j && sbt compile`

# Setup
## Bintray
Add following lines into your build.sbt
```
resolvers += Resolver.bintrayRepo("et-chan", "maven")
libraryDependencies +=
  ("th4j-core" %% "th4j-core" % "0.1")
```
## Compile
Execute `sbt publishLocal` to publish the compiled library into your local ivy repos, then add 
```
libraryDependencies += ("th4j-core" %% "th4j-core" % "0.1")
```
into you build.sbt.

#Usage
The wrapper aims to mimic the APIs in torch, in an object oriented and type-safe way, as much as possible. The main components in th4j are exactly the ones in torch: Storage and Tensor, while storage is the internal holder for tensor data. Similarly, there are six types of tensors(storages) available for use: Double, Float, Int, Long, Short, Char. (Cuda & OpenCL are yet to be implemented.)

##Construction
For the purpose of illustration, we will only focus on playing with DoubleTensor in scala. Java users should be able to use the libraries with similar syntax, except the querying part.

First of all, import the libraries:

```scala
import th4j.Tensor._
```

To construct a new tensor:

```scala
val t = new DoubleTensor()
```

, a new tensor with specified sizes:

```scala
val t = new DoubleTensor(sz1, [sz2, [sz3, [sz4]])
```

, or a new tensor viewing the same storage of another tensor
 
```scala
val t1 = new DoubleTensor(3, 4)
val t2 = new DoubleTensor(t1)
```

##Tensor Querying
Due to the syntax differences between Lua and Scala, it is unlikely to implement an identical querying way of tensor as in torch. Instead, another operator !: is introduced to support range querying. The following table shows the comparison of them, supposing t is an instance of DoubleTensor.

|                               | Torch                    | th4j           | 
|-------------------------------| -------------------------|----------------| 
|Extracting sub tensors 1       | `t[i]`                   | `t(i - 1)` | 
|Extracting sub tensors 2       | `t[i][j]`      | `t(i - 1)(j - 1)`     | 
|Extracting sub tensors 3       |`t[{i, j}]` |`t( i-1 !: j-1)`|
|Range query                    | `t[{{i, j}, {k, f}, {g, h}]`    | `t((i-1, j)!:(k-1, f)!:(g-1, h))`      | 
|Range query with negative index| `t[{{i, -1}, {k, -2}}]`  |`t((i-1, -1)!:(k-1, -2))`|
|Range query with singleton dimension|`t[{{i, i}, {k, f}}]` | `t(i!:(k-1, f))`|

You may notice that there is an annoying ``-1" for almost every coordinate. In fact, th4j uses the convention 0-based indexing scheme. Another point worth noticing is that the ranges in th4j are left inclusive and right exclusive, as shown above. Note that since the querying relies heavily on Scala Implicits, java users has to use a list of tuple to do range query.


##Tensor Manipulation
In torch, one can flexibly change the view, or shape of a tensor. th4j supports most of them, while mathematics manipulations of the tensors are still being implemented.

For example, one may want to change the view of a tensor and expand it. In torch, it is

```lua
t = torch.Tensor(3, 4):view(3, 1, 4):expand(3, 10, 4)
```

To achieve the same in th4j, one can

```scala
val t = new DoubleTensor(3, 4).view(3, 1, 4).expand(3, 10, 4)
```

Experienced torch users may find the API very familiar. However, there are some exceptions. Say we want to create a random tensor. In torch, you will

```lua
t = torch.rand(3, 4)
```

In th4j, you have to 

```scala
val t = new DoubleTensor(3, 4).rand()
```

Similar differences occur in eye, zero etc. Refer to the Tensor.scala to know more.



##Lua Interpolation
TODO


# Warning
The package is in its early development, and the API may change in future release.

# Third party libraries
1. Torch 7 (All rights reserved to Idiap Research Institute and Deepmind Technologies)

# License
MIT License