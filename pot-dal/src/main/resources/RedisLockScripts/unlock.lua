local lock_name = KEYS[1]
local lock_sign = ARGV[2]
local sign_exists = redis.call("EXISTS",lock_name)
if 0 == sign_exists then
    return '0:0'
end
local curr_sign = redis.call("GET",lock_name)
if lock_sign == curr_sign then
    local lock_hold_name = lock_name .. '_HOLD_' .. curr_sign .. "_COUNT"
    local lock_hold_exists = redis.call("EXISTS",lock_hold_name)
    if 0 == lock_hold_exists then
        redis.call("DEL",lock_hold_name)
        return curr_sign .. ':0'
    else
        local curr_hold_count = redis.call("INCR",lock_hold_name)
        if curr_hold_count <=0 then
            redis.call("DEL",lock_name)
            redis.call("DEL",lock_hold_name)
        end
        return curr_sign .. ':' .. curr_hold_count
    end
else
   local lock_hold_name = lock_name .. '_HOLD_' .. curr_sign .. "_COUNT"
   local lock_hold_exists = redis.call("EXISTS",lock_hold_name)
   if 0 == lock_hold_exists then
       return curr_sign .. ':0'
   else
       local curr_hold_count = redis.call("GET",lock_hold_name)
       return curr_sign .. ':' .. curr_hold_count
   end
end