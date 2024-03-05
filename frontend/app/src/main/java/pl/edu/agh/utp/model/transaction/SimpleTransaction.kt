package pl.edu.agh.utp.model.transaction

import java.util.UUID

data class SimpleTransaction(val transactionId: UUID, val description: String, val date: String)