package com.coursework.accesscontrol.auth;

import com.coursework.accesscontrol.model.Operation;
import com.coursework.accesscontrol.model.Resource;
import com.coursework.accesscontrol.model.User;

public interface AccessAuthorizer {
    AccessResult authorize(User user, Resource resource, Operation operation);
}

