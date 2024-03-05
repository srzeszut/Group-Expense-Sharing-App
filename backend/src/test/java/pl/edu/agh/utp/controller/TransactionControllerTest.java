package pl.edu.agh.utp.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.utp.model.nodes.Category;
import pl.edu.agh.utp.model.nodes.Transaction;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.model.relationships.Debt;
import pl.edu.agh.utp.model.relationships.Payment;
import pl.edu.agh.utp.records.dto.TransactionDTO;
import pl.edu.agh.utp.service.TransactionService;

class TransactionControllerTest {
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock private TransactionService transactionService;

  @InjectMocks private TransactionController transactionController;

  @BeforeEach
  @SneakyThrows
  public void setup() {
    try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
      mockMvc = standaloneSetup(transactionController).build();
    }
  }

  @Nested
  class GetTransactionById {
    @Test
    @SneakyThrows
    public void shouldReturnOkForCorrectData() {
      var groupId = UUID.randomUUID();
      var user = new User("name", "email", "password");
      var transaction =
          new Transaction(
              "description",
              "12-12-2012",
              new Category("category"),
              new Payment(user, 1.0),
              Collections.singletonList(new Debt(user, 1.0)));
      given(transactionService.findTransactionById(groupId)).willReturn(Optional.of(transaction));

      mockMvc
          .perform(get("/transactions/" + groupId))
          .andExpect(status().isOk())
          .andExpect(
              content()
                  .json(
                      objectMapper.writeValueAsString(
                          TransactionDTO.fromTransaction(transaction))));
    }

    @Test
    @SneakyThrows
    public void shouldReturnNotFoundForIncorrectId() {
      var groupId = UUID.randomUUID();
      given(transactionService.findTransactionById(groupId)).willReturn(Optional.empty());

      mockMvc.perform(get("/transactions/" + groupId)).andExpect(status().isNotFound());
    }
  }
}
