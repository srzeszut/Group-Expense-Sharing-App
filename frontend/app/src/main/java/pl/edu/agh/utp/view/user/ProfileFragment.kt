package pl.edu.agh.utp.view.user

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import pl.edu.agh.utp.R
import pl.edu.agh.utp.activity.LoginActivity
import pl.edu.agh.utp.manager.UserSession
import pl.edu.agh.utp.model.user.User


class ProfileFragment : Fragment() {
    private var name: String = "TestName"
    private var email: String = "test@mail.com"
    private var password: String?= "TestPassword"
    private lateinit var userSession: UserSession
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = UserSession(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.findViewById<TextView>(R.id.textViewName)?.text = name
        view.findViewById<TextView>(R.id.textViewEmail)?.text = email
        view.findViewById<TextView>(R.id.textViewPassword)?.text = password
        view.findViewById<Button>(R.id.logoutButton)?.setOnClickListener {
            userSession.logOut()
            user = userSession.getUser()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<ImageView>(R.id.editIconName)?.setOnClickListener {
            showEditDialog("Name") { editedText ->
                view.findViewById<TextView>(R.id.textViewName)?.text = editedText
            }
        }

        view.findViewById<ImageView>(R.id.editIconPassword)?.setOnClickListener {
            showEditDialog("Password") { editedText ->
                view.findViewById<TextView>(R.id.textViewPassword)?.text = editedText
            }
        }

        view.findViewById<ImageView>(R.id.editIconEmail)?.setOnClickListener {
            showEditDialog("Email") { editedText ->
                view.findViewById<TextView>(R.id.textViewEmail)?.text = editedText
            }
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
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val editedText = editText.text.toString().trim()

                val isValidEmail = if (title.equals("Email", ignoreCase = true)) {
                    android.util.Patterns.EMAIL_ADDRESS.matcher(editedText).matches()
                } else {
                    true
                }

                if (editedText.isNotEmpty() && isValidEmail) {
                    onTextEdited.invoke(editedText)
                    dialog.dismiss()
                    Log.d("ProfileFragment", "Name: $name, Email: $email, Password: $password")
                } else {
                    val errorMessage = when {
                        editedText.isEmpty() -> "Please enter a valid $title"
                        !isValidEmail -> "Please enter a valid email address"
                        else -> "Invalid input"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

}