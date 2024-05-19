package com.example.gamestore.ui.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gamestore.data.AppPreference
import com.example.gamestore.data.Status
import com.example.gamestore.databinding.ActivityLoginBinding
import com.example.gamestore.ui.product.activity.ProductActivity
import com.example.gamestore.ui.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var viewModel: LoginViewModel
     private var binding: ActivityLoginBinding? = null
    @Inject
    lateinit var appPreference : AppPreference


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        binding?.btnLogin?.setOnClickListener {
            val emailText = binding?.etEmailLogin?.text?.toString()?.trim()
            val passwordText = binding?.etPasswordLogin?.text?.toString()?.trim()
            viewModel.signInUser(emailText!!, passwordText!!).observe(this, {
                when (it.status) {
                    Status.LOADING -> {
                        binding?.pbLogin?.visibility = View.VISIBLE
                    }

                    Status.SUCCESS -> {
                        binding?.pbLogin?.visibility = View.GONE
                        binding?.root?.showsnackBar("Login successful")
                        saveToDataStore(it.data?.username!!,it.data?.email!!)
                        toHome()
                    }

                    Status.ERROR -> {
                        binding?.pbLogin?.visibility = View.GONE
                        binding?.root?.showsnackBar(it.message!!)
                    }
                }
            })

        }







        btnLoginToSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            this.finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun toHome(){
        val intent = Intent(this@LoginActivity, ProductActivity::class.java)
        startActivity(intent)
        this.finish()
    }
    private fun saveToDataStore(name: String, email: String) {
        lifecycleScope.launch {
            appPreference.saveUser2(name,email)
        }
    }
}
fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}