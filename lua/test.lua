require 'torch'
require 'nn'
function multi(a,b)
    return  {a+b, a*b}
end

function newTensor()
    local d = torch.Tensor(3, 4)
    d:fill(1)
    d:retain()
    print(d)
    local ptr = torch.pointer(d)
    print(ptr)
    return ptr
end



function readFromTensor(ptr)
    local self = torch.pushudata(ptr, "torch.DoubleTensor")
    print(self)
end

function test(i, str)
    return i, i, str
end