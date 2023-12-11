package eu.mcomputing.mobv.mobvzadanie.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.mobvzadanie.R
import eu.mcomputing.mobv.mobvzadanie.data.DataRepository
import eu.mcomputing.mobv.mobvzadanie.data.PreferenceData
import eu.mcomputing.mobv.mobvzadanie.databinding.FragmentPasswordBinding
import eu.mcomputing.mobv.mobvzadanie.viewmodels.AuthViewModel

class PasswordFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->

            viewModel.passwordResult.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    if (it != viewModel.passResultMessage) {
                        Snackbar.make(
                            bnd.submitButton,
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    viewModel.clearPassResult()
                    requireView().findNavController().navigate(R.id.action_to_profile)
                }
            }

        }
    }

}