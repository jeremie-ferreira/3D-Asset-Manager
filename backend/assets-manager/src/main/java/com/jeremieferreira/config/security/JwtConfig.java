package com.jeremieferreira.config.security;

public class JwtConfig {
    public static final String URI = "/auth/**";
    public static final String HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";
    public static final int EXPIRATION = 24*60*60;
    public static final String SECRET = "JKP02DyK0rHzSdN3u4uLO3X4Y8E6UZwYs5TOJQGKcLjC8jPMUOMupSilGJR03sV0";
}