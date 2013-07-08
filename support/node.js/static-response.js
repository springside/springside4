var http = require('http');
var cluster = require('cluster');

var port = 8888;
var interval =10000;
var numReqs = 0;

//worker handle http request
function requestHandler(request, response){
  try {
    // send response
    response.writeHead(200, {'Content-Type': 'text/plain'});
    var body='Hello world';
  	response.end(body);

    // notify master about the request
    process.send({ cmd: 'notifyRequest' });
  } catch(error){
    console.log(error)
  }
}

//master count requests
function messageHandler(msg) {
    if (msg.cmd && msg.cmd == 'notifyRequest') {
      numReqs += 1;
    }
}

//master print statistics
function flushStatistics(){
  var msg = new Date().toISOString().replace(/T/, ' ').replace(/\..+/, '') + ", " +numReqs + " requests, " +  (numReqs*1000)/interval+ " TPS."; 
  console.log(msg);
  numReqs=0;
}

if (cluster.isMaster) {
  // Master start workers
  var numCPUs = require('os').cpus().length;
  for (var i = 0; i < numCPUs; i++) {
    cluster.fork();
  }
  console.log(numCPUs + " servers running at port " + port);

  //define notification hanlder
  Object.keys(cluster.workers).forEach(function(id) {
    cluster.workers[id].on('message', messageHandler);
  });

  // start flush statistics timer
  setInterval(flushStatistics, interval);

} else {
  // Worker start server
  http.createServer(requestHandler).listen(port);
}



