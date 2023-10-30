# Scurry

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/scurry_3.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=g:com.github.losizm%20a:scurry_3)

The Groovy-_esque_ wrapper for [Scamper](https://github.com/losizm/scamper).


## Let's Scurry!

Although there are no references to Groovy itself in the codebase, this library
is intended for Groovy developers. It defines a typeless interface to Scamper in
favor of dynamic typing, which is a Groovy staple.

### HTTP Server

Here's a quick example of running an HTTP server:

```groovy
import scurry.http.HttpServer
import scurry.http.response.BadRequest
import scurry.http.response.Ok

// Define utility to log HTTP messages
def logHttpMessage(msg) {
  println "[server] ${msg.startLine}"
  msg.headers.each { println "[server] $it" }
  println ''
  msg
}

// Create HTTP server
def server = new HttpServer(host: 'localhost', port: 8080)

// At this point, the server hasn't been started.
// You'll want to add some endpoints first.

// Log incoming requests
server.incoming { logHttpMessage(it) }

// Handle GET requests
server.get('/greet') { req ->
  def name = req.query.name
  if (name == null) new Ok(body: 'Hello, stranger!')
  else              new Ok(body: "Hello, $name!")
}

// Handle POST requests
server.post('/echo') { req ->
  def message = req.body.toString(8192)
  if (message == '') new BadRequest(body: 'No message.')
  else               new Ok(body: message)
}

// Log outgoing responses
server.outgoing { logHttpMessage(it) }

// Start HTTP server
server.start()

Thread.start {
  // Run server for 30 seconds
  try     { sleep(30000) }
  finally { server.stop() }
}

```

### HTTP Client

In the following, the HTTP client is used to talk to server defined in previous
section:

```groovy
import scurry.http.HttpClient

// Create HTTP client using custom settings
def client = new HttpClient(
  resolveTo: [host: 'localhost', port: 8080, secure: false],
  accept: '*/*',
  acceptEncoding: ['deflate', 'gzip']
)

// Send GET request
client.get(url: '/greet', query: [name: 'Lupita']) { res ->
  println "[client] ${res.body.toString(8192)}"
}

// Send POST request with message body
client.post(url: '/echo', body: 'Can you hear me?') { res ->
  println "[client] ${res.body.toString(8192)}"
}

// Send empty POST request and handle client error
client.post(url: '/echo', body: null) { res ->
  println "[client] ${res.statusCode} ${res.reasonPhrase}"
  
  if (res.successful) println "[client] This won't print."
  else                println "[client] Oops! ${res.body.toString(8192)}"
}
```

## License

**Scurry** is licensed under the Apache License, Version 2. See [LICENSE](LICENSE)
for more information.
