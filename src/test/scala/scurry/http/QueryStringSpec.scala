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

import groovy.lang.GroovyShell

class QueryStringSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val sh = GroovyShell()

  it should "create query string" in {
    val q = sh.evaluate("""
      new scurry.http.QueryString(
        userId: 500,
        userName: 'me',
        groups: ['staff', 'admin', 'sudo']
      )
    """).asInstanceOf[QueryString]

    assert(q.get("userId") == "500")
    assert(q.getInteger("userId") == 500)
    assert(q.getLong("userId") == 500L)
    assert(q.getValues("userId").size == 1)
    assert(q.getValues("userId").get(0) == "500")

    assert(q.get("userName") == "me")
    assert(q.getValues("userName").size == 1)
    assert(q.getValues("userName").get(0) == "me")

    assert(q.get("groups") == "staff")
    assert(q.getValues("groups").size == 3)
    assert(q.getValues("groups").get(0) == "staff")
    assert(q.getValues("groups").get(1) == "admin")
    assert(q.getValues("groups").get(2) == "sudo")
  }
