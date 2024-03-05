package pl.edu.agh.utp.viewmodel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.edu.agh.utp.R
import pl.edu.agh.utp.api.ApiObject
import pl.edu.agh.utp.model.group.Group
import pl.edu.agh.utp.model.group.GroupRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class GroupAdapter(private val clickListener: OnGroupClickListener) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    private var groupList: MutableList<Group> = mutableListOf()

    private val apiService = ApiObject.instance

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]

        holder.itemView.setOnClickListener {
            clickListener.onGroupClick(group.groupId)
        }

        holder.groupName.text = group.name
    }

    override fun getItemCount(): Int = groupList.size

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.group_name)
    }

    interface OnGroupClickListener {
        fun onGroupClick(groupId: UUID)
    }

    fun fetchUserGroups(userId: UUID) {
        apiService.getUserGroups(userId).enqueue(object : Callback<List<Group>> {
            override fun onResponse(
                call: Call<List<Group>>,
                response: Response<List<Group>>
            ) {
                if (response.isSuccessful) {
                    Log.i("FetchUserGroups", "Success: ${response.body()}")
                    val userGroups = response.body() ?: emptyList()
                    groupList.clear()
                    groupList.addAll(userGroups)
                    notifyItemRangeInserted(0, userGroups.size)
                } else {
                    Log.e("FetchUserGroups", "Error: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<List<Group>>,
                t: Throwable
            ) {
                Log.e("FetchUserGroups", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    fun createGroup(groupName: String, userId: UUID, onGroupCreated: (UUID) -> Unit) {
        val groupRequest = GroupRequest(groupName, userId)
        try {
            val response = apiService.createGroup(groupRequest).execute()
            if (response.isSuccessful) {
                Log.i("CreateGroup", "Success: ${response.body()}")
                val createdGroup = response.body()
                createdGroup?.let {
                    groupList.add(it)
                    notifyItemInserted(groupList.size - 1)
                    onGroupCreated(it.groupId)
                } ?: Log.w("CreateGroup", "Received null group in response")
            } else {
                Log.e("CreateGroup", "Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("CreateGroup", "Error: ${e.message}", e)
        }
    }


    fun addUsersToGroup(groupId: UUID ,emailList: MutableList<String>) {
        apiService.addUsersToGroup(groupId, emailList).enqueue(object : Callback<Group> {
            override fun onResponse(
                call: Call<Group>,
                response: Response<Group>
            ) {
                if (response.isSuccessful) {
                    Log.i("AddUsersToGroup", "Success: ${response.body()}")
                    val createdGroup = response.body()
                    if (createdGroup != null) {
                        groupList.add(createdGroup)
                    }
                } else {
                    Log.e("AddUsersToGroup", "Error: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<Group>,
                t: Throwable
            ) {
                Log.e("AddUsersToGroup", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
}