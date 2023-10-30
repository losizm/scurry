import scurry.http.*
import scurry.http.response.BadRequest
import scurry.http.response.InternalServerError
import scurry.http.response.Ok

// Define utility to log HTTP messages
def logHttpMessage(msg) {
  log.info "[server] correlate=${msg.correlate}"
  log.info "${msg.startLine}"
  msg.headers.each { log.info "$it" }
  log.info ''
  msg
}

def inHost   = args.size() > 0 ? args[0] : 'localhost'
def inPort   = args.size() > 1 ? args[1] as Integer : 0
def inLogger = args.size() > 2 ? args[2] : 'GreetServer'

// Create HTTP server
def server = new HttpServer(host: inHost, port: inPort, logger: inLogger)

// Log incoming requests
server.incoming {
  logHttpMessage(it)
}

// Handle GET requests
server.get('/greet') { req ->
  def name = req.query.name
  if (name == null) 
    new Ok(body: 'Hello, world!')
  else
    new Ok(body: "Hello, $name!")
}

// Handle GET requests with path parameter
server.get('/greet/:name') { req ->
  def params = req.pathParameters
  def name = params.getString('name')

  if (name == null) 
    new Ok(body: 'Hello, world!')
  else
    new Ok(body: "Hello, $name!")
}

// Handle POST requests at different endpoint
server.post('/echo') { req ->
  def message = req.body.toString(8192)
  if (message == '')
    new BadRequest(body: 'No message.')
  else
    new Ok(body: message)
}

// Log outgoing responses
server.outgoing { logHttpMessage(it) }
server
