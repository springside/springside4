-- KEYS: [1]job:ready, [2]job:lock

local job=redis.call('rpop', KEYS[1])
redis.call('zadd', KEYS[2], job, now)
return job;