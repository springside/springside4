-- KEYS: [1]job:scheduled, [2]job:ready, [3]job:dispatch.counter, [4]job:lock, [5]job:retry.counter
-- ARGV: [1]currentTime, [2]reliable, [3]timeout
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
end

if (ARGV[2] == 'true') then
	local expiredTime = tonumber(ARGV[1]) - tonumber(ARGV[3])
	local lockJobs = redis.call('zrangebyscore', KEYS[4], '-inf', expiredTime)
	local lockJobCount = table.maxn(lockJobs)
	
	if lockJobCount>0  then
	  -- Comments: add to the Ready Job list
	  local i = 1
	  while i<=lockJobCount do
	  	local j = 1
	  	local batchJobs={}
	  	while i<=lockJobCount and j<=batchsize do
	  		batchJobs[j]= lockJobs[i]
	  		i=i+1
	  		j=j+1
	  	end
	  	redis.call('lpush', KEYS[2], unpack(batchJobs))
	  end

	  -- Comments: remove from scheduled Job sorted set
	  redis.call('zremrangebyscore', KEYS[4], '-inf', expiredTime)
	  
	  -- Comments: incr Retry Lock counter
	  redis.call('incrby', KEYS[5], lockJobCount)
	end
end
