package com.geekydroid.managedr.ui.doctordashboard.view

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import com.geekydroid.managedr.ui.doctordashboard.viewmodel.DoctorDashboardViewmodel
import com.geekydroid.managedr.ui.doctordashboard.viewmodel.doctorDashboardEvents
import com.geekydroid.managedr.utils.UiOnClickListener
import com.geekydroid.managedr.utils.uiutils.PickerUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoctorDashboardFragment : Fragment(),UiOnClickListener {

    private lateinit var binding:FragmentDoctorDashboardBinding
    private var doctorId:Int = -1
    private val args:DoctorDashboardFragmentArgs by navArgs()
    private val viewmodel:DoctorDashboardViewmodel by viewModels()
    private lateinit var adapter: GenericAdapter
    private lateinit var host:FragmentActivity

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
        host = requireActivity()
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        doctorId = args.doctorId

        setUI()
        viewmodel.setDoctorId(doctorId)
        viewmodel.getTransactionData()
        viewmodel.transactionData.observe(viewLifecycleOwner){
            when(it)
            {
                is Resource.Error -> Any()
                is Resource.Loading -> Any()
                is Resource.Success -> updateList(it.data)
            }
        }
        viewmodel.getDoctorDataById()
        viewmodel.doctorData.observe(viewLifecycleOwner){
            when(it)
            {
                is Resource.Error -> Any()
                is Resource.Loading -> Any()
                is Resource.Success -> updateDoctorData(it.data)
            }
        }
        observeUiEvents()
        (binding.spinnerCity.editText as AutoCompleteTextView).setOnClickListener {
            val cityNames = viewmodel.getCityNames()
            if (cityNames.isNotEmpty())
            {
                openCitySelectionDialog(cityNames)
            }
        }
        (binding.spinnerCategory.editText as AutoCompleteTextView).setOnClickListener {
            if (viewmodel.divisionNames.isNotEmpty())
            {
                openDivisionSelectionDialog()
            }
        }
        binding.txTypeGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            viewmodel.updateTxTypeFilter(checkedIds)
        }
    }

    private fun clearChipSelection()
    {
        binding.txTypeGroup.clearCheck()
    }

    private fun openDivisionSelectionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_division)
        builder.setCancelable(false)
        builder.setMultiChoiceItems(viewmodel.divisionNames.toTypedArray(), viewmodel.selectedDivisionData
        ) { _, index, isSelected ->

            if (isSelected) {
                viewmodel.addDivision(index)
            } else {
                viewmodel.removeDivision(index)
            }

        }
        builder.setPositiveButton(R.string.btn_text_ok
        ) { _, _ ->
            viewmodel.showDivisionSelection()
        }
        builder.setNegativeButton(R.string.btn_text_cancel
        ) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNeutralButton(R.string.btn_text_clear_all
        ) { _, _ -> viewmodel.clearDivisionSelection() }

        builder.show()
    }

    private fun openCitySelectionDialog(cityNames:List<String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_city)
        builder.setCancelable(false)
        builder.setMultiChoiceItems(cityNames.toTypedArray(), viewmodel.selectedCityData
        ) { _, index, isSelected ->

            if (isSelected) {
                viewmodel.addCity(index)
            } else {
                viewmodel.removeCity(index)
            }

        }
        builder.setPositiveButton(R.string.btn_text_ok
        ) { _, _ ->
            viewmodel.showCitySelection()
        }
        builder.setNegativeButton(R.string.btn_text_cancel
        ) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNeutralButton(R.string.btn_text_clear_all
        ) { _, _ -> viewmodel.clearCitySelection() }

        builder.show()
    }

    private fun updateList(data: List<DoctorDashboardTxData>?) {
        data?.let {
            adapter.submitList(it)
        }
    }

    private fun setUI() {
        binding.dashboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dashboardRecyclerView.isNestedScrollingEnabled = false
        adapter = GenericAdapter(this,R.layout.transaction_card)
        binding.dashboardRecyclerView.adapter = adapter

        host.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.doctor_dashboard_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId)
                {
                    R.id.edit -> viewmodel.onEditMenuItemClicked()
                }
                return true
            }

        },viewLifecycleOwner,Lifecycle.State.RESUMED)
    }



    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.doctorDashboardEvent.collect{
                when(it)
                {
                    doctorDashboardEvents.addNewCollectionClicked -> navigateToNewCollectionFragment()
                    doctorDashboardEvents.addNewServiceClicked -> navigateToNewServiceFragment()
                    doctorDashboardEvents.showDateRangePicker -> openDateRangePicker()
                    doctorDashboardEvents.clearChipSelection -> clearChipSelection()
                    doctorDashboardEvents.navigateToEditDoctorScreen -> openEditDoctorScreen()
                }
            }
        }
    }

    private fun openEditDoctorScreen() {
        val action = DoctorDashboardFragmentDirections.actionDoctorDashboardFragmentToAddNewDoctorFragment()
        action.doctorId = doctorId
        findNavController().navigate(action)
    }

    private fun openDateRangePicker() {
        val picker = PickerUtils.getDateRangePicker()
        picker.show(requireActivity().supportFragmentManager,"date range picker")
        picker.addOnPositiveButtonClickListener { dateRange ->
            viewmodel.updateDateRange(dateRange)
        }
    }

    private fun navigateToNewCollectionFragment() {
        val cityId:Int = viewmodel.getDoctorCityId()
        if (cityId != -1)
        {
            val action = DoctorDashboardFragmentDirections.actionDoctorDashboardFragmentToNewServiceFragment(doctorId,TransactionType.COLLECTION,cityId)
            findNavController().navigate(action)
        }
    }

    private fun navigateToNewServiceFragment() {
        val cityId = viewmodel.getDoctorCityId()
        if (cityId != -1)
        {
            val action = DoctorDashboardFragmentDirections.actionDoctorDashboardFragmentToNewServiceFragment(doctorId,TransactionType.SERVICE,cityId)
            findNavController().navigate(action)
        }
    }

    private fun updateDoctorData(doctorData:HomeScreenDoctorData? = null) {
       doctorData?.let {
           binding.doctorData = it
       }
    }

    override fun onClick(vararg args: Any) {

    }

}