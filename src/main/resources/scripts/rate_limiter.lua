
--- 获取令牌
--- 返回码
--- -2 没有令牌桶配置
--- -1 表示取令牌失败，也就是桶里没有令牌
--- 1 表示取令牌成功
--- @param key 令牌的唯一标识
--- @param permits_s 请求令牌数量
--- @param curr_mill_second_s 当前毫秒数
--- @param reserved_percent_s 桶中预留的令牌百分比，整数
--- @param max_wait_mill_second_s 最长等待多久。负值意味着给其它请求保留部分token
local function acquire(key, permits_s, curr_mill_second_s, reserved_percent_s, max_wait_mill_second_s)
    local permits = tonumber(permits_s)
    local curr_mill_second = tonumber(curr_mill_second_s)
    local reserved_percent = tonumber(reserved_percent_s);
    local max_wait_mill_second = 0
    if reserved_percent <= 0 then
        -- 不预留令牌时，等待时间才生效
        max_wait_mill_second = tonumber(max_wait_mill_second_s)
    end

    local rate_limit_info = redis.pcall("HMGET", key, "last_mill_second", "curr_permits", "max_permits", "rate")
    local last_mill_second = tonumber(rate_limit_info[1])
    local curr_permits = tonumber(rate_limit_info[2])
    local max_permits = tonumber(rate_limit_info[3])
    local rate = tonumber(rate_limit_info[4])

    --- 标识没有配置令牌桶
    if type(rate) == 'boolean' or rate == nil then
        return -2
    end

    local local_curr_permits = max_permits;
    local secondToMill = 1000;
    local percent = 100;

    --- 令牌桶刚刚创建，上一次获取令牌的毫秒数为空
    --- 根据和上一次向桶里添加令牌的时间和当前时间差，触发式往桶里添加令牌，并且更新上一次向桶里添加令牌的时间
    --- 如果向桶里添加的令牌数不足一个，则不更新上一次向桶里添加令牌的时间
    if (type(last_mill_second) ~= 'boolean' and last_mill_second ~= nil) then
        --  local reverse_permits, rest = math.modf(((curr_mill_second - last_mill_second) / secondToMill) * rate)
        local reverse_permits=math.floor((curr_mill_second-last_mill_second)/secondToMill)*rate
        --- 大于0表示不是第一次获取令牌，也没有向桶里添加令牌
        if (reverse_permits > 0) then
            redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
        end

         local expect_curr_permits = reverse_permits + curr_permits;
               local_curr_permits = math.min(expect_curr_permits, max_permits);
    else
        redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
    end

    local result = -1
    local remainder = local_curr_permits - permits
    if (remainder + rate * max_wait_mill_second / secondToMill - max_permits * reserved_percent / percent >= 0) then
        result = math.max(0, (-remainder * secondToMill) / rate)
        redis.pcall("HSET", key, "curr_permits", remainder)
    else
        redis.pcall("HSET", key, "curr_permits", local_curr_permits)
    end

    return result
end

local key = KEYS[1]

return acquire(key, ARGV[1], ARGV[2], ARGV[3], ARGV[4])