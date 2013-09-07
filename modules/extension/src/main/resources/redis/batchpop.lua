-- KEYS: [1]job:ready, [2]job:lock
-- ARGV: [1]currentTime, [2]batchsize, [3]reliable
local batchJobs={}
for i=1,ARGV[2] do
	local job = redis.call('rpop', KEYS[1])
	if (job) then
		batchJobs[i] = job
	else
		break
	end	
end	

if (ARGV[3] == 'true') then
	local count =  table.maxn(batchJobs)
	for i=1,count do
		redis.call('zadd', KEYS[2], ARGV[1], batchJobs[i])
	end
end

return batchJobs;