package com.assetneuron.whatsapp.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("authConfig")
public class AuthConfig {

    @Value("${authorization.skip:true}")
    private boolean skipAuthorization;

    public boolean isSecurityEnabled() {
        return !skipAuthorization;
    }
}

