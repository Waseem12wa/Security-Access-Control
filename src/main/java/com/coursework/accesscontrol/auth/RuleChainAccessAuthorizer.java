package com.coursework.accesscontrol.auth;

import com.coursework.accesscontrol.auth.rules.AccessRule;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Evaluates access by trying a chain of role-specific rules and taking the first decision.
 */
public final class RuleChainAccessAuthorizer implements AccessAuthorizer {
    private final List<AccessRule> rules;

    public RuleChainAccessAuthorizer(List<AccessRule> rules) {
        this.rules = List.copyOf(Objects.requireNonNull(rules, "rules"));
    }

    @Override
    public AccessResult authorize(User user, Resource resource, Operation operation) {
        for (AccessRule rule : rules) {
            Optional<AccessResult> decision = rule.evaluate(user, resource, operation);
            if (decision.isPresent()) {
                return decision.get();
            }
        }
        return new AccessResult(AccessDecision.REFUSE);
    }
}

