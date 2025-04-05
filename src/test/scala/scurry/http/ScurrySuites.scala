package scurry.http

class ScurrySuites extends org.scalatest.Suites(
  QueryStringSpec(),
  HeadersSpec(),
  CookiesSpec(),
  MultipartSpec(),
  HttpClientSpec(),
  HttpServerSpec()
)
