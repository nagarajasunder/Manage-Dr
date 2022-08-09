package com.geekydroid.managedr.ui.add_doctor.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.geekydroid.managedr.R
import com.geekydroid.managedr.adapter.GenericAdapter
import com.geekydroid.managedr.databinding.FragmentHomeBinding
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.add_doctor.viewmodel.HomeFragmentEvents
import com.geekydroid.managedr.ui.add_doctor.viewmodel.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewmodel:HomeFragmentViewModel by viewModels()
    private lateinit var adapter:GenericAdapter

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
        binding.lifecycleOwner = viewLifecycleOwner
        setUI()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.eventsChannel.collect{ event ->
                when(event)
                {
                    HomeFragmentEvents.addNewDoctorFabClicked -> navigateToAddNewDoctorScreen()
                }
            }
        }

        viewmodel.doctorData.observe(viewLifecycleOwner){ resource ->

            when(resource)
            {
                is Resource.Error -> showSnackBar()
                is Resource.Loading -> showLoadingDialog()
                is Resource.Success -> setupAdapter(resource.data)
            }

        }

        binding.fabAddNewDoctor.setOnClickListener {
            viewmodel.onFabClicked()
        }
    }

    private fun setUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun setupAdapter(data:List<HomeScreenDoctorData>? = null) {
        data?.let {
            adapter = GenericAdapter(it,R.layout.doctor_card)
            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun showSnackBar() {

    }

    private fun showLoadingDialog() {

    }

    private fun navigateToAddNewDoctorScreen() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddNewDoctorFragment()
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }

}