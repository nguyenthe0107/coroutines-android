package com.kantek.coroutines.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "loginResponse", strict = false)
class LoginResponse {
    @get:Element(name = "loginReturn", type = String::class)
    @set:Element(name = "loginReturn", type = String::class)
    var loginReturn: String? = null
}
