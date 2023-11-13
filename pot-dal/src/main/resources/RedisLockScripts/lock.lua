local lock_name = KEYS[1]
local lock_generator = KEYS[2]
local lock_sign = ARGV[1]
local lock_expire_time = ARGV[2]
local generator_exists = redis.call("EXISTS",lock_generator)
if 0 == generator_exists then
    redis.call("SET",lock_generator,10000)
end

local sign_exists = redis.call("EXISTS",lock_name)
if 0 == sign_exists then
    local incr_sign = redis.call("INCR",lock_generator)
    redis.call("SET",lock_name,incr_sign,"EX",lock_expire_time)
    local lock_hold_name = lock_name .. '_HOLD_' .. incr_sign .. '_COUNT'
    redis.call("SET",lock_hold_name,1,"EX",lock_expire_time)
    return incr_sign .. ':1'
else
    local curr_sign = redis.call("GET",lock_name)
    if lock_sign == curr_sign then
        redis.call("SET",lock_name,curr_sign,"EX",lock_expire_time)
        local lock_hold_name = lock_name .. '_HOLD_' .. curr_sign .. "_COUNT"
        local lock_hold_exists = redis.call("EXISTS",lock_hold_name)
        if 0 == lock_hold_exists then
            redis.call("SET",lock_hold_name,1,"EX",lock_expire_time)
            return curr_sign .. ':1'
        else
            local curr_hold_count = redis.call("INCR",lock_hold_name)
            if curr_hold_count <=0 then
                redis.call("SET",lock_hold_name,1,"EX",lock_expire_time)
                return curr_sign .. ':1'
            else
                redis.call("SET",lock_hold_name.curr_hold_count,"EX",lock_expire_time)
                return curr_sign .. ':' .. curr_hold_count
            end
        end
    else
        return '0:0'
    end
end