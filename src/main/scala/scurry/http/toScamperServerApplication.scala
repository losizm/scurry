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

import scamper.http.server.ServerApplication

import settings.ServerSettingsConverter

private object toScamperServerApplication extends ServerSettingsConverter:
  def apply(map: JMap[String, AnyRef]): ServerApplication =
    val app = ServerApplication()
    toOptionInt(map, "backlogSize").foreach(app.backlogSize)
    toOptionInt(map, "queueSize").foreach(app.queueSize)
    toOptionInt(map, "poolSize").foreach(app.poolSize)
    toOptionInt(map, "bufferSize").foreach(app.bufferSize)
    toOptionInt(map, "readTimeout").foreach(app.readTimeout)
    toOptionInt(map, "headerLimit").foreach(app.headerLimit)
    toOptionString(map, "logger").foreach(app.logger)
    toOptionKeepAlive(map).foreach(app.keepAlive(_, _))
    toOptionSsl(map).foreach(app.secure(_, _))
    app
