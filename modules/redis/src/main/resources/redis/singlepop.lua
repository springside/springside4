-- KEYS: [1]job:ready, [2]job:lock
-- ARGV: [1]currentTime, [2]reliable

local job=redis.call('rpop', KEYS[1])

if (job and ARGV[2] == 'true') then
	redis.call('zadd', KEYS[2], ARGV[1], job)
end

return job;