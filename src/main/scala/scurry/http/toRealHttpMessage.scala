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

private object toRealHttpMessage extends Converter:
  def apply(map: JMap[String, AnyRef]): RealHttpMessage =
    if map.containsKey("method") || map.containsKey("target") then
      toRealHttpRequest(map)
    else if map.containsKey("statusCode") || map.containsKey("reasonPhrase") then
      toRealHttpResponse(map)
    else
      bad("Unable to identify message type")
