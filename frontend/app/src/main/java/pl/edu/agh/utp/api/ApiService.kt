package pl.edu.agh.utp.api

import pl.edu.agh.utp.model.Reimbursement
import pl.edu.agh.utp.model.category.Category
import pl.edu.agh.utp.model.graph.TransactionsGraph
import pl.edu.agh.utp.model.group.Group
import pl.edu.agh.utp.model.group.GroupRequest
import pl.edu.agh.utp.model.transaction.SimpleTransaction
import pl.edu.agh.utp.model.transaction.Transaction
import pl.edu.agh.utp.model.transaction.TransactionRequest
import pl.edu.agh.utp.model.user.LoginRequest
import pl.edu.agh.utp.model.user.RegisterRequest
import pl.edu.agh.utp.model.user.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface ApiService {

    @POST("groups/{id}/users")
    fun addUsersToGroup(@Path("id") groupId: UUID, @Body userIds: List<String>): Call<Group>

    @GET("groups/{id}/users")
    fun getUsersFromGroup(@Path("id") groupId: UUID): Call<List<User>>

    @POST("groups")
    fun createGroup(@Body groupCreationRequest: GroupRequest): Call<Group>

    @GET("users/{id}/groups")
    fun getUserGroups(@Path("id") userId: UUID): Call<List<Group>>

    @GET("groups/{id}/transactions")
    fun getTransactions(@Path("id") groupId: UUID): Call<List<SimpleTransaction>>

    @GET("transactions/{id}")
    fun getTransaction(@Path("id") transactionId:UUID): Call<Transaction>

    @POST("groups/{id}/transactions")
    fun addTransaction(
            @Path("id") groupId: UUID,
            @Body transaction: TransactionRequest
    ): Call<Transaction>

    @POST("/users/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<User>

    @POST("/users")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<User>

    @GET("/groups/{id}/reimbursements")
    fun getReimbursements(@Path("id") groupId: UUID): Call<List<Reimbursement>>

    @POST("/groups/{id}/transactions/categories")
    fun filterTransactionsByCategory(@Path("id") groupId: UUID, @Body category: List<Category>): Call<List<SimpleTransaction>>

    @POST("groups/{id}/reimbursements/categories")
    fun getReimbursements(@Path("id") groupId: UUID,@Body category: List<Category>): Call<List<Reimbursement>>

    @GET("groups/{id}/categories")
    fun getCategoriesByGroup(@Path("id") groupId: UUID): Call<List<Category>>

    @GET("groups/{id}/graph")
    fun getGraph(@Path("id") groupId: UUID, @Query("merge") merge: Boolean): Call<TransactionsGraph>

    @POST("groups/{id}/graph/users")
    fun getGraphWithUsers(@Path("id") groupId: UUID, @Body users: List<User>, @Query("merge") merge: Boolean): Call<TransactionsGraph>
}