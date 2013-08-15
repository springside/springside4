-- KEYS: [1]job:ready, [2]job:lock
-- ARGV: [1]currentTime

local job=redis.call('rpop', KEYS[1])

if (job ~=nil) then
	redis.call('zadd', KEYS[2], ARGV[1], job)
end

return job;