package com.coursework.accesscontrol.model;

import java.util.Objects;

public final class Resource {
    private final String name;
    private final AccessScope scope;
    private String content;

    public Resource(String name, AccessScope scope, String content) {
        this.name = Objects.requireNonNull(name, "name");
        this.scope = Objects.requireNonNull(scope, "scope");
        this.content = Objects.requireNonNull(content, "content");
    }

    public String getName() {
        return name;
    }

    public AccessScope getScope() {
        return scope;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = Objects.requireNonNull(content, "content");
    }
}

