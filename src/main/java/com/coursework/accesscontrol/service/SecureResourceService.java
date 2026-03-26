package com.coursework.accesscontrol.service;

import com.coursework.accesscontrol.auth.AccessOutcome;
import com.coursework.accesscontrol.auth.AccessResult;
import com.coursework.accesscontrol.auth.AccessAuthorizer;
import com.coursework.accesscontrol.capability.Capability;
import com.coursework.accesscontrol.capability.Read;
import com.coursework.accesscontrol.capability.Write;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.User;

import java.util.Objects;

public final class SecureResourceService {
    private final AccessAuthorizer authorizer;

    public SecureResourceService(AccessAuthorizer authorizer) {
        this.authorizer = Objects.requireNonNull(authorizer, "authorizer");
    }

    public AccessOutcome<String> read(User user, Capability<Read> capability, Resource resource) {
        Objects.requireNonNull(capability, "capability");
        Objects.requireNonNull(resource, "resource");

        if (capability.getOperation() != Operation.READ) {
            // Defensive check (compile-time restriction should already prevent this).
            return new AccessOutcome<>(authorizeAndReturnResult(user, resource, Operation.READ), null);
        }

        AccessResult result = authorizer.authorize(user, resource, Operation.READ);
        if (!result.isAllowed()) {
            return new AccessOutcome<>(result, null);
        }
        return new AccessOutcome<>(result, resource.getContent());
    }

    public AccessOutcome<Void> write(User user, Capability<Write> capability, Resource resource, String newContent) {
        Objects.requireNonNull(capability, "capability");
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(newContent, "newContent");

        if (capability.getOperation() != Operation.WRITE) {
            // Defensive check (compile-time restriction should already prevent this).
            return new AccessOutcome<>(authorizeAndReturnResult(user, resource, Operation.WRITE), null);
        }

        AccessResult result = authorizer.authorize(user, resource, Operation.WRITE);
        if (!result.isAllowed()) {
            return new AccessOutcome<>(result, null);
        }
        resource.setContent(newContent);
        return new AccessOutcome<>(result, null);
    }

    private AccessResult authorizeAndReturnResult(User user, Resource resource, Operation operation) {
        return authorizer.authorize(user, resource, operation);
    }
}

