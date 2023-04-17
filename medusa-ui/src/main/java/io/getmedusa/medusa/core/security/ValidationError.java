package io.getmedusa.medusa.core.security;

public record ValidationError(String field, String message) { }