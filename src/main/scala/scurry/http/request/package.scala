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
package request

/** GET request. */
class Get private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates GET request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "GET"))

/** HEAD request. */
class Head private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates HEAD request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "HEAD"))

/** POST request. */
class Post private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates POST request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "POST"))

/** PUT request. */
class Put private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates PUT request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "PUT"))

/** PATCH request. */
class Patch private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates PATCH request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "PATCH"))

/** DELETE request. */
class Delete private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates DELETE request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "DELETE"))

/** OPTIONS request. */
class Options private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates OPTIONS request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "OPTIONS"))

/** TRACE request. */
class Trace private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates TRACE request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "TRACE"))

/** CONNECT request. */
class Connect private[scurry] (req: ScamperHttpRequest) extends HttpRequest(req):
  /** Creates CONNECT request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req, "CONNECT"))
