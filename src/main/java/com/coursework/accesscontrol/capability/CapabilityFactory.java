package com.coursework.accesscontrol.capability;

import com.coursework.accesscontrol.model.Operation;

public final class CapabilityFactory {

    public Capability<Read> createReadCapability() {
        return new Capability<>(Operation.READ, Read.class);
    }

    public Capability<Write> createWriteCapability() {
        return new Capability<>(Operation.WRITE, Write.class);
    }
}

