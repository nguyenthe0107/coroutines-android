package com.kantek.coroutines.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "SOAP-ENV:Envelope")
@NamespaceList({
        @Namespace(prefix = "SOAP-ENV", reference = "http://schemas.xmlsoap.org/soap/envelope/"),
        @Namespace(prefix = "SOAP-ENC", reference = "http://schemas.xmlsoap.org/soap/envelope/"),
        @Namespace(prefix = "ns1", reference = "urn:Magento"),
        @Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
        @Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance")
})

public class LoginResponseEnvelope {
    @Attribute(name = "encodingStyle", required = false)
    private String encodingStyle = "http://schemas.xmlsoap.org/soap/encoding/";
    @Element(name = "Body", required = false)
    private LoginResponseBody body;
    public LoginResponseEnvelope(){

    }

    public LoginResponseBody getBody() {
        return body;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public void setBody(LoginResponseBody body) {
        this.body = body;
    }

    public void setEncodingStyle(String encodingStyle) {
        this.encodingStyle = encodingStyle;
    }
}
