package pl.edu.agh.utp.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.utp.model.nodes.Transaction;
import pl.edu.agh.utp.records.dto.PaymentDTO;

@Repository
public interface TransactionRepository extends Neo4jRepository<Transaction, UUID> {

    @Query(
        """
            MATCH (t:Transaction)-[p:MADE_PAYMENT]->(u:User)
            WHERE t.id = $transactionId
            WITH u.id as userId, u.name as name, u.email as email, p.amount as amount
            RETURN {userId: userId, name: name, email: email} as user, amount
        """
    )
    PaymentDTO findPaymentByTransactionId(@Param("transactionId") UUID transactionId);

    @Query(
        """
            MATCH (t:Transaction)-[o:OWES]->(u:User)
            WHERE t.id = $transactionId
            WITH u.id as userId, u.name as name, u.email as email, o.amount as amount
            RETURN {userId: userId, name: name, email: email} as user, amount
        """
    )
    List<PaymentDTO> findDebtsByTransactionId(@Param("transactionId") UUID transactionId);

}
