package com.coursework.accesscontrol;

import com.coursework.accesscontrol.auth.AccessAuthorizer;
import com.coursework.accesscontrol.auth.AccessResult;
import com.coursework.accesscontrol.auth.LoggingAccessAuthorizer;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.log.AccessLog;
import com.coursework.accesscontrol.model.AccessScope;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingAccessAuthorizerTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm");
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-20T16:00:00Z"), ZoneId.of("Europe/London"));

    @Test
    void logsAllowDecision_withCorrectFields() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);

        AccessAuthorizer stub = (user, resource, operation) ->
                new AccessResult(AccessDecision.ALLOW);

        LoggingAccessAuthorizer logging = new LoggingAccessAuthorizer(stub, log);

        User user = new User("userX", Role.STAFF);
        Resource res = new Resource("Lecture Material", AccessScope.INTERNAL, "content");
        Operation op = Operation.READ;

        AccessResult result = logging.authorize(user, res, op);

        assertEquals(AccessDecision.ALLOW, result.getDecision());
        assertEquals(1, log.getEntries().size());

        assertEquals("userX", log.getEntries().get(0).getUserId());
        assertEquals(Role.STAFF, log.getEntries().get(0).getRole());
        assertEquals("Lecture Material", log.getEntries().get(0).getResourceName());
        assertEquals(Operation.READ, log.getEntries().get(0).getOperation());
        assertEquals(AccessDecision.ALLOW, log.getEntries().get(0).getDecision());
    }

    @Test
    void logsRefuseDecision_whenStubRefuses() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);

        AccessAuthorizer stub = (user, resource, operation) ->
                new AccessResult(AccessDecision.REFUSE);

        LoggingAccessAuthorizer logging = new LoggingAccessAuthorizer(stub, log);

        User user = new User("userY", Role.GUEST);
        Resource res = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "secret");
        Operation op = Operation.READ;

        AccessResult result = logging.authorize(user, res, op);

        assertEquals(AccessDecision.REFUSE, result.getDecision());
        assertEquals(1, log.getEntries().size());
        assertEquals(AccessDecision.REFUSE, log.getEntries().get(0).getDecision());
    }
}

