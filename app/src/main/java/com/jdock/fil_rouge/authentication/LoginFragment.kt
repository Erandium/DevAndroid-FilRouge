package com.jdock.fil_rouge.authentication


import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jdock.fil_rouge.R
import com.jdock.fil_rouge.databinding.FragmentLoginBinding
import com.jdock.fil_rouge.network.Api
import com.jdock.fil_rouge.network.SHARED_PREF_TOKEN_KEY
import kotlinx.coroutines.launch




class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root// Inflate the layout for this fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = binding.loginConfButton
        loginButton.setOnClickListener{
            if(binding.loginEmailEditText.text.toString()!="" && binding.loginPasswordEdittext.text.toString()!=""){
                var loginForm = LoginForm(binding.loginEmailEditText.text.toString(),binding.loginPasswordEdittext.text.toString())
                lifecycleScope.launch{
                    val response = Api.userWebService.login(loginForm)
                    if(response.isSuccessful){
                        val fetchedToken = response.body()?.token
                        PreferenceManager.getDefaultSharedPreferences(context).edit {
                            putString(SHARED_PREF_TOKEN_KEY, fetchedToken)
                        }
                        findNavController().navigate(R.id.action_loginFragment_to_taskListFragment)
                    }
                    else{
                        Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }
}