package com.coursework.accesscontrol;

import com.coursework.accesscontrol.auth.AccessAuthorizer;
import com.coursework.accesscontrol.auth.AccessResult;
import com.coursework.accesscontrol.auth.RuleChainAccessAuthorizer;
import com.coursework.accesscontrol.auth.rules.GuestAccessRule;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.model.AccessScope;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RuleChainAccessAuthorizerTest {

    @Test
    void defaultFallsBackToRefuse_whenNoRuleApplies() {
        // Only GuestAccessRule exists in the chain; a STUDENT role should result in a safe REFUSE default.
        AccessAuthorizer authorizer = new RuleChainAccessAuthorizer(
                List.of(new GuestAccessRule())
        );

        User student = new User("user2", Role.STUDENT);
        Resource publicRes = new Resource("Printer", AccessScope.PUBLIC, "content");

        AccessResult result = authorizer.authorize(student, publicRes, Operation.READ);
        assertEquals(AccessDecision.REFUSE, result.getDecision());
    }

    @Test
    void ruleChainReturnsDecisionFromFirstApplicableRule() {
        // Here, GuestAccessRule should be the applicable rule for a GUEST role.
        AccessAuthorizer authorizer = new RuleChainAccessAuthorizer(
                List.of(new GuestAccessRule())
        );

        User guest = new User("user1", Role.GUEST);
        Resource publicRes = new Resource("Printer", AccessScope.PUBLIC, "content");

        AccessResult result = authorizer.authorize(guest, publicRes, Operation.READ);
        assertEquals(AccessDecision.ALLOW, result.getDecision());
    }
}

