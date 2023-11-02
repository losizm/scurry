import scurry.http.*
import scurry.http.request.Get
import scurry.http.request.Post

def inHost   = args.size() > 0 ? args[0] : 'localhost'
def inPort   = args.size() > 1 ? args[1] as Integer : 0
def inSecure = args.size() > 2 ? args[2].toBoolean() : false

// Create HTTP client using custom settings
def client = new HttpClient(
  resolveTo: [host: inHost, port: inPort, secure: inSecure ],
  accept: '*/*',
  acceptEncoding: ['deflate', 'gzip']
)

// Send GET request
client.get(target: '/greet?name=developer') { res ->
  log.info "[client] ${res.body.toString(8192)}"
}

// Manually create GET request and add query parameter
def greetRequest = new Get(target: '/greet', query: [name: 'Big Dawg'])
client.send(greetRequest) { res ->
  log.info "[client] ${res.body.toString(8192)}"
}

// Send GET request with path parameter
client.get(target: '/greet/Lupita') { res ->
  log.info "[client] ${res.body.toString(8192)}"
}

// Send POST request with message body
client.post(target: '/echo', body: '''Hello, it's me.''') { res ->
  log.info "[client] ${res.body.toString(8192)}"
}

// Manually create POST request and add message body
def echoRequest = new Post(target: '/echo', body: 'Just me again.')
client.send(echoRequest) { res ->
  log.info "[client] ${res.body.toString(8192)}"
}

// Send empty POST request and handle client error
client.post(target: '/echo', body: '') { res ->
  if (res.successful)
    log.info '''[client] This won't be printed.'''
  else
    log.info "[client] Oops! ${res.body.toString(8192)} (${res.statusCode})"
}
