package pl.edu.agh.utp.records;

import pl.edu.agh.utp.model.nodes.User;

public record Reimbursement(User toUser, User fromUser, double amount) {}
