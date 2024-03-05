package pl.edu.agh.utp.viewmodel

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import pl.edu.agh.utp.api.ApiObject

import pl.edu.agh.utp.databinding.ItemSummaryBinding
import pl.edu.agh.utp.model.Reimbursement
import pl.edu.agh.utp.model.category.Category
import pl.edu.agh.utp.model.transaction.SimpleTransaction
import pl.edu.agh.utp.model.user.User
import java.util.UUID
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SummaryRecyclerViewAdapter( private val groupId: UUID

) : RecyclerView.Adapter<SummaryRecyclerViewAdapter.ViewHolder>() {

    private val apiService = ApiObject.instance
    private val users = List(4) { User(UUID.randomUUID(), "User $it", "user$it@example.com") }

//    private val reimbursments: List<Reimbursement> = listOf(
//        Reimbursement(users[0], users[1], 50.0),
//        Reimbursement(users[2], users[3], 30.0),
//        Reimbursement(users[1], users[2], 20.0),
//        Reimbursement(users[3], users[0], 40.0)
//    )//
    private val reimbursements: MutableList<Reimbursement> =mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reimbursements[position]
        holder.debtorTextView.text = item.debtor.name
        holder.creditorTextView.text = item.creditor.name
        holder.amountTextView.text = "OWES: "+item.amount.toString() + " PLN" + " TO: "
    }


    override fun getItemCount(): Int = reimbursements.size

    inner class ViewHolder(binding: ItemSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        val debtorTextView: TextView = binding.debtorTextView
        val creditorTextView: TextView = binding.creditorTextView
        val amountTextView: TextView = binding.amountTextView

        override fun toString(): String {
            return super.toString() + " '" + debtorTextView.text + "'"
        }
    }

    public fun fetchReimburesments() {
        apiService.getReimbursements(groupId).enqueue(object : Callback<List<Reimbursement>> {
            override fun onResponse(
                call: Call<List<Reimbursement>>,
                response: Response<List<Reimbursement>>
            ) {
                if (response.isSuccessful) {
                    Log.i("Reimbursments", "Success: ${response.body()}")
                    val newReimbursements = response.body() ?: emptyList()
                    reimbursements.clear()
                    reimbursements.addAll(newReimbursements)
                    notifyDataSetChanged()

                } else {
                    Log.e("Reimbursments", "Failure: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Reimbursement>>, t: Throwable) {
                Log.e("Reimbursments", "Failure: ${t.message}")
            }
        })
    }

    fun fetchReimbursementsByCategories(categories: List<Category>) {
        Log.i("ReimbursementsByCategories", categories.toString())
        apiService.getReimbursements(groupId, categories).enqueue(object : Callback<List<Reimbursement>> {
            override fun onResponse(
                call: Call<List<Reimbursement>>,
                response: Response<List<Reimbursement>>
            ) {
                Log.i("ReimbursementsByCategories", categories.toString())
                if (response.isSuccessful) {
                    Log.i("ReimbursementsByCategories", "Success: ${response.body()}")
                    val newReimbursements = response.body() ?: emptyList()
                    reimbursements.clear()
                    reimbursements.addAll(newReimbursements)
                    notifyDataSetChanged()

                } else {
                    Log.e("ReimbursementsByCategories", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Reimbursement>>, t: Throwable) {
                Log.e("ReimbursementsByCategories", "Error: ${t.message}")
            }
        })
    }
}