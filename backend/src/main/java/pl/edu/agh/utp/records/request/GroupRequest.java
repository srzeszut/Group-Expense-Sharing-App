package pl.edu.agh.utp.records.request;

import java.util.UUID;

public record GroupRequest(String name, UUID userId) {}
