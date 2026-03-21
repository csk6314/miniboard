package com.miniboard.backend.auth.jwt;

public enum TokenType {

    ACCESS_TOKEN("access"),
    REFRESH_TOKEN("refresh");


    private final String typeName;

    private TokenType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }
}
