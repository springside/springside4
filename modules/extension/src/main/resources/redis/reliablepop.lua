-- KEYS: [1]job:ready, [2]job:lock
-- ARGS: No

local job=redis.call('rpop', KEYS[1])

if(job ~=nil) then
	redis.call('zadd', KEYS[2], now, job)
end

return job;