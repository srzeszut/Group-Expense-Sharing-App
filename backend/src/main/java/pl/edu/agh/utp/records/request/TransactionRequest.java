package pl.edu.agh.utp.records.request;

import java.util.List;
import java.util.UUID;

public record TransactionRequest(
    String description,
    String date,
    String category,
    double amount,
    UUID paymentUserId,
    List<UUID> debtsUserIds) {}
