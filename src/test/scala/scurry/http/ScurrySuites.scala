package scurry.http

import org.scalatest.{ BeforeAndAfterAll, Suites }

class ScurrySuites extends Suites(
  QueryStringSpec(),
  HeadersSpec(),
  CookiesSpec(),
  MultipartSpec(),
  HttpClientSpec(),
  HttpServerSpec()
) with BeforeAndAfterAll:

  override def beforeAll() = ()
  override def afterAll() = ()
