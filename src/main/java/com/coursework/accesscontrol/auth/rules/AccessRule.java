package com.coursework.accesscontrol.auth.rules;

import com.coursework.accesscontrol.auth.AccessResult;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.User;

import java.util.Optional;

/**
 * One policy rule in a chain. Return {@link Optional#empty()} when the rule
 * does not apply (so later rules may decide).
 */
public interface AccessRule {
    Optional<AccessResult> evaluate(User user, Resource resource, Operation operation);
}

