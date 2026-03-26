package com.coursework.accesscontrol.log;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class AccessLog {
    private final Clock clock;
    private final DateTimeFormatter formatter;
    private final List<AccessLogEntry> entries = new ArrayList<>();

    public AccessLog(Clock clock, DateTimeFormatter formatter) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
    }

    public Clock getClock() {
        return clock;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void record(AccessLogEntry entry) {
        entries.add(Objects.requireNonNull(entry, "entry"));
    }

    public List<AccessLogEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public List<String> toLogLines() {
        List<String> lines = new ArrayList<>(entries.size());
        for (AccessLogEntry entry : entries) {
            lines.add(entry.toLogLine(formatter));
        }
        return lines;
    }
}

