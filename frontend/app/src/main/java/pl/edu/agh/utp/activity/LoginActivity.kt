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
import pl.edu.agh.utp.databinding.ActivityLoginBinding
import pl.edu.agh.utp.manager.UserSession
import pl.edu.agh.utp.model.user.LoginRequest
import pl.edu.agh.utp.model.user.User

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Initialize views
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            lifecycleScope.launch {
                val user = loginUser(LoginRequest(email, password))

                if (user != null) {
                    UserSession(applicationContext).saveUser(user)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    binding.passwordEditText.error = "Invalid login credentials"
                }
            }
        }


        binding.registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun loginUser(loginRequest: LoginRequest): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiObject.instance.loginUser(loginRequest).execute()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                null
            }
        }
    }

}