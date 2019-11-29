package com.kantek.coroutines.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Root

@Root(name = "SOAP-ENV:Envelope")
@NamespaceList(
    Namespace(
        prefix = "SOAP-ENV",
        reference = "http://schemas.xmlsoap.org/soap/envelope/"
    ),
    Namespace(prefix = "SOAP-ENC", reference = "http://schemas.xmlsoap.org/soap/envelope/"),
    Namespace(prefix = "ns1", reference = "urn:Magento"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance")
)
class LoginResponseEnvelope {
    @set:Attribute(name = "encodingStyle", required = false)
    @get:Attribute(name = "encodingStyle", required = false)
    var encodingStyle = "http://schemas.xmlsoap.org/soap/encoding/"

    @set:Element(name = "Body", required = false)
    @get:Element(name = "Body", required = false)
    var body: LoginResponseBody? = null
}
