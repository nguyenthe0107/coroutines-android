package com.kantek.coroutines.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "loginResponse", strict = false)
public class LoginResponse {
    @Element(name = "loginReturn", type = String.class)
    private String loginReturn;

    public String getLoginReturn() {
        return loginReturn;
    }

    public void setLoginReturn(String loginReturn) {
        this.loginReturn = loginReturn;
    }
}
