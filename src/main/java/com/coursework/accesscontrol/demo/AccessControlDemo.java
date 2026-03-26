package com.coursework.accesscontrol.demo;

import com.coursework.accesscontrol.auth.LoggingAccessAuthorizer;
import com.coursework.accesscontrol.auth.RuleChainAccessAuthorizer;
import com.coursework.accesscontrol.capability.CapabilityFactory;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.log.AccessLog;
import com.coursework.accesscontrol.model.AccessScope;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;
import com.coursework.accesscontrol.auth.AccessAuthorizer;
import com.coursework.accesscontrol.auth.AccessOutcome;
import com.coursework.accesscontrol.auth.rules.AdminAccessRule;
import com.coursework.accesscontrol.auth.rules.GuestAccessRule;
import com.coursework.accesscontrol.auth.rules.StaffAccessRule;
import com.coursework.accesscontrol.auth.rules.StudentAccessRule;
import com.coursework.accesscontrol.service.SecureResourceService;

import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class AccessControlDemo {
    public static void main(String[] args) {
        // Formatter matches the coursework logging style: "20-01-2025 16:00, ..."
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm");
        Clock clock = Clock.system(ZoneId.of("Europe/London"));

        AccessLog log = new AccessLog(clock, formatter);

        AccessAuthorizer authorizer = new RuleChainAccessAuthorizer(
                java.util.List.of(
                        new GuestAccessRule(),
                        new StudentAccessRule(),
                        new StaffAccessRule(),
                        new AdminAccessRule()
                )
        );
        authorizer = new LoggingAccessAuthorizer(authorizer, log);

        SecureResourceService service = new SecureResourceService(authorizer);
        CapabilityFactory capabilityFactory = new CapabilityFactory();

        // Resources
        Resource printer = new Resource("Printer", AccessScope.PUBLIC, "Printer status: OK");
        Resource lecture = new Resource("Lecture Material", AccessScope.INTERNAL, "Lecture Material: Topic A slides");
        Resource examPaper = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "Exam Paper: Midterm Q1..Q5");

        // Users (3 different roles)
        User guest = new User("user1", Role.GUEST);
        User student = new User("user2", Role.STUDENT);
        User staff = new User("user3", Role.STAFF);

        // Capabilities
        var readCap = capabilityFactory.createReadCapability();
        var writeCap = capabilityFactory.createWriteCapability();

        System.out.println("=== Scenario: Access Allow/Deny ===");

        // Guest allow (PUBLIC READ)
        printRead(service.read(guest, readCap, printer));
        // Guest deny (CONFIDENTIAL READ)
        printRead(service.read(guest, readCap, examPaper));

        // Student allow (INTERNAL READ)
        printRead(service.read(student, readCap, lecture));
        // Student deny (writes are refused by policy)
        printWrite(service.write(student, writeCap, examPaper, "UPDATED EXAM PAPER CONTENT"));

        // Staff allow (INTERNAL READ)
        printRead(service.read(staff, readCap, lecture));
        // Staff deny (CONFIDENTIAL READ)
        printRead(service.read(staff, readCap, examPaper));

        System.out.println();
        System.out.println("=== Access Log ===");
        for (String line : log.toLogLines()) {
            System.out.println(line);
        }
    }

    private static void printRead(AccessOutcome<String> outcome) {
        if (outcome.getAccessResult().getDecision() == AccessDecision.ALLOW) {
            System.out.println("[ALLOW][READ] content=" + outcome.getValue());
        } else {
            System.out.println("[REFUSE][READ] content=null");
        }
    }

    private static void printWrite(AccessOutcome<Void> outcome) {
        if (outcome.getAccessResult().getDecision() == AccessDecision.ALLOW) {
            System.out.println("[ALLOW][WRITE] content=updated");
        } else {
            System.out.println("[REFUSE][WRITE] content=not updated");
        }
    }
}

