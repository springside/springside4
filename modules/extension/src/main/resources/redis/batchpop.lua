-- KEYS: [1]job:ready, [2]job:lock
-- ARGS: [1]currentTime, [2]batchsize, [3]reliable
local batchJobs={}
for i=1,ARGV[2] do
	local job = redis.call('rpop', KEYS[1])
	if (job ==nil) then
		break
	else
		batchJobs[i] = job
	end
	
end	

if (ARGV[3]=='true') then
	for i=1,ARGV[2] do
		redis.call('zadd', KEYS[2], ARGV[1], batchJobs[i])
	end
end

return batchJobs;