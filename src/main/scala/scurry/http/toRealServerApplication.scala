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

private object toRealServerApplication extends ServerSettingsConverter:
  def apply(map: JMap[String, AnyRef]): ServerApplication =
    val app = ServerApplication()
    map.optionInt("backlogSize").foreach(app.backlogSize)
    map.optionInt("queueSize").foreach(app.queueSize)
    map.optionInt("poolSize").foreach(app.poolSize)
    map.optionInt("bufferSize").foreach(app.bufferSize)
    map.optionInt("readTimeout").foreach(app.readTimeout)
    map.optionInt("headerLimit").foreach(app.headerLimit)
    map.optionString("logger").foreach(app.logger)
    map.optionKeepAlive().foreach(app.keepAlive(_, _))
    map.optionSsl().foreach(app.secure(_, _))
    app
