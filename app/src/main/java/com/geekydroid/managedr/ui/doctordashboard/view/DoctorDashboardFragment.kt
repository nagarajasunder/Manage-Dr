package com.geekydroid.managedr.ui.doctordashboard.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.FragmentDoctorDashboardBinding
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.doctordashboard.viewmodel.DoctorDashboardViewmodel
import com.geekydroid.managedr.ui.doctordashboard.viewmodel.doctorDashboardEvents
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DoctorDashboardFragment : Fragment() {

    private lateinit var binding:FragmentDoctorDashboardBinding
    private var doctorId:Int = -1
    private val args:DoctorDashboardFragmentArgs by navArgs()
    private val viewmodel:DoctorDashboardViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_doctor_dashboard,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        doctorId = args.doctorId
        viewmodel.getDoctorDataById(doctorId)
        viewmodel.doctorData.observe(viewLifecycleOwner){
            when(it)
            {
                is Resource.Error -> Any()
                is Resource.Loading -> Any()
                is Resource.Success -> updateDoctorData(it.data)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.doctorDashboardEvent.collect{
                when(it)
                {
                    doctorDashboardEvents.addNewCollectionClicked -> TODO()
                    doctorDashboardEvents.addNewServiceClicked -> navigateToNewServiceFragment()
                }
            }
        }
    }

    private fun navigateToNewServiceFragment() {
        val action = DoctorDashboardFragmentDirections.actionDoctorDashboardFragmentToNewServiceFragment(doctorId)
        findNavController().navigate(action)
    }

    private fun updateDoctorData(doctorData:HomeScreenDoctorData? = null) {
       doctorData?.let {
           binding.doctorData = it
       }
    }

}