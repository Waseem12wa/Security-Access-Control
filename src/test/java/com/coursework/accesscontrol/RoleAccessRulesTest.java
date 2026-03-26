package com.coursework.accesscontrol;

import com.coursework.accesscontrol.auth.AccessResult;
import com.coursework.accesscontrol.auth.rules.AdminAccessRule;
import com.coursework.accesscontrol.auth.rules.AccessRule;
import com.coursework.accesscontrol.auth.rules.GuestAccessRule;
import com.coursework.accesscontrol.auth.rules.StaffAccessRule;
import com.coursework.accesscontrol.auth.rules.StudentAccessRule;
import com.coursework.accesscontrol.log.AccessDecision;
import com.coursework.accesscontrol.model.AccessScope;
import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.Role;
import com.coursework.accesscontrol.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RoleAccessRulesTest {

    private User user(Role role) {
        return new User("u-" + role, role);
    }

    private Resource resource(String name, AccessScope scope) {
        return new Resource(name, scope, "content");
    }

    @Test
    void guestRule_onlyAppliesToGuest_andAllowsOnlyReadPublic() {
        AccessRule guestRule = new GuestAccessRule();

        Optional<AccessResult> notApplicable =
                guestRule.evaluate(user(Role.STUDENT), resource("Lecture", AccessScope.PUBLIC), Operation.READ);
        assertTrue(notApplicable.isEmpty(), "Guest rule should be empty for non-GUEST roles");

        Optional<AccessResult> allowPublic =
                guestRule.evaluate(user(Role.GUEST), resource("Printer", AccessScope.PUBLIC), Operation.READ);
        assertEquals(AccessDecision.ALLOW, allowPublic.get().getDecision());

        Optional<AccessResult> refuseInternal =
                guestRule.evaluate(user(Role.GUEST), resource("Lecture", AccessScope.INTERNAL), Operation.READ);
        assertEquals(AccessDecision.REFUSE, refuseInternal.get().getDecision());

        Optional<AccessResult> refuseConfidential =
                guestRule.evaluate(user(Role.GUEST), resource("Exam Paper", AccessScope.CONFIDENTIAL), Operation.READ);
        assertEquals(AccessDecision.REFUSE, refuseConfidential.get().getDecision());

        Optional<AccessResult> refuseWrite =
                guestRule.evaluate(user(Role.GUEST), resource("Printer", AccessScope.PUBLIC), Operation.WRITE);
        assertEquals(AccessDecision.REFUSE, refuseWrite.get().getDecision());
    }

    @Test
    void studentRule_allowsReadPublicAndInternal_refusesConfidentialAndWrite() {
        AccessRule studentRule = new StudentAccessRule();

        Optional<AccessResult> allowPublic =
                studentRule.evaluate(user(Role.STUDENT), resource("Printer", AccessScope.PUBLIC), Operation.READ);
        assertEquals(AccessDecision.ALLOW, allowPublic.get().getDecision());

        Optional<AccessResult> allowInternal =
                studentRule.evaluate(user(Role.STUDENT), resource("Lecture", AccessScope.INTERNAL), Operation.READ);
        assertEquals(AccessDecision.ALLOW, allowInternal.get().getDecision());

        Optional<AccessResult> refuseConfidentialRead =
                studentRule.evaluate(user(Role.STUDENT), resource("Exam Paper", AccessScope.CONFIDENTIAL), Operation.READ);
        assertEquals(AccessDecision.REFUSE, refuseConfidentialRead.get().getDecision());

        Optional<AccessResult> refuseWritePublic =
                studentRule.evaluate(user(Role.STUDENT), resource("Printer", AccessScope.PUBLIC), Operation.WRITE);
        assertEquals(AccessDecision.REFUSE, refuseWritePublic.get().getDecision());
    }

    @Test
    void staffRule_allowsReadPublicAndInternal_refusesConfidential_andAllWrites() {
        AccessRule staffRule = new StaffAccessRule();

        Optional<AccessResult> allowPublic =
                staffRule.evaluate(user(Role.STAFF), resource("Printer", AccessScope.PUBLIC), Operation.READ);
        assertEquals(AccessDecision.ALLOW, allowPublic.get().getDecision());

        Optional<AccessResult> allowInternal =
                staffRule.evaluate(user(Role.STAFF), resource("Lecture", AccessScope.INTERNAL), Operation.READ);
        assertEquals(AccessDecision.ALLOW, allowInternal.get().getDecision());

        Optional<AccessResult> refuseConfidentialRead =
                staffRule.evaluate(user(Role.STAFF), resource("Exam Paper", AccessScope.CONFIDENTIAL), Operation.READ);
        assertEquals(AccessDecision.REFUSE, refuseConfidentialRead.get().getDecision());

        Optional<AccessResult> refuseWrite =
                staffRule.evaluate(user(Role.STAFF), resource("Lecture", AccessScope.INTERNAL), Operation.WRITE);
        assertEquals(AccessDecision.REFUSE, refuseWrite.get().getDecision());
    }

    @Test
    void adminRule_allowsReadAndWriteForAllScopes() {
        AdminAccessRule adminRule = new AdminAccessRule();

        Optional<AccessResult> allowReadPublic =
                adminRule.evaluate(user(Role.ADMIN), resource("Printer", AccessScope.PUBLIC), Operation.READ);
        assertEquals(AccessDecision.ALLOW, allowReadPublic.get().getDecision());

        Optional<AccessResult> allowWriteConfidential =
                adminRule.evaluate(user(Role.ADMIN), resource("Exam Paper", AccessScope.CONFIDENTIAL), Operation.WRITE);
        assertEquals(AccessDecision.ALLOW, allowWriteConfidential.get().getDecision());
    }
}

