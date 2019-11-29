package com.kantek.coroutines.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Body", strict = false)
class LoginResponseBody {
    @get:Element(name = "loginResponse", required = false)
    @set:Element(name = "loginResponse", required = false)
    var response: LoginResponse? = null
}
