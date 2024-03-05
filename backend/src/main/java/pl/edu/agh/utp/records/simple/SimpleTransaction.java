package pl.edu.agh.utp.records.simple;

import pl.edu.agh.utp.model.nodes.Transaction;

import java.util.UUID;

public record SimpleTransaction(UUID transactionId, String description, String date) {

    public static SimpleTransaction fromTransaction(Transaction transaction) {
        return new SimpleTransaction(transaction.getId(), transaction.getDescription(), transaction.getDate());
    }
}
