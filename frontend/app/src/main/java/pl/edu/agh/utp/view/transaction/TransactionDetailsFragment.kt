package pl.edu.agh.utp.view.transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.edu.agh.utp.R
import pl.edu.agh.utp.api.ApiObject
import pl.edu.agh.utp.model.transaction.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class TransactionDetailsFragment(private val transactionId: UUID) : Fragment() {

    private val apiService = ApiObject.instance
    private var transaction: Transaction? = null


    private lateinit var textViewDescription: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var textViewUsers: TextView
    private lateinit var textViewDebts: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        textViewDescription = view.findViewById(R.id.textViewDescription)
        textViewDate = view.findViewById(R.id.textViewDate)
        textViewCategory = view.findViewById(R.id.textViewCategory)
        textViewUsers = view.findViewById(R.id.textViewUsers)
        textViewDebts = view.findViewById(R.id.textViewDebts)

        fetchTransactionDetails()
    }

    private fun fetchTransactionDetails() {
        apiService.getTransaction(transactionId).enqueue(object : Callback<Transaction> {
            override fun onResponse(
                call: Call<Transaction>,
                response: Response<Transaction>
            ) {
                if (response.isSuccessful) {
                    transaction = response.body()
                    Log.d("TransactionDetailsFragment", "Transaction: $transaction")
                    updateTransactionDetails(transaction)
                } else {
                    Log.d("TransactionDetailsFragment", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Log.d("TransactionDetailsFragment", "Error: ${t.message}")
            }
        })
    }


    private fun updateTransactionDetails(transaction: Transaction?) {
        transaction?.let {
            textViewDescription.text = "Description: ${it.description}"
            textViewDate.text = "Date: ${it.date}"
            textViewCategory.text = "Category: ${it.category.name}"

            it.payment.let { payment ->
                val user = payment.user
                textViewUsers.text = "User: ${user.name} - Amount: ${payment.amount}"
            }

            it.debts.let { debts ->
                val debtsText = debts.joinToString("\n") { debt ->
                    "User: ${debt.user.name} - Amount: ${debt.amount}"
                }
                textViewDebts.text = "Debts:\n$debtsText"
            }
        }
    }
}
