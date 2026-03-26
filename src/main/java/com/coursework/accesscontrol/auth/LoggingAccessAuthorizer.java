package com.coursework.accesscontrol.auth;

import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.log.AccessLog;
import com.coursework.accesscontrol.log.AccessLogEntry;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.User;

import java.util.Objects;

/**
 * Decorator: records every authorization decision to the access log.
 */
public final class LoggingAccessAuthorizer implements AccessAuthorizer {
    private final AccessAuthorizer delegate;
    private final AccessLog accessLog;

    public LoggingAccessAuthorizer(AccessAuthorizer delegate, AccessLog accessLog) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.accessLog = Objects.requireNonNull(accessLog, "accessLog");
    }

    @Override
    public AccessResult authorize(User user, Resource resource, Operation operation) {
        AccessResult result = delegate.authorize(user, resource, operation);
        AccessDecision decision = result.getDecision();
        accessLog.record(new AccessLogEntry(accessLog.getClock(), user, resource, operation, decision));
        return result;
    }
}

