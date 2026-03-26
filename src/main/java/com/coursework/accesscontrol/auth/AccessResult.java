package com.coursework.accesscontrol.auth;

import com.coursework.accesscontrol.log.AccessDecision;

public final class AccessResult {
    private final AccessDecision decision;

    public AccessResult(AccessDecision decision) {
        this.decision = decision;
    }

    public AccessDecision getDecision() {
        return decision;
    }

    public boolean isAllowed() {
        return decision == AccessDecision.ALLOW;
    }
}

