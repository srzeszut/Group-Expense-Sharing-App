package pl.edu.agh.utp.service.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pl.edu.agh.utp.records.Reimbursement;
import pl.edu.agh.utp.records.UserBalance;

public class ReimbursementCalculator {
  public static List<Reimbursement> calculateReimbursements(List<UserBalance> balances) {

    double sum = balances.stream().mapToDouble(UserBalance::balance).sum();
    if (sum != 0) {
      throw new IllegalArgumentException("Sum of balances must be equal to 0");
    }
    // split for two lists with negative and positive balances
    var partitionedBalances =
        balances.stream().collect(Collectors.partitioningBy(balance -> balance.balance() <= 0));
    List<UserBalance> negativeBalances = partitionedBalances.get(true);
    List<UserBalance> positiveBalances = partitionedBalances.get(false);

    List<Reimbursement> reimbursements = new ArrayList<>();

    for (UserBalance negativeBalance : negativeBalances) {
      double currentNegativeBalanceValue = negativeBalance.balance();
      while (currentNegativeBalanceValue < 0) {
        UserBalance positiveBalance = positiveBalances.get(0);
        double positiveBalanceValue = positiveBalance.balance();
        double debtValue = positiveBalanceValue + currentNegativeBalanceValue;
        if (debtValue <= 0) {
          reimbursements.add(
              new Reimbursement(
                  positiveBalance.user(), negativeBalance.user(), positiveBalanceValue));
          positiveBalances.remove(0);
          currentNegativeBalanceValue = debtValue;
        } else {
          reimbursements.add(
              new Reimbursement(
                  positiveBalance.user(), negativeBalance.user(), -currentNegativeBalanceValue));
          positiveBalances.set(0, new UserBalance(positiveBalance.user(), debtValue));
          currentNegativeBalanceValue = 0;
        }
      }
    }
    return reimbursements;
  }
}
