package pl.edu.agh.utp.view.transaction

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import pl.edu.agh.utp.R
import pl.edu.agh.utp.api.ApiObject
import pl.edu.agh.utp.manager.UserSession
import pl.edu.agh.utp.model.user.PersonInfo
import pl.edu.agh.utp.model.transaction.Transaction
import pl.edu.agh.utp.model.transaction.TransactionRequest
import pl.edu.agh.utp.model.user.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.UUID


class AddTransactionFragment(private val groupId: UUID) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = UserSession(requireContext())
    }


    private var people: MutableMap<UUID, PersonInfo> = mutableMapOf()

    private var category: String = ""
    private var description: String = ""
    private var amount: Double = 0.0
    private lateinit var userSession: UserSession
    private var user: User? = null
    private var debtsUserIds: List<UUID> = listOf()

    private lateinit var descriptionEditText: EditText
    private lateinit var paymentEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var addExpenseButton: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        user = userSession.getUser()
        fetchGroupUsers(groupId)

        val view = inflater.inflate(R.layout.fragment_add_transaction, container, false)

        descriptionEditText = view.findViewById(R.id.editTextDescription)
        paymentEditText = view.findViewById(R.id.editTextPayment)
        categoryEditText = view.findViewById(R.id.editCategory)
        addExpenseButton = view.findViewById(R.id.addExpenseButton)

        addExpenseButton.setOnClickListener { addExpenseButtonClicked() }

        return view
    }

    private fun updatePeopleLayout() {
        val peopleLinearLayout: ViewGroup = requireView().findViewById(R.id.linearLayoutPeople)
        peopleLinearLayout.removeAllViews()

        for ((id, personData) in people) {
            val checkBox = CheckBox(requireContext())
            checkBox.text = personData.name
            checkBox.isChecked = personData.isSelected
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                people[id]?.isSelected = isChecked
            }
            peopleLinearLayout.addView(checkBox)
        }
    }

    private fun displayErrorToast(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
    private fun validatePeople(): Boolean {
        return if (people.isEmpty() || !people.any { it.value.isSelected }) {
            val errorMessage = "Select at least one person to your expense."
            displayErrorToast(errorMessage)
            false
        } else {
            true
        }
    }
    private fun validateAmount(): Boolean {
        if (amount <= 0.0) {
            displayErrorToast("Amount cannot be empty.")
            return false
        }
        return true
    }
    private fun validateDescription(): Boolean {
        if (description.isBlank()) {
            displayErrorToast("Description cannot be empty.")
            return false
        }
        return true
    }
    private fun validateCategory(): Boolean {
        if (category.isBlank()) {
            displayErrorToast("Category cannot be empty.")
            return false
        }
        return true
    }
    private fun validate(): Boolean {
        description = descriptionEditText.text.toString()
        amount = paymentEditText.text.toString().toDoubleOrNull() ?: 0.0
        category = categoryEditText.text.toString()
        return validateDescription() && validateAmount() && validateCategory()  &&  validatePeople()
    }

    private fun navigateToTransactionsFragment() {
        val fragment = TransactionsFragment(groupId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addExpenseButtonClicked() {
        if (!validate()) {
            return
        }

        val todaysDate: LocalDate = LocalDate.now()
        debtsUserIds = people.filter { it.value.isSelected }.map { it.key }

        user?.userId?.let { userId ->
            val transactionRequest = TransactionRequest(
                description,
                todaysDate.toString(),
                category,
                amount,
                userId,
                debtsUserIds
            )

            addTransaction(groupId, transactionRequest) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(requireContext(), "Expense added successfully", Toast.LENGTH_SHORT).show()
                    navigateToTransactionsFragment()
                } else {
                    Toast.makeText(requireContext(), "Something went wrong. Expense not added", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: Toast.makeText(requireContext(), "User ID is null", Toast.LENGTH_SHORT).show()
    }


    private fun preparePeopleHashMap(users: List<User>) {
        for (user in users) {
            people[user.userId] = PersonInfo(user.name, false)
            Log.i("AddTransactionFragment", "User: $user")
        }
    }

    private fun fetchGroupUsers(groupId: UUID) {
        Log.d("AddTransactionFragment","groupid: $groupId")
        ApiObject.instance.getUsersFromGroup(groupId).enqueue(object : Callback<List<User>> {
            override fun onResponse(
                call: Call<List<User>>,
                response: Response<List<User>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { users ->
                        Log.d("AddTransactionFragment", "Users: $users")
                        preparePeopleHashMap(users)
                        updatePeopleLayout()
                    } ?: Log.d("AddTransactionFragment", "Response body is null")
                } else {
                    Log.d("AddTransactionFragment", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("AddTransactionFragment", "Error: ${t.message}")
            }
        })
    }


    private fun addTransaction(groupId: UUID, transactionRequest: TransactionRequest, callback: (Boolean) -> Unit) {
        ApiObject.instance.addTransaction(groupId, transactionRequest)
            .enqueue(object : Callback<Transaction> {
                override fun onResponse(
                    call: Call<Transaction>,
                    response: Response<Transaction>
                ) {
                    if (response.isSuccessful) {
                        val addedTransaction = response.body()
                        Log.d("AddTransactionFragment", "Transaction added: $addedTransaction")
                        callback(true)
                    } else {
                        Log.d("AddTransactionFragment", "Error: ${response.code()}")
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<Transaction>, t: Throwable) {
                    Log.d("AddTransactionFragment", "Error: ${t.printStackTrace()}")
                    callback(false)
                }
            })
    }
}