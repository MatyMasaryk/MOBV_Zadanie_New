package eu.mcomputing.mobv.mobvzadanie.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.mobvzadanie.R
import eu.mcomputing.mobv.mobvzadanie.data.DataRepository
import eu.mcomputing.mobv.mobvzadanie.databinding.FragmentUserBinding
import eu.mcomputing.mobv.mobvzadanie.viewmodels.FeedViewModel
import eu.mcomputing.mobv.mobvzadanie.widgets.bottomBar.BottomBar

class UserFragment : Fragment() {
    private lateinit var viewModel: FeedViewModel
    private lateinit var binding: FragmentUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.FEED)

            val urlBase = "https://upload.mcomputing.eu/"
            val userItem = viewModel.selectedUser?.value
            val urlPhoto = userItem?.photo
            if (urlPhoto != null && urlPhoto != "") {
                Picasso.get().load(urlBase + urlPhoto).into(bnd.profilePicture)
            } else {
                bnd.profilePicture.setImageResource(R.drawable.baseline_account_box_24)
            }

        }

    }

}