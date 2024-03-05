package pl.edu.agh.utp.service.helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.records.Reimbursement;
import pl.edu.agh.utp.records.UserBalance;

class ReimbursementCalculatorTest {

  @Test
  void shouldCorrectlyCalculateReimbursements() {
    // given
    var user1 = createDummyUser("user1");
    var user2 = createDummyUser("user2");
    var user1Balance = 100;
    var user2Balance = -100;
    var userBalances =
        List.of(new UserBalance(user1, user1Balance), new UserBalance(user2, user2Balance));

    // when
    var reimbursements = ReimbursementCalculator.calculateReimbursements(userBalances);

    // then
    assertThat(reimbursements).containsExactly(new Reimbursement(user1, user2, 100));
  }

  @Test
  void shouldOmitUserIfItsBalanceIsZero() {
    // given
    var user1 = createDummyUser("user1");
    var user2 = createDummyUser("user2");
    var user3 = createDummyUser("user3");
    var user1Balance = 100;
    var user2Balance = -100;
    var user3Balance = 0;

    var userBalances =
        List.of(
            new UserBalance(user1, user1Balance),
            new UserBalance(user2, user2Balance),
            new UserBalance(user3, user3Balance));

    // when
    var reimbursements = ReimbursementCalculator.calculateReimbursements(userBalances);

    // then
    assertThat(reimbursements).containsExactly(new Reimbursement(user1, user2, 100));
  }

  @Test
  void shouldCorrectlySplitReimbursements() {
    // given
    var user1 = createDummyUser("user1");
    var user2 = createDummyUser("user2");
    var user3 = createDummyUser("user3");
    var user1Balance = -200;
    var user2Balance = 100;
    var user3Balance = 100;

    var userBalances =
        List.of(
            new UserBalance(user1, user1Balance),
            new UserBalance(user2, user2Balance),
            new UserBalance(user3, user3Balance));

    // when
    var reimbursements = ReimbursementCalculator.calculateReimbursements(userBalances);

    // then
    assertThat(reimbursements)
        .containsExactly(
            new Reimbursement(user2, user1, 100), new Reimbursement(user3, user1, 100));
  }

  @Test
  void shouldThrowExceptionWhenSumOfBalancesIsNotZero() {
    // given
    var user1 = createDummyUser("user1");
    var user2 = createDummyUser("user2");
    var user1Balance = 100;
    var user2Balance = -50;
    var userBalances =
        List.of(new UserBalance(user1, user1Balance), new UserBalance(user2, user2Balance));

    // when then
    assertThatThrownBy(() -> ReimbursementCalculator.calculateReimbursements(userBalances))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Sum of balances must be equal to 0");
  }

  private static User createDummyUser(String name) {
    return new User(name, name, name);
  }
}
