package com.coursework.accesscontrol.auth;

import java.util.Objects;

/**
 * Generic outcome for secure operations.
 *
 * @param <T> type of the returned value (e.g., String for reads).
 */
public final class AccessOutcome<T> {
    private final AccessResult accessResult;
    private final T value;

    public AccessOutcome(AccessResult accessResult, T value) {
        this.accessResult = Objects.requireNonNull(accessResult, "accessResult");
        this.value = value;
    }

    public AccessResult getAccessResult() {
        return accessResult;
    }

    public T getValue() {
        return value;
    }
}

