-- KEYS: [1]job:sleeping, [2]job:ready
-- ARGS: [1]currentTime
-- Comments: result is the  job id
local jobs=redis.call('zrangebyscore', KEYS[1], '-inf', ARGV[1])
local count = table.maxn(jobs)

if count>0  then
  -- Comments: remove from Sleeping Job sorted set
  redis.call('zremrangebyscore', KEYS[1], '-inf', ARGV[1])
  
  -- Comments: add to the Ready Job list
  -- Comments: can optimize to use lpush id1,id2,... for better performance
  for i=1,count do 
    redis.call('lpush', KEYS[2], jobs[i])
  end
end