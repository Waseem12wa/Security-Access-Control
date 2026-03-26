package com.coursework.accesscontrol;

import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.log.AccessLogEntry;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccessLogEntryTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm");
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-20T16:00:00Z"), ZoneId.of("Europe/London"));

    @Test
    void toLogLine_includesAllRequiredFieldsAndFormatsTimestamp() {
        User user = new User("user2", Role.STUDENT);
        Resource resource = new Resource("Exam Paper", AccessScope.CONFIDENTIAL, "old");
        Operation op = Operation.READ;
        AccessDecision decision = AccessDecision.REFUSE;

        AccessLogEntry entry = new AccessLogEntry(FIXED_CLOCK, user, resource, op, decision);

        assertEquals(
                "20-01-2025 16:00, user2, STUDENT, Exam Paper, READ, REFUSE",
                entry.toLogLine(FORMATTER)
        );
    }
}

