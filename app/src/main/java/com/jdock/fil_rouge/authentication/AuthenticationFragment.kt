package com.jdock.fil_rouge.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.jdock.fil_rouge.R
import com.jdock.fil_rouge.databinding.FragmentAuthenticationBinding


class AuthenticationFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root// Inflate the layout for this fragment
    }

    private lateinit var binding: FragmentAuthenticationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginButton = binding.authLoginButton
        loginButton.setOnClickListener{
            findNavController().navigate(R.id.action_authenticationFragment_to_loginFragment)
        }
        val signupButton = binding.authSignupButton
        signupButton.setOnClickListener{
            findNavController().navigate(R.id.action_authenticationFragment_to_signupFragment)
        }
    }

   }