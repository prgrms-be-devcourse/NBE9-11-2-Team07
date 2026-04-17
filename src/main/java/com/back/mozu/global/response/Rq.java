package com.back.mozu.global.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request;

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public String getCurrentUserEmail() {
        return getHeader("X-USER-EMAIL");
    }
}