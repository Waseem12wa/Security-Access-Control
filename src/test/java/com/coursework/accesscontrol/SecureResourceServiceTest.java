package com.coursework.accesscontrol;

import com.coursework.accesscontrol.auth.AccessAuthorizer;
import com.coursework.accesscontrol.auth.AccessOutcome;
import com.coursework.accesscontrol.auth.LoggingAccessAuthorizer;
import com.coursework.accesscontrol.auth.RuleChainAccessAuthorizer;
import com.coursework.accesscontrol.capability.Capability;
import com.coursework.accesscontrol.capability.CapabilityFactory;
import com.coursework.accesscontrol.capability.Read;
import com.coursework.accesscontrol.capability.Write;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.log.AccessLog;
import com.coursework.accesscontrol.model.AccessScope;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;
import com.coursework.accesscontrol.auth.rules.AdminAccessRule;
import com.coursework.accesscontrol.auth.rules.GuestAccessRule;
import com.coursework.accesscontrol.auth.rules.StaffAccessRule;
import com.coursework.accesscontrol.auth.rules.StudentAccessRule;
import com.coursework.accesscontrol.service.SecureResourceService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SecureResourceServiceTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm");
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-20T16:00:00Z"), ZoneId.of("Europe/London"));

    private SecureResourceService buildRealService(AccessLog log) {
        AccessAuthorizer authorizer = new RuleChainAccessAuthorizer(
                List.of(
                        new GuestAccessRule(),
                        new StudentAccessRule(),
                        new StaffAccessRule(),
                        new AdminAccessRule()
                )
        );
        authorizer = new LoggingAccessAuthorizer(authorizer, log);
        return new SecureResourceService(authorizer);
    }

    @Test
    void readAllowed_returnsContent_andDoesNotModifyResource() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildRealService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Read> readCap = factory.createReadCapability();

        User student = new User("user2", Role.STUDENT);
        Resource lecture = new Resource("Lecture Material", AccessScope.INTERNAL, "Slides content v1");

        AccessOutcome<String> outcome = service.read(student, readCap, lecture);

        assertEquals(AccessDecision.ALLOW, outcome.getAccessResult().getDecision());
        assertEquals("Slides content v1", outcome.getValue());
        assertEquals("Slides content v1", lecture.getContent());

        assertEquals(1, log.getEntries().size());
        assertEquals(AccessDecision.ALLOW, log.getEntries().get(0).getDecision());
    }

    @Test
    void writeRefused_doesNotChangeContent_andReturnsNullPayload() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildRealService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Write> writeCap = factory.createWriteCapability();

        User student = new User("user2", Role.STUDENT);
        Resource exam = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "Old content");

        AccessOutcome<Void> outcome = service.write(student, writeCap, exam, "New content");

        assertEquals(AccessDecision.REFUSE, outcome.getAccessResult().getDecision());
        assertNull(outcome.getValue());
        assertEquals("Old content", exam.getContent(), "Content must remain unchanged after refusal");
    }

    @Test
    void adminWriteAllowed_modifiesResource_andLogsAllow() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildRealService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Write> writeCap = factory.createWriteCapability();

        User admin = new User("user4", Role.ADMIN);
        Resource exam = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "Old content");

        AccessOutcome<Void> outcome = service.write(admin, writeCap, exam, "Updated exam content");
        assertEquals(AccessDecision.ALLOW, outcome.getAccessResult().getDecision());
        assertNull(outcome.getValue());
        assertEquals("Updated exam content", exam.getContent());
        assertEquals(1, log.getEntries().size());
        assertEquals(AccessDecision.ALLOW, log.getEntries().get(0).getDecision());
        assertEquals(Operation.WRITE, log.getEntries().get(0).getOperation());
    }

    @Test
    void guestReadRefused_returnsNullPayload() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildRealService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Read> readCap = factory.createReadCapability();

        User guest = new User("user1", Role.GUEST);
        Resource exam = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "Secret");

        AccessOutcome<String> outcome = service.read(guest, readCap, exam);

        assertEquals(AccessDecision.REFUSE, outcome.getAccessResult().getDecision());
        assertNull(outcome.getValue());
        assertEquals(1, log.getEntries().size());
        assertEquals(AccessDecision.REFUSE, log.getEntries().get(0).getDecision());
    }
}

