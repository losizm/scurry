import java.util.concurrent.atomic.AtomicInteger

import scurry.http.HttpServer

def host = args[0]
def port = args[1] as Integer
def logger = args.size() > 2 ? args[2] : 'MessengerServer'

def httpServer = new HttpServer(
  host: host,
  port: port,
  logger: logger,
  backlogSize: 20,
  ssl: null,
  queueSize: 16,
  poolSize: 4,
  bufferSize: 8192,
  readTimeout: 250,
  headerLimit: 20,
  keepAlive: null
)

def logHttpMessage(msg) {
  log.info '[server]'
  log.info "${msg.startLine}"
  msg.headers.each { log.info "$it" }
  log.info ''
  msg
}

httpServer.incoming { logHttpMessage(it) }
httpServer.outgoing { logHttpMessage(it) }

httpServer.trigger {
  log.info "[server] trigger event received: [type: ${it.type}, host: ${it.server.host}, port: ${it.server.port}, sslEnabled: ${it.server.sslEnabled}]"
}

httpServer.websocket('/messenger') { websocket ->
  def messages = ['Hello, world!', '''It's time to scurry.''', 'Goodbye, cruel world!']
  def messageIndex = new AtomicInteger(0)

  websocket.idleTimeout = 60
  websocket.messageCapacity = 16384
  websocket.payloadLimit = 8192

  log.info "[server] id=${websocket.id}"
  log.info "[server] target=${websocket.target}"
  log.info "[server] secure=${websocket.secure}"
  log.info "[server] protocolVersion=${websocket.protocolVersion}"
  log.info "[server] state=${websocket.state}"
  log.info "[server] messageCapacity=${websocket.messageCapacity}"
  log.info "[server] payloadLimit=${websocket.payloadLimit}"
  log.info "[server] idleTimeout=${websocket.idleTimeout}"

  websocket.onPing {
    log.info "[server] received ping: ${it.size()} byte(s)"
    websocket.pong(it)
  }

  websocket.onPong {
    log.info "[server] received ping: ${it.size()} byte(s)"
  }

  websocket.onText { text ->
    log.info "[server] received text: $text"
    switch (text) {
      case 'get':
        def index = messageIndex.getAndIncrement()
        if (index < messages.size()) {
          def message = messages[index]
          log.info "[server] sending text: $message"
          websocket.send(message)
        }
        else {
          log.info "[server] sending text: Error: No more messages"
          websocket.send('Error: No more messages')
        }
        break

      case 'reset':
        messageIndex.set(0)
        break

      default:
        log.info "[server] sending text: Error: Invalid command"
        websocket.send('Error: Invalid command')
    }
  }

  websocket.onBinary {
    log.info "[server] received ping: ${it.size()} byte(s)"
  }

  websocket.onError {
    log.info("[server] error occurred: $it")
  }

  websocket.onClose {
    log.info("[server] closing connection: $it")
  }

  log.info("[server] sending ping: test")
  websocket.ping("test".getBytes("UTF-8"))

  log.info("[server] opening connection")
  websocket.open()
}

httpServer
