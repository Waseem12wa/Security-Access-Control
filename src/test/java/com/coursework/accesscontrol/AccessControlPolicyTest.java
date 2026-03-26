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

public class AccessControlPolicyTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm");
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-20T16:00:00Z"), ZoneId.of("Europe/London"));

    private SecureResourceService buildService(AccessLog log) {
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
    void studentAllowedReadInternal() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Read> readCap = factory.createReadCapability();
        assertEquals(Operation.READ, readCap.getOperation());

        User student = new User("user2", Role.STUDENT);
        Resource lecture = new Resource("Lecture Material", AccessScope.INTERNAL, "Lecture: Slides content");

        AccessOutcome<String> outcome = service.read(student, readCap, lecture);
        assertEquals(AccessDecision.ALLOW, outcome.getAccessResult().getDecision());
        assertEquals("Lecture: Slides content", outcome.getValue());

        assertEquals(1, log.getEntries().size());
        assertEquals(AccessDecision.ALLOW, log.getEntries().get(0).getDecision());
        assertEquals("user2", log.getEntries().get(0).getUserId());
    }

    @Test
    void studentDeniedWriteConfidentialAndLogged() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Write> writeCap = factory.createWriteCapability();
        assertEquals(Operation.WRITE, writeCap.getOperation());

        User student = new User("user2", Role.STUDENT);
        Resource exam = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "Old content");

        AccessOutcome<Void> outcome = service.write(student, writeCap, exam, "New confidential content");
        assertEquals(AccessDecision.REFUSE, outcome.getAccessResult().getDecision());
        assertNull(outcome.getValue());
        assertEquals("Old content", exam.getContent(), "Policy must prevent modification on refusal");

        assertEquals(1, log.getEntries().size());
        assertEquals(AccessDecision.REFUSE, log.getEntries().get(0).getDecision());
        assertEquals("Exam Paper", log.getEntries().get(0).getResourceName());
        assertEquals("user2", log.getEntries().get(0).getUserId());
    }

    @Test
    void guestDeniedReadConfidentialAndLoggedFormat() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Read> readCap = factory.createReadCapability();

        User guest = new User("user1", Role.GUEST);
        Resource exam = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "Secret exam content");

        AccessOutcome<String> outcome = service.read(guest, readCap, exam);
        assertEquals(AccessDecision.REFUSE, outcome.getAccessResult().getDecision());
        assertNull(outcome.getValue());

        assertEquals(1, log.getEntries().size());
        assertEquals("20-01-2025 16:00, user1, GUEST, Exam Paper, READ, REFUSE",
                log.getEntries().get(0).toLogLine(FORMATTER));
    }

    @Test
    void adminAllowsReadWriteConfidential_updatesContentAndLogs() {
        AccessLog log = new AccessLog(FIXED_CLOCK, FORMATTER);
        SecureResourceService service = buildService(log);
        CapabilityFactory factory = new CapabilityFactory();

        Capability<Read> readCap = factory.createReadCapability();
        Capability<Write> writeCap = factory.createWriteCapability();

        User admin = new User("user4", Role.ADMIN);
        Resource exam = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "Old content");

        AccessOutcome<Void> writeOutcome = service.write(admin, writeCap, exam, "Updated exam content");
        assertEquals(AccessDecision.ALLOW, writeOutcome.getAccessResult().getDecision());
        assertEquals("Updated exam content", exam.getContent());

        AccessOutcome<String> readOutcome = service.read(admin, readCap, exam);
        assertEquals(AccessDecision.ALLOW, readOutcome.getAccessResult().getDecision());
        assertEquals("Updated exam content", readOutcome.getValue());

        // 2 actions -> 2 log entries
        assertEquals(2, log.getEntries().size());
        assertEquals(AccessDecision.ALLOW, log.getEntries().get(0).getDecision());
        assertEquals(AccessDecision.ALLOW, log.getEntries().get(1).getDecision());
    }
}

