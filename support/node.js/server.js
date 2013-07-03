var http = require('http');
var cluster = require('cluster');

var port = 8000;
var interval =10000;
var numReqs = 0;

//worker handle http request
function requestHandler(req, res){
  try {
    // send response
    res.writeHead(200, {'Content-Type': 'text/plain'});
    var result='Hello world\n';
  	res.end(result);

    // notify master about the request
    process.send({ cmd: 'notifyRequest' });
  } catch(error){
    console.log(error)
  }
}

//master count requestes
function messageHandler(msg) {
    if (msg.cmd && msg.cmd == 'notifyRequest') {
      numReqs += 1;
    }
}

//master print the statistics
function flushStatistics(){
  var msg = new Date().toISOString().replace(/T/, ' ').replace(/\..+/, '') + ", " +numReqs + " requests, " +  (numReqs*1000)/interval+ " TPS."; 
  console.log(msg);
  numReqs=0;
}

if (cluster.isMaster) {
  // Start workers
  var numCPUs = require('os').cpus().length;
  for (var i = 0; i < numCPUs; i++) {
    cluster.fork();
  }
  console.log(numCPUs + " servers running at port " + port);

  //define the notify hanlder
  Object.keys(cluster.workers).forEach(function(id) {
    cluster.workers[id].on('message', messageHandler);
  });

  // start flush statistics timer
  setInterval(flushStatistics, interval);

} else {
  // Worker start server
  http.createServer(requestHandler).listen(port);
}



