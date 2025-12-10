package com.example.app.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.app.R
import com.example.app.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObservers()

        viewModel.loadUserProfile()
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        binding.settingsButton.setOnClickListener {
            viewModel.navigateToSettings()
        }

        binding.editProfileButton.setOnClickListener {
            viewModel.navigateToEditProfile()
        }

        binding.deleteAccountButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.clearCacheButton.setOnClickListener {
            viewModel.clearCache()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                        binding.userNameText.text = state.userName
                        binding.userEmailText.text = state.userEmail
                        state.errorMessage?.let { showError(it) }
                    }
                }

                launch {
                    viewModel.navigationEvent.collect { event ->
                        when (event) {
                            is ProfileNavigationEvent.NavigateToLogin -> {
                                findNavController().navigate(R.id.action_profile_to_login)
                            }
                            is ProfileNavigationEvent.NavigateToSettings -> {
                                findNavController().navigate(R.id.action_profile_to_settings)
                            }
                            is ProfileNavigationEvent.NavigateToEditProfile -> {
                                findNavController().navigate(R.id.action_profile_to_edit)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteAccount() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}