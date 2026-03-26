package com.coursework.accesscontrol.auth.rules;

import com.coursework.accesscontrol.auth.AccessResult;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;

import java.util.Optional;

public final class AdminAccessRule implements AccessRule {
    @Override
    public Optional<AccessResult> evaluate(User user, Resource resource, Operation operation) {
        if (user.getRole() != Role.ADMIN) {
            return Optional.empty();
        }

        // Admin can READ and WRITE all resources regardless of scope.
        return Optional.of(new AccessResult(AccessDecision.ALLOW));
    }
}

