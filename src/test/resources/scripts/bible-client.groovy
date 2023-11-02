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
  keepAliveEnabled: false,
  cookieStoreEnabled: false,
  resolveTo: [
    host: 'localhost',
    port: 10080,
    secure: false
  ]
)

httpClient.outgoing {
  it.setHeader("X-Outgoing-Test", Instant.now())
  it
}

httpClient.incoming {
  it.setHeader("X-Incoming-Test", Instant.now())
  it
}

def req = [
  method: "GET",
  target: '/api/findVerses/1/1/1',
  query: [
    q: '/it was good'
  ],
  headers: [
    'User-Agent': 'Nunya/1.0.0',
    'Date': Instant.now(),
    'X-Test': [1024, 8192L, 'Hello, world!', Instant.now()]
  ]
]

def verses = httpClient.send(req) { res ->
  log.info "${res.httpVersion} ${res.statusCode} ${res.reasonPhrase}"
  res.headers.each { header ->
    log.info "${header.name}: ${header.value}"
  }
  log.info ""

  def bytes = res.body.toBytes(maxLength)
  def json  = new JsonSlurper()
  json.parse(bytes)
}

verses.each { verse ->
  log.info "Testament: ${verse.testament}"
  log.info "Book: ${verse.book}"
  log.info "Chapter: ${verse.chapter}"
  log.info "Number: ${verse.number}"
  log.info "Text: ${verse.text}"
  log.info ""
}
