# Scurry

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/scurry_3.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22scamper_3%22)

The Groovy-_esque_ wrapper for [Scamper](https://github.com/losizm/scamper).


## Let's Scurry!

Although there are no references to Groovy itself in the codebase, this library
is intended for Groovy developers. It defines a typeless interface to Scamper
utilites in favor of dynamic typing, which is a Groovy staple.

Here's a quick example using the HTTP client:

```groovy
import groovy.json.JsonSlurper
import scurry.http.HttpClient

// Create client using supplied settings
def client = new HttpClient(
  accept: ['application/json', '*/*; q=0.2'],
  acceptEncoding: ['deflate', 'gzip'],
  bufferSize: 8192,
  readTimeout: 5000,
  continueTimeout: 1000,
  keepAlive: false,
  storeCookies: true
)

// Create POST request
def request = [
  method: 'POST',
  url: 'https://api.example.com/messages',
  headers: [
    'Content-Type': 'application/json',
    'Authorization': 'Bearer 94c2f320-7120-4338-8e40-42bc2581dd05'
  ],
  // Supply body as byte[], String, File, Path, InputStream, Reader,
  // QueryString, Multipart, or BodyWriter
  body: '''{ "to": ["Peter", "Mary"], "text": "Hello, world!"] }'''
]

// Send request and check response
client.send(request) { response ->
  println "${response.statusCode} ${response.reasonPhrase}"
  response.headers.each {
    println "${it.name}: ${it.value}"
  }

  if (!response.isSuccessful) {
    def json  = new JsonSlurper()
    def body  = response.body.toBytes(1024)
    def error = json.parse(body)

    throw new Exception("Error Code ${error.code}: ${error.message}")
  }
}
```

## License

**Scurry** is licensed under the Apache License, Version 2. See [LICENSE](LICENSE)
for more information.
