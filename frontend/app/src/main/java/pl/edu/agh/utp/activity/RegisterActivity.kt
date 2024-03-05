package pl.edu.agh.utp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.agh.utp.R
import pl.edu.agh.utp.api.ApiObject
import pl.edu.agh.utp.databinding.ActivityRegisterBinding
import pl.edu.agh.utp.manager.UserSession
import pl.edu.agh.utp.model.user.RegisterRequest
import pl.edu.agh.utp.model.user.User

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)
        // Initialize views
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            lifecycleScope.launch {
                val user = registerUser(RegisterRequest(username, email, password))

                if (user != null) {
                    UserSession(applicationContext).saveUser(user)
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    binding.passwordEditText.error = "Internal error, try again"
                }
            }
        }

        binding.loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun registerUser(registerRequest: RegisterRequest): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiObject.instance.registerUser(registerRequest).execute()
                if (response.isSuccessful) response.body() else null

            } catch (e: Exception) {
                null
            }
        }
    }
}