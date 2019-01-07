package com.mike.routing;

public class RouteError {
    private final Type type;

    public RouteError(Type type) {
        this.type = type;
    }

    public enum Type {DidNotDropTo, DidNotPickFor}
}
