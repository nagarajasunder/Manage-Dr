package com.geekydroid.managedr.ui.add_doctor.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.geekydroid.managedr.R
import com.geekydroid.managedr.utils.UiOnClickListener
import com.geekydroid.managedr.adapter.GenericAdapter
import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.databinding.FragmentHomeBinding
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.add_doctor.viewmodel.HomeFragmentEvents
import com.geekydroid.managedr.ui.add_doctor.viewmodel.HomeFragmentViewModel
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HomeFragment"
@AndroidEntryPoint
class HomeFragment : Fragment(),UiOnClickListener {

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
                    is HomeFragmentEvents.addNewDoctorFabClicked -> navigateToAddNewDoctorScreen()
                    is HomeFragmentEvents.navigateToDoctorDashboard -> navigateToDoctorDashboard(event.doctorId)
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

    private fun navigateToDoctorDashboard(doctorId:Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToDoctorDashboardFragment(doctorId)
        findNavController().navigate(action)
    }

    private fun setUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        adapter = GenericAdapter(this,R.layout.doctor_card)
        binding.recyclerView.adapter = adapter
    }

    private fun setupAdapter(data:List<HomeScreenDoctorData>? = null) {
        data?.let {
            adapter.submitList(it)
        }
    }

    private fun showSnackBar() {
        //Todo
    }

    private fun showLoadingDialog() {
        //Todo
    }

    private fun navigateToAddNewDoctorScreen() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddNewDoctorFragment()
        findNavController().navigate(action)
    }


    override fun onClick(position: Int, data: ScreenData?) {
        if (data is HomeScreenDoctorData)
        {
            viewmodel.onDoctorClicked(data)
        }
    }

}