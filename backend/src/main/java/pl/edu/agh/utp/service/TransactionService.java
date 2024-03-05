package pl.edu.agh.utp.service;

import io.vavr.control.Either;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.utp.model.nodes.Category;
import pl.edu.agh.utp.model.nodes.Transaction;
import pl.edu.agh.utp.model.relationships.Debt;
import pl.edu.agh.utp.model.relationships.Payment;
import pl.edu.agh.utp.records.dto.PaymentDTO;
import pl.edu.agh.utp.records.request.TransactionRequest;
import pl.edu.agh.utp.repository.CategoryRepository;
import pl.edu.agh.utp.repository.TransactionRepository;
import pl.edu.agh.utp.repository.UserRepository;

@Service
@AllArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  public Optional<Transaction> findTransactionById(UUID id) {
    return transactionRepository.findById(id);
  }

  @Transactional
  public Either<String, Transaction> createTransactionFromRequest(
      TransactionRequest transactionRequest) {
    var category = categoryRepository.findByName(transactionRequest.category());
    var paymentUser = userRepository.findById(transactionRequest.paymentUserId());
    if (paymentUser.isEmpty()) {
      return Either.left("Payment user not found");
    }
    var debtsUsers = userRepository.findAllById(transactionRequest.debtsUserIds());

    if (debtsUsers.size() != transactionRequest.debtsUserIds().size()) {
      return Either.left("Debts users not found");
    }

    var payment = new Payment(paymentUser.get(), transactionRequest.amount());
    double amountToPay = getAmountToPay(transactionRequest);
    var debts = debtsUsers.stream().map(user -> new Debt(user, amountToPay)).toList();

    var transaction =
        new Transaction(
            transactionRequest.description(),
            transactionRequest.date(),
            category.orElse(categoryRepository.save(new Category(transactionRequest.category()))),
            payment,
            debts);
    return Either.right(transaction);
  }

  public PaymentDTO getPaymentByTransactionId(UUID transactionId) {
    return transactionRepository.findPaymentByTransactionId(transactionId);
  }

  public List<PaymentDTO> getDebtsByTransactionId(UUID transactionId) {
    return transactionRepository.findDebtsByTransactionId(transactionId);
  }

  private double getAmountToPay(TransactionRequest transactionRequest) {
    var amount = transactionRequest.amount() / transactionRequest.debtsUserIds().size();

    return round(amount, 2);
  }

  private double round(double value, int places) {
    return new BigDecimal(value).setScale(places, RoundingMode.DOWN).doubleValue();
  }

}
