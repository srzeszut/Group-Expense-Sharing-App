package pl.edu.agh.utp.viewmodel

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.edu.agh.utp.api.ApiObject
import pl.edu.agh.utp.databinding.ItemCategoryFilterBinding
import pl.edu.agh.utp.model.graph.TransactionsGraph
import pl.edu.agh.utp.model.user.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class UserFilterAdapter(private val groupId: UUID) : RecyclerView.Adapter<UserFilterAdapter.ViewHolder>() {

    private var users: List<User> = listOf()
    private val usersHashmap: HashMap<User, Boolean> = HashMap()
    public var transactionsGraph: TransactionsGraph? = null
    private val apiService = ApiObject.instance


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemCategoryFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            usersHashmap[user] = isChecked
        }
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(binding: ItemCategoryFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val checkbox: CheckBox = binding.checkbox
        val name: TextView = binding.name
    }

    fun fetchUsersFromGroup() {
        apiService.getUsersFromGroup(groupId).enqueue(object : Callback<List<User>> {
            override fun onResponse(
                call: Call<List<User>>,
                response: Response<List<User>>
            ) {
                if (response.isSuccessful) {
                    Log.i("fetchUsersFromGroup", "Success: ${response.body()}")
                    val groupUsers = response.body()
                    if (groupUsers != null) {
                        users = groupUsers
                        users.forEach { user -> usersHashmap[user] = true}
                        notifyDataSetChanged()
                    }
                } else {
                    Log.e("fetchUsersFromGroup", "Error: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<List<User>>,
                t: Throwable
            ) {
                Log.e("fetchUsersFromGroup", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    fun getGraphWithUsers(users: List<User>, setGraph: (TransactionsGraph) -> Unit) {
        apiService.getGraphWithUsers(groupId, users, false).enqueue(object : Callback<TransactionsGraph> {
            override fun onResponse(
                call: Call<TransactionsGraph>,
                response: Response<TransactionsGraph>
            ) {
                if (response.isSuccessful) {
                    Log.i("getGraphByUsers", "Success: ${response.body()}")
                    val graph = response.body()
                    if (graph != null) {
                        setGraph(graph)
                    }
                } else {
                    Log.e("getGraphByUsers", "Error: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<TransactionsGraph>,
                t: Throwable
            ) {
                Log.e("getGraphByUsers", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    fun getGraph(setGraph: (TransactionsGraph) -> Unit) {
        apiService.getGraph(groupId, true).enqueue(object : Callback<TransactionsGraph> {
            override fun onResponse(
                call: Call<TransactionsGraph>,
                response: Response<TransactionsGraph>
            ) {
                if (response.isSuccessful) {
                    Log.i("getGraphByUsers", "Success: ${response.body()}")
                    val graph = response.body()
                    if (graph != null) {
                        setGraph(graph)
                    }
                } else {
                    Log.e("getGraphByUsers", "Error: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<TransactionsGraph>,
                t: Throwable
            ) {
                Log.e("getGraphByUsers", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    fun reset() {
        users.forEach {usersHashmap[it] = true}
    }

    fun getSelectedUsers(): List<User> {
        val selectedUsers = mutableListOf<User>()
        for ((user, isSelected) in usersHashmap.entries) {
            if (isSelected) {
                selectedUsers.add(user)
            }
        }
        return selectedUsers
    }
}