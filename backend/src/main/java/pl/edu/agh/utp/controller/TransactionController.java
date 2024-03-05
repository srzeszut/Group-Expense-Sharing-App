package pl.edu.agh.utp.controller;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.utp.records.dto.TransactionDTO;
import pl.edu.agh.utp.service.TransactionService;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping("/{id}")
  public TransactionDTO getTransactionById(@PathVariable("id") UUID id) {
    return transactionService
        .findTransactionById(id)
        .map(TransactionDTO::fromTransaction)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
  }
}
