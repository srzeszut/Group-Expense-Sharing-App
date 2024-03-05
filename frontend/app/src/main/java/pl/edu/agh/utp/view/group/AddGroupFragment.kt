package pl.edu.agh.utp.view.group

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.agh.utp.R
import pl.edu.agh.utp.manager.UserSession
import pl.edu.agh.utp.viewmodel.EmailListAdapter
import pl.edu.agh.utp.viewmodel.GroupAdapter
import java.util.UUID

class AddGroupFragment(private val groupAdapter: GroupAdapter) : Fragment() {

    private val emailList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_group, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.emails_recycler_view)
        val emailListAdapter = EmailListAdapter(emailList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = emailListAdapter

        val addEmailButton: Button = view.findViewById(R.id.add_email_button)
        addEmailButton.setOnClickListener {
            showEditDialog("email") { email ->
                emailList.add(email)
                emailListAdapter.notifyItemInserted(emailList.size - 1)
            }
        }

        val createButton: Button = view.findViewById(R.id.add_group_button)
        createButton.setOnClickListener {
            createGroup(emailList)
        }

        return view
    }

    private fun showEditDialog(title: String, onTextEdited: (String) -> Unit) {
        val editText = EditText(requireContext())
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit $title")
            .setView(editText)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val editedText = editText.text.toString().trim()

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(editedText).matches()) {
                    onTextEdited(editedText)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Invalid email address", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }


    private fun createGroup(emailList: MutableList<String>) {
        val groupName: String = (view?.findViewById<EditText>(R.id.name_input)?.text.toString() ?: "")
        val userId: UUID = UserSession(requireContext()).getUser()?.userId!!
        var groupId: UUID = UUID.randomUUID()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                groupAdapter.createGroup(groupName, userId, onGroupCreated = { id ->
                    groupId = id
                    navigateToGroupsFragment()
                })
            }
            groupAdapter.addUsersToGroup(groupId, emailList)
        }
    }

    private fun navigateToGroupsFragment() {
        val groupsFragment = GroupsFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, groupsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
