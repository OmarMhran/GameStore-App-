package com.example.gamestore.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.gamestore.R
import com.example.gamestore.data.Status
import com.example.gamestore.data.model.User
import com.example.gamestore.databinding.ActivitySignUpBinding
import com.example.gamestore.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.*
import javax.inject.Inject


@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    lateinit var viewModel: SignUpViewModel
    lateinit var user: User
    private var binding : ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)


        binding?.tvHaveAccount?.setOnClickListener {
            toSignIn()
        }


        binding?.btnSignUp?.setOnClickListener {
            val emailText = binding?.etEmailSignUp?.text?.toString()
            val passwordText = binding?.etPasswordSignUp?.text.toString()
            val fullNameText = binding?.etNameSignUp?.text?.toString()
            val mobilePhoneText = binding?.etPhoneSignUp?.text?.toString()


            when {
                mobilePhoneText?.length!! != 8 ->
                    binding?.root?.rootView?.showsnackBar("Mobile Phone must be 8 digit")

                else -> {
                    user = User(fullNameText!!, emailText!!, mobilePhoneText, passwordText)
                    viewModel.signUpUser(emailText.toString(), passwordText, fullNameText.toString())
                        .observe(this, {
                            when (it.status) {
                                Status.SUCCESS -> {
                                    binding?.pbSignUp?.visibility = View.GONE
                                    viewModel.saveUser(user)
                                    binding?.root?.rootView?.showsnackBar("User account registered")
                                    toSignIn()
                                }
                                Status.ERROR -> {
                                    binding?.pbSignUp?.visibility = View.GONE
                                    binding?.root?.rootView?.showsnackBar(it.message!!)
                                }
                                Status.LOADING -> {
                                    binding?.pbSignUp?.visibility = View.VISIBLE
                                }
                            }
                        })

                }

            }
        }


    }
    private fun toSignIn() {
        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}


fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}