package com.mike.routing;

import java.util.List;

public class RouteErrors {
    private List<RouteError> errors;

    public boolean hasErrors() {
        return errors.size() > 0;
    }
    public void reset() {
        errors.clear();
    }

    public void add(RouteError error) {
        errors.add(error);
    }

}
