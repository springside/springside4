@echo off

echo [INFO] Analize perf4j log.
echo [INFO] Please put perf4j-0.9.16.jar here.



cd %~dp0


java -jar perf4j-0.9.16.jar ../logs/perf_stats.log -t 30000


pause