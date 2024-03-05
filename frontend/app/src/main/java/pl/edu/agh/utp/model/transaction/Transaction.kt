package pl.edu.agh.utp.model.transaction

import pl.edu.agh.utp.model.category.Category
import pl.edu.agh.utp.model.transaction.Debt
import pl.edu.agh.utp.model.transaction.Payment
import java.util.UUID

data class Transaction(
    val transactionId: UUID,
    val description: String,
    val date: String,
    val category: Category,
    val payment: Payment,
    val debts: List<Debt>
)