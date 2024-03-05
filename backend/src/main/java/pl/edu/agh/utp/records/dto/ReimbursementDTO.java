package pl.edu.agh.utp.records.dto;

import pl.edu.agh.utp.records.Reimbursement;

public record ReimbursementDTO(UserDTO debtor, UserDTO creditor, double amount) {
  public static ReimbursementDTO fromReimbursement(Reimbursement reimbursement) {
    return new ReimbursementDTO(
        UserDTO.fromUser(reimbursement.toUser()),
        UserDTO.fromUser(reimbursement.fromUser()),

        reimbursement.amount());
  }
}
