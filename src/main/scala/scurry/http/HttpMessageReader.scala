/*
 * Copyright 2023 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scurry.http

import java.lang.{ Boolean as JBoolean, Integer as JInteger }
import java.util.HashMap as JHashMap

import scamper.http.{ HttpRequest, HttpResponse }
import scamper.http.cookies.{ RequestCookies, ResponseCookies }

private object HttpMessageReader:
  def apply(req: HttpRequest): JMap[String, AnyRef] =
    val map = JHashMap[String, AnyRef]()
    map.put("method", req.method.toString)
    map.put("target", req.target.toString)
    map.put("path", req.path)
    map.put("query", QueryString(req.query))
    map.put("httpVersion", req.version.toString)
    map.put("headers", Headers(req.headers))
    map.put("cookies", Cookies(req.cookies))
    map.put("body", Body(req))
    map

  def apply(res: HttpResponse): JMap[String, AnyRef] =
    val map = JHashMap[String, AnyRef]()
    map.put("httpVersion", res.version.toString)
    map.put("statusCode", JInteger.valueOf(res.statusCode))
    map.put("reasonPhrase", res.reasonPhrase)
    map.put("isInformational", JBoolean.valueOf(res.isInformational))
    map.put("isSuccessful", JBoolean.valueOf(res.isSuccessful))
    map.put("isRedirection", JBoolean.valueOf(res.isRedirection))
    map.put("isClientError", JBoolean.valueOf(res.isClientError))
    map.put("isServerError", JBoolean.valueOf(res.isServerError))
    map.put("headers", Headers(res.headers))
    map.put("cookies", Cookies(res.cookies))
    map.put("body", Body(res))
    map
