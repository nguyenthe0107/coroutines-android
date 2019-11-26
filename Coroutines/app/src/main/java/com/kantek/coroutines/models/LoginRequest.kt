package com.kantek.coroutines.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Root

@Root(name = "SOAP-ENV:Envelope")
@NamespaceList(
    Namespace(prefix = "SOAP-ENV", reference = "http://schemas.xmlsoap.org/soap/envelope/")
)
class LoginRequestEnvelope constructor(
    @field:Element(name = "SOAP-ENV:Body", required = false)
    private var body: LoginRequestBody
)

@Root(name = "SOAP-ENV:Body", strict = false)
class LoginRequestBody constructor(
    @field:Element(name = "SOAP-ENV:login", required = false)
    val request: LoginRequest
)

@Root(name = "SOAP-ENV:login", strict = false)
@Namespace(reference = "urn:Magento")
class LoginRequest constructor(
    @field:Element(name = "username")
    val userName: String,

    @field:Element(name = "apiKey")
    val apiKey: String
)