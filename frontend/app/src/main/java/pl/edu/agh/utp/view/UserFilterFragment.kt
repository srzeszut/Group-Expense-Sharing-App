package pl.edu.agh.utp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.edu.agh.utp.R
import pl.edu.agh.utp.model.graph.TransactionsGraph
import pl.edu.agh.utp.model.user.User
import pl.edu.agh.utp.viewmodel.UserFilterAdapter
import java.util.UUID

class UserFilterFragment(
    private val groupId: UUID,
    private val callback: (List<User>, (TransactionsGraph) -> Unit) -> Unit,
    private val updateView : (TransactionsGraph) -> Unit) : DialogFragment() {

    private lateinit var userFilterAdapter: UserFilterAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_filter, container, false)

        recyclerView = view.findViewById(R.id.user_list)
        userFilterAdapter = UserFilterAdapter(groupId)
        userFilterAdapter.fetchUsersFromGroup()
        recyclerView.adapter = userFilterAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val resetButton: Button = view.findViewById(R.id.reset_btn)
        val applyButton: Button = view.findViewById(R.id.apply_btn)

        resetButton.setOnClickListener {
            userFilterAdapter.reset()
        }
        applyButton.setOnClickListener {
            callback(userFilterAdapter.getSelectedUsers(), updateView)
            this.dismiss()
        }

        return view
    }
}