package com.coursework.accesscontrol.log;

import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class AccessLogEntry {
    private final LocalDateTime timestamp;
    private final String userId;
    private final Role role;
    private final String resourceName;
    private final Operation operation;
    private final AccessDecision decision;

    public AccessLogEntry(
            LocalDateTime timestamp,
            String userId,
            Role role,
            Resource resource,
            Operation operation,
            AccessDecision decision
    ) {
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.role = Objects.requireNonNull(role, "role");
        this.resourceName = Objects.requireNonNull(resource, "resource").getName();
        this.operation = Objects.requireNonNull(operation, "operation");
        this.decision = Objects.requireNonNull(decision, "decision");
    }

    public AccessLogEntry(Clock clock, User user, Resource resource, Operation operation, AccessDecision decision) {
        this(
                LocalDateTime.ofInstant(clock.instant(), clock.getZone()),
                user.getId(),
                user.getRole(),
                resource,
                operation,
                decision
        );
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Operation getOperation() {
        return operation;
    }

    public AccessDecision getDecision() {
        return decision;
    }

    /**
     * Example output:
     * 20-01-2025 16:00, user2, ADMIN, Exam Paper, WRITE, ALLOW
     */
    public String toLogLine(DateTimeFormatter formatter) {
        return timestamp.format(formatter)
                + ", " + userId
                + ", " + role
                + ", " + resourceName
                + ", " + operation
                + ", " + decision;
    }
}

