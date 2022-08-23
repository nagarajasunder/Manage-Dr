package com.geekydroid.managedr.ui.doctordashboard.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.geekydroid.managedr.R
import com.geekydroid.managedr.adapter.GenericAdapter
import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.application.TransactionType
import com.geekydroid.managedr.databinding.FragmentDoctorDashboardBinding
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import com.geekydroid.managedr.ui.doctordashboard.viewmodel.DoctorDashboardViewmodel
import com.geekydroid.managedr.ui.doctordashboard.viewmodel.doctorDashboardEvents
import com.geekydroid.managedr.utils.UiOnClickListener
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "DoctorDashboardFragment"
@AndroidEntryPoint
class DoctorDashboardFragment : Fragment(),UiOnClickListener {

    private lateinit var binding:FragmentDoctorDashboardBinding
    private var doctorId:Int = -1
    private val args:DoctorDashboardFragmentArgs by navArgs()
    private val viewmodel:DoctorDashboardViewmodel by viewModels()
    private lateinit var adapter: GenericAdapter

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

        setUI()

        viewmodel.getTransactionData(doctorId)
        viewmodel.transactionData.observe(viewLifecycleOwner){
            when(it)
            {
                is Resource.Error -> Any()
                is Resource.Loading -> Any()
                is Resource.Success -> updateList(it.data)
            }
        }

        viewmodel.getDoctorDataById(doctorId)
        viewmodel.doctorData.observe(viewLifecycleOwner){
            when(it)
            {
                is Resource.Error -> Any()
                is Resource.Loading -> Any()
                is Resource.Success -> updateDoctorData(it.data)
            }
        }
        viewmodel.cityData.observe(viewLifecycleOwner){
            setupCitySpinner(it)
        }
        observeUiEvents()
    }

    private fun updateList(data: List<DoctorDashboardTxData>?) {
        data?.let {
            Log.d(TAG, "updateList: $it")
            adapter.submitList(it)
        }
    }

    private fun setUI() {
        binding.dashboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dashboardRecyclerView.setHasFixedSize(true)
        adapter = GenericAdapter(this,R.layout.transaction_card)
        binding.dashboardRecyclerView.adapter = adapter
    }

    private fun setupCitySpinner(cityList: List<MdrCity>) {
        val adapter = ArrayAdapter(requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, cityList.map { it.cityName })
        (binding.spinnerCity.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.spinnerCity.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->  }
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.doctorDashboardEvent.collect{
                when(it)
                {
                    doctorDashboardEvents.addNewCollectionClicked -> navigateToNewCollectionFragment()
                    doctorDashboardEvents.addNewServiceClicked -> navigateToNewServiceFragment()
                }
            }
        }
    }

    private fun navigateToNewCollectionFragment() {
        val action = DoctorDashboardFragmentDirections.actionDoctorDashboardFragmentToNewServiceFragment(doctorId,TransactionType.COLLECTION)
        findNavController().navigate(action)
    }

    private fun navigateToNewServiceFragment() {
        val action = DoctorDashboardFragmentDirections.actionDoctorDashboardFragmentToNewServiceFragment(doctorId,TransactionType.SERVICE)
        findNavController().navigate(action)
    }

    private fun updateDoctorData(doctorData:HomeScreenDoctorData? = null) {
       doctorData?.let {
           binding.doctorData = it
       }
    }

    override fun onClick(position: Int, data: ScreenData?) {

    }

}