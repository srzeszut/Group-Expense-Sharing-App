package pl.edu.agh.utp.service;

import io.vavr.control.Either;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.utp.model.nodes.Category;
import pl.edu.agh.utp.model.nodes.Group;
import pl.edu.agh.utp.model.nodes.Transaction;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.records.TransactionsGraph;
import pl.edu.agh.utp.records.Reimbursement;
import pl.edu.agh.utp.records.UserBalance;
import pl.edu.agh.utp.records.dto.TransactionDTO;
import pl.edu.agh.utp.records.dto.UserDTO;
import pl.edu.agh.utp.records.request.GroupRequest;
import pl.edu.agh.utp.records.request.TransactionRequest;
import pl.edu.agh.utp.records.simple.SimpleGroup;
import pl.edu.agh.utp.records.simple.SimpleTransaction;
import pl.edu.agh.utp.repository.GroupRepository;
import pl.edu.agh.utp.repository.UserRepository;
import pl.edu.agh.utp.service.helpers.GraphConstructor;
import pl.edu.agh.utp.service.helpers.ReimbursementCalculator;

@Service
@AllArgsConstructor
public class GroupService {
  private final GroupRepository groupRepository;

  private final UserRepository userRepository;

  private final TransactionService transactionService;

  @Transactional
  public Optional<SimpleGroup> createGroup(GroupRequest request) {
    return userRepository
        .findById(request.userId())
        .map(
            user ->
                new Group(request.name(), Collections.singletonList(user), Collections.emptyList()))
        .map(groupRepository::save)
        .map(SimpleGroup::fromGroup);
  }

  public List<SimpleTransaction> getAllTransactionsByGroupId(UUID groupId) {
    return groupRepository.findAllTransactionsByGroupId(groupId);
  }

  public List<User> getAllUsersByGroupId(UUID groupId) {
    return groupRepository.findAllUsersByGroupId(groupId);
  }

  @Transactional
  public Either<String, Transaction> addTransactionToGroup(
      UUID groupId, TransactionRequest transactionRequest) {

    return groupRepository
        .findById(groupId)
        .map(group -> processGroupWithTransaction(group, transactionRequest))
        .orElse(Either.left("Invalid group userId"));
  }

  private Either<String, Transaction> processGroupWithTransaction(
      Group group, TransactionRequest transactionRequest) {

    var transactionEither = transactionService.createTransactionFromRequest(transactionRequest);
    return transactionEither
        .map(
            transaction -> {
              group.getTransactions().add(transaction);
              groupRepository.save(group);
              return transaction;
            })
        .orElse(() -> Either.left(transactionEither.getLeft()));
  }

  @Transactional
  public Optional<Group> addUsersToGroup(UUID groupId, List<String> emails) {
    return groupRepository.findById(groupId).map(group -> updateGroupWithUsers(group, emails));
  }

  private Group updateGroupWithUsers(Group group, List<String> emails) {
    List<User> usersToAdd = userRepository.findAllByEmail(emails);
    group.getUsers().addAll(usersToAdd);
    return groupRepository.save(group);
  }

  @Transactional
  public List<UserBalance> getAllBalancesByGroupId(UUID groupId) {
    return groupRepository.findAllBalancesByGroupId(groupId);
  }

  @Transactional
  public List<Reimbursement> getReimbursementsByGroupId(UUID groupId) {
    List<UserBalance> balances = getAllBalancesByGroupId(groupId);
    return ReimbursementCalculator.calculateReimbursements(balances);
  }

  @Transactional
  public List<UserBalance> getBalancesByGroupIdAndCategory(
      UUID groupId, List<Category> categories) {
    return groupRepository.findBalancesByGroupIdAndCategory(
        groupId, categories.stream().map(Category::getName).toList());
  }

  @Transactional
  public List<Reimbursement> getReimbursementsByGroupIdAndCategory(
      UUID groupId, List<Category> categories) {
    List<UserBalance> balances = getBalancesByGroupIdAndCategory(groupId, categories);
    return ReimbursementCalculator.calculateReimbursements(balances);
  }

  public Optional<Group> findGroupById(UUID groupId) {
    return groupRepository.findById(groupId);
  }

  public List<Category> getCategoriesByGroupId(UUID groupId) {
    return groupRepository.findCategoriesByGroupId(groupId);
  }

  public List<SimpleTransaction> getTransactionsByGroupIdAndCategories(
      UUID groupId, List<Category> categories) {
    return groupRepository.findAllTransactionsByGroupIdAndCategories(
        groupId, categories.stream().map(Category::getName).toList());
  }

  public TransactionsGraph getTransactionGraph(UUID groupId, boolean merge) {
    List<UserDTO> users = groupRepository.findAllUsersByGroupId(groupId)
            .stream().map(UserDTO::fromUser).toList();
    List<SimpleTransaction> simpleTransactions = groupRepository.findAllTransactionsByGroupId(groupId);
    List<TransactionDTO> transactions = simpleTransactions.stream().map( transaction -> new TransactionDTO(
            transaction.transactionId(),
            transaction.description(),
            transaction.date(),
            null,
            transactionService.getPaymentByTransactionId(transaction.transactionId()),
            transactionService.getDebtsByTransactionId(transaction.transactionId())
    )).toList();
    System.out.println(transactions);
    return GraphConstructor.constructGraph(users, transactions, merge);
  }

  public TransactionsGraph getTransactionGraphWithUsers(UUID groupId, List<UserDTO> users, boolean merge) {
    List<SimpleTransaction> simpleTransactions = groupRepository.findAllTransactionsByGroupIdAndUsers(
            groupId, users.stream().map(UserDTO::userId).toList());
    List<TransactionDTO> transactions = simpleTransactions.stream().map( transaction -> new TransactionDTO(
            transaction.transactionId(),
            transaction.description(),
            transaction.date(),
            null,
            transactionService.getPaymentByTransactionId(transaction.transactionId()),
            transactionService.getDebtsByTransactionId(transaction.transactionId())
    )).toList();
    return GraphConstructor.constructGraph(users, transactions, merge);
  }
}
