package pl.edu.agh.utp.view.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.edu.agh.utp.R
import pl.edu.agh.utp.manager.UserSession
import pl.edu.agh.utp.view.transaction.TransactionsFragment
import pl.edu.agh.utp.viewmodel.GroupAdapter
import java.util.UUID

class GroupsFragment : Fragment(), GroupAdapter.OnGroupClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        val addGroupButton: Button = view.findViewById(R.id.add_group_button)
        addGroupButton.setOnClickListener {
            navigateToAddGroupFragment()
        }

        recyclerView = view.findViewById(R.id.groups_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        groupAdapter = GroupAdapter(this)
        recyclerView.adapter = groupAdapter

//        fetchUserGroups()

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchUserGroups()
    }

//    private fun fetchUserGroups(userId: Long) {
//        groupAdapter.setItems(mutableListOf(
//            Group(15, "Nazwa"),
//            Group(1, "To niezla nazwa"),
//            Group(2, "To niezla rozbita swieczka")))
//    }

    private fun fetchUserGroups() {
        if (UserSession(requireContext()).isLoggedIn()) {
            val userId: UUID = UserSession(requireContext()).getUser()?.userId!!
            groupAdapter.fetchUserGroups(userId)
        }
    }

    private fun navigateToAddGroupFragment() {
        val addGroupFragment = AddGroupFragment(groupAdapter) // TODO: remove passing adapter once dependency injection works
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, addGroupFragment)
        transaction.addToBackStack(null) // TODO: ability to return to previous view
        transaction.commit()
    }

    private fun navigateToTransactionsFragment(groupId: UUID) {
        val transactionFragment = TransactionsFragment(groupId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, transactionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onGroupClick(groupId: UUID) {
        navigateToTransactionsFragment(groupId)
    }
}