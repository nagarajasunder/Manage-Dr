package com.geekydroid.managedr.ui.add_doctor.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.FragmentHomeBinding
import com.geekydroid.managedr.ui.add_doctor.viewmodel.HomeFragmentEvents
import com.geekydroid.managedr.ui.add_doctor.viewmodel.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewmodel:HomeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.eventsChannel.collect{ event ->
                when(event)
                {
                    HomeFragmentEvents.addNewDoctorFabClicked -> navigateToAddNewDoctorScreen()
                }
            }
        }

        binding.fabAddNewDoctor.setOnClickListener {
            viewmodel.onFabClicked()
        }
    }

    private fun navigateToAddNewDoctorScreen() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddNewDoctorFragment()
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }

}