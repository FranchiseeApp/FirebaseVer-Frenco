package com.aryasurya.franchiso.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aryasurya.franchiso.R
import com.aryasurya.franchiso.data.pref.User
import com.aryasurya.franchiso.data.session.SessionManager
import com.aryasurya.franchiso.databinding.FragmentAccountBinding
import com.aryasurya.franchiso.databinding.FragmentHomeBinding
import com.aryasurya.franchiso.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater , container , false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val user: User? = sessionManager.getSession()

        // Lakukan pengecekan apakah ada sesi pengguna atau tidak
        if (user != null) {



        } else {
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }



    }
}