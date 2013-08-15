-- KEYS: [1]job:scheduled, [2]job:ready, [3]job:dispatch.counter
-- ARGS: [1]currentTime
local jobs=redis.call('zrangebyscore', KEYS[1], '-inf', ARGV[1])
local count = table.maxn(jobs)
local batchsize = 64

if count>0  then
  -- Comments: add to the Ready Job list
  local i = 1
  while i<=count do
  	local j = 1
  	local batchJobs={}
  	while i<=count and j<=batchsize do
  		batchJobs[j]= jobs[i]
  		i=i+1
  		j=j+1
  	end
  	redis.call('lpush', KEYS[2], unpack(batchJobs))
  end

  -- Comments: remove from scheduled Job sorted set
  redis.call('zremrangebyscore', KEYS[1], '-inf', ARGV[1])
  
  -- Comments: incr Dispatch counter
  redis.call('incrby', KEYS[3], count)
  
  return count;
end