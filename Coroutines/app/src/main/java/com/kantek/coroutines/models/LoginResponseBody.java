package com.kantek.coroutines.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Body", strict = false)
public class LoginResponseBody {
    @Element(name = "loginResponse", required = false)
    private LoginResponse response;

    public LoginResponse getResponse() {
        return response;
    }

    public void setResponse(LoginResponse response) {
        this.response = response;
    }
}
