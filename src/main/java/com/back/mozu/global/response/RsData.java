package com.back.mozu.global.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record RsData<T>(
        String msg,
        String resultCode,
        String errorCode,
        T data
) {
    public RsData(String msg, String resultCode, T data) {
        this(msg, resultCode, null, data);
    }

    public RsData(String msg, String resultCode) {
        this(msg, resultCode, null, null);
    }

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("-")[0]);
    }
}