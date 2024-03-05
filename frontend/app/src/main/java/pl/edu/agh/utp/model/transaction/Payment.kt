package pl.edu.agh.utp.model.transaction

import pl.edu.agh.utp.model.user.User

data class Payment(
    val user: User,
    val amount: Double
)

