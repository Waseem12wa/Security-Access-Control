package com.coursework.accesscontrol;

import com.coursework.accesscontrol.capability.Capability;
import com.coursework.accesscontrol.capability.CapabilityFactory;
import com.coursework.accesscontrol.capability.Read;
import com.coursework.accesscontrol.capability.Write;
import com.coursework.accesscontrol.model.Operation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CapabilityFactoryTest {

    @Test
    void createReadCapability_operationReadAndTypeCorrect() {
        CapabilityFactory factory = new CapabilityFactory();
        Capability<Read> readCap = factory.createReadCapability();

        assertEquals(Operation.READ, readCap.getOperation());
        assertEquals(Read.class, readCap.getCapabilityType());
    }

    @Test
    void createWriteCapability_operationWriteAndTypeCorrect() {
        CapabilityFactory factory = new CapabilityFactory();
        Capability<Write> writeCap = factory.createWriteCapability();

        assertEquals(Operation.WRITE, writeCap.getOperation());
        assertEquals(Write.class, writeCap.getCapabilityType());
    }
}

