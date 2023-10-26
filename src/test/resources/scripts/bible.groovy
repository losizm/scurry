import groovy.json.JsonSlurper

import java.time.Instant

import scurry.http.HttpClient


def final maxLength = 256 * KB

def httpClient = new HttpClient(
  bufferSize: 4096,
  readTimeout: 1000,
  continueTimeout: 500,
  accept: ['application/json; q=0.8', '*/*; q=0.2'],
  acceptEncoding: ['deflate', 'gzip'],
  keepAlive: false,
  storeCookies: false,
  resolveTo: [
    host: 'localhost',
    port: 10080,
    secure: false
  ]
)

def req = [
  method: "GET",
  url: '/api/findVerses/1/1/1',
  query: [
    q: '/it was good'
  ],
  headers: [
    'User-Agent': 'Gramper/0.1.0',
    'Date': Instant.now(),
    'X-Test': [1024, 8192L, 'Hello, world!', Instant.now()]
  ]
]

def verses = httpClient.send(req) { res ->
  println "${res.httpVersion} ${res.statusCode} ${res.reasonPhrase}"
  res.headers.each { header ->
    println "${header.name}: ${header.value}"
  }
  println()

  def bytes = res.body.toBytes(maxLength)
  def json  = new JsonSlurper()
  json.parse(bytes)
}

verses.each { verse ->
  println "Testament: ${verse.testament}"
  println "Book: ${verse.book}"
  println "Chapter: ${verse.chapter}"
  println "Number: ${verse.number}"
  println "Text: ${verse.text}"
  println()
}
