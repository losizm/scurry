import groovy.json.JsonSlurper

import java.time.Instant
import java.util.concurrent.TimeUnit

import scurry.http.HttpClient


def final maxLength = 256 * KB

def host = args[0]
def port = args[1] as Integer
def secure = args[2].toBoolean()

def httpClient = new HttpClient(
  bufferSize: 4096,
  readTimeout: 1000,
  continueTimeout: 500,
  accept: ['application/json; q=0.8', '*/*; q=0.2'],
  acceptEncoding: ['deflate', 'gzip'],
  keepAliveEnabled: false,
  cookieStoreEnabled: false,
  resolveTo: [
    host: host,
    port: port,
    secure: secure
  ]
)

def websocket = httpClient.websocket(target: '/messenger') { it ->
  log.info "[client] id=${it.id}"
  log.info "[client] target=${it.target}"
  log.info "[client] secure=${it.secure}"
  log.info "[client] protocolVersion=${it.protocolVersion}"
  log.info "[client] state=${it.state}"
  log.info "[client] messageCapacity=${it.messageCapacity}"
  log.info "[client] payloadLimit=${it.payloadLimit}"
  log.info "[client] idleTimeout=${it.idleTimeout}"
  it
}

try {
  websocket.onPing { data ->
    log.info "[client] received ping: ${data.size()} byte(s)"
    websocket.pong(data)
  }

  websocket.onText { text ->
    log.info "[client] received text: $text"
  }

  log.info "[client] opening"
  websocket.open()

  log.info "[client] sending text: get"
  websocket.send('get')

  log.info "[client] sending text: get"
  websocket.send('get')

  log.info "[client] sending text: get"
  websocket.send('get')

  log.info "[client] sending text: get"
  websocket.send('get')

  log.info "[client] sending text: reset"
  websocket.send('reset')

  log.info "[client] sending text: get"
  websocket.send('get')

  log.info "[client] sending text: get (asynchronously)"
  websocket.sendAsync('get').get(3, TimeUnit.SECONDS)

  log.info "[client] asyncrhonous call completed"
  sleep(3 * 1000)
}
catch (e) {
  e.printStackTrace()
}
finally {
  websocket.close()
}
