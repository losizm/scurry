import groovy.json.JsonSlurper

import java.time.Instant

import scurry.http.HttpClient
import scurry.http.Multipart


def checkResponse(res) {
  if (!res.isSuccessful) {
    res.body.drain(16 * KB)
    throw Exception("${res.statusCode} ${res.reasonPhrase}")
  }
}

def createComment(httpClient, text, ... attachments) {
  def json = new JsonSlurper()
  def updateText = 'Hello, everybody!'
  def maxLength = 256 * KB

  def commentBody = attachments
    ? new Multipart([name: 'text', content: text], *attachments)
    : text

  // Create comment
  def commentUrl = httpClient.post(url: '/api/comments', body: commentBody) { res ->
    checkResponse(res)
    res.headers['Location']
  }

  // Get comment
  httpClient.get(url: commentUrl) { res ->
    checkResponse(res)

    def comment = json.parse(res.body.toInputStream(16 * 1024))
    println "[${comment.time}] ${comment.text}, attachments=${comment.attachments}"

    if (comment.text != text)
      throw Exception("Unexpected comment: ${comment.text}")

    if (comment.attachments.size() != attachments.size())
      throw Exception("Unexpected comment: ${comment.text}")
  }

  // Update comment
  httpClient.put(url: commentUrl, body: updateText) { res ->
    checkResponse(res)
  }

  // Check updated comment
  httpClient.get(url: commentUrl) { res ->
    checkResponse(res)

    def comment = json.parse(res.body.toInputStream(16 * 1024))
    println "[${comment.time}] ${comment.text}, attachments=${comment.attachments}"

    if (comment.text != updateText)
      throw Exception("Unexpected comment: ${comment.text}")
  }

  // Delete comment
  httpClient.delete(url: commentUrl) { res ->
    checkResponse(res)
  }

  // Check deleted comment
  httpClient.get(url: commentUrl) { res ->
    if (res.statusCode != 404) {
      res.drain(8 * KB)
      throw Exception("${res.statusCode} ${res.reasonPhrase}")
    }
  }
}

def httpClient = new HttpClient(
  bufferSize: 4 * KB,
  readTimeout: 1000,
  continueTimeout: 500,
  accept: ['application/json; q=0.8', '*/*; q=0.2'],
  acceptEncoding: ['deflate', 'gzip'],
  keepAlive: false,
  storeCookies: false,
  resolveTo: [host: 'localhost', port: 20080, secure: false]
)

// Create simple comment
createComment(httpClient, 'Hello, barbershop')

// Create multipart comment
createComment(
  httpClient,
  'Hello, again',
  [name: 'attachment', content: new File(resources, 'junk/photo.svg')],
  [name: 'attachment', content: new File(resources, 'junk/currency.dat'), fileName: 'currency.txt']
)
