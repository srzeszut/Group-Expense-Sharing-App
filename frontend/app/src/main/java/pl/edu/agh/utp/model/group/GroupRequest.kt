package pl.edu.agh.utp.model.group

import java.util.UUID

data class GroupRequest(
    val name: String,
    val userId: UUID
)
