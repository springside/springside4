-- KEYS: [1]job:ready, [2]job:lock
-- ARGS: [1]currentTime, [2]batchsize, [3]reliable
local batchJobs={}
for i=1,ARGV[2] do
	batchJobs[i]=redis.call('rpop', KEYS[1])
end	

if (ARGV[3]=='true') then
	for i=1,ARGV[2] do
		redis.call('zadd', KEYS[2], ARGV[1], batchJobs[i])
	end
end

return batchJobs;