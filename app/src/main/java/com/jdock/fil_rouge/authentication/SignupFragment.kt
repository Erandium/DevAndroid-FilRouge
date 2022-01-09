package com.jdock.fil_rouge.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.jdock.fil_rouge.R
import com.jdock.fil_rouge.databinding.FragmentSignupBinding
import com.jdock.fil_rouge.network.Api
import com.jdock.fil_rouge.network.SHARED_PREF_TOKEN_KEY
import kotlinx.coroutines.launch


class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root// Inflate the layout for this fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = binding.signupConfButton
        loginButton.setOnClickListener{
            if(binding.signupFirstnameEditText.text.toString()!=""
                && binding.signupLastnameEditText.text.toString()!=""
                && binding.signupEmailEditText.text.toString()!=""
                && binding.signupPasswordEditText.text.toString()!=""
                && binding.signupPasswordConfEditText.text.toString()!=""
                && binding.signupPasswordEditText.text.toString().equals(binding.signupPasswordConfEditText.text.toString()))
                {
                var signUpForm = SignUpForm(binding.signupFirstnameEditText.text.toString(),
                    binding.signupLastnameEditText.text.toString(),
                    binding.signupEmailEditText.text.toString(),
                    binding.signupPasswordEditText.text.toString(),
                    binding.signupPasswordConfEditText.text.toString())
                lifecycleScope.launch{
                    val response = Api.userWebService.signUp(signUpForm)
                    if(response.isSuccessful){
                        val fetchedToken = response.body()?.token
                        PreferenceManager.getDefaultSharedPreferences(context).edit {
                            putString(SHARED_PREF_TOKEN_KEY, fetchedToken)
                        }
                        findNavController().navigate(R.id.action_signupFragment_to_taskListFragment)
                    }
                    else{
                        Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }
}