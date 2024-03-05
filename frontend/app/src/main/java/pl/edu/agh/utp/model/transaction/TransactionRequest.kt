package pl.edu.agh.utp.model.transaction

import java.util.UUID

data class TransactionRequest(
    val description: String,
    val date: String,
    val category: String,
    val amount: Double,
    val paymentUserId: UUID,
    val debtsUserIds: List<UUID>
)
