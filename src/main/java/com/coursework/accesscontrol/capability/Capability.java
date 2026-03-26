package com.coursework.accesscontrol.capability;

import com.coursework.accesscontrol.model.Operation;

import java.util.Objects;

/**
 * Capability<T> provides compile-time restriction over which operation can be invoked.
 * <p>
 * For example, {@code SecureResourceService#read(...)} accepts only {@code Capability<Read>}.
 */
public final class Capability<T extends CapabilityType> {
    private final Operation operation;
    private final Class<T> capabilityType;

    public Capability(Operation operation, Class<T> capabilityType) {
        this.operation = Objects.requireNonNull(operation, "operation");
        this.capabilityType = Objects.requireNonNull(capabilityType, "capabilityType");
    }

    public Operation getOperation() {
        return operation;
    }

    public Class<T> getCapabilityType() {
        return capabilityType;
    }
}

