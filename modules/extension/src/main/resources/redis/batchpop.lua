-- KEYS: [1]job:ready, [2]job:lock
-- ARGS: [1]batchsize, [2]reliable
local batchsize = ARGV[1]
local batchJobs={}
for i=1,batchsize do
	local job=redis.call('rpop', KEYS[1])
	batchJobs[i]=job
end	

if (reliable=='true') then
	for i=1,batchsize do
	redis.call('zadd', KEYS[2],  now, batchJobs[i])
	end
end

return batchJobs;