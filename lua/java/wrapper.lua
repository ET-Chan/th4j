-- The MIT License (MIT)
--
-- Copyright (c) 2015 Iat Chong Chan
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
-- SOFTWARE.
--

--
-- Created by IntelliJ IDEA.
-- User: et
-- Date: 03/11/15
-- Time: 14:24
--

local wrapper = function(func,types, ...)
    local callParams = {... }
--    print(func, types, callParams)
    for k, v in ipairs(types) do
        if v:find('Tensor') then
           callParams[k] = torch.pushudata(callParams[k], 'torch.' .. v)
        end
    end

    local ret = table.pack(func(unpack(callParams)))

    for k, v in ipairs(ret) do
        if (torch.isTensor(v)) then
            v:retain()
            ret[k] = torch.pointer(v)
        end
    end
    return unpack(ret)
    --need to be modified
end

th4j.javaWrapper = wrapper

