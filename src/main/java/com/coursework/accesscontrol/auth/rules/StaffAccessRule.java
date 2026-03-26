package com.coursework.accesscontrol.auth.rules;

import com.coursework.accesscontrol.auth.AccessResult;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.model.AccessScope;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;

import java.util.Optional;

public final class StaffAccessRule implements AccessRule {
    @Override
    public Optional<AccessResult> evaluate(User user, Resource resource, Operation operation) {
        if (user.getRole() != Role.STAFF) {
            return Optional.empty();
        }

        // Staff can READ PUBLIC and INTERNAL, but cannot WRITE.
        if (operation == Operation.READ && (resource.getScope() == AccessScope.PUBLIC || resource.getScope() == AccessScope.INTERNAL)) {
            return Optional.of(new AccessResult(AccessDecision.ALLOW));
        }
        return Optional.of(new AccessResult(AccessDecision.REFUSE));
    }
}

