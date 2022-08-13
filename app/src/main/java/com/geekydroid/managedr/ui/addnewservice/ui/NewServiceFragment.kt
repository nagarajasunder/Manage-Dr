package com.geekydroid.managedr.ui.addnewservice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.FragmentNewServiceBinding
import com.geekydroid.managedr.ui.addnewservice.viewmodel.AddNewServiceFragmentViewmodel
import com.geekydroid.managedr.ui.addnewservice.viewmodel.NewServiceFragmentEvents
import com.geekydroid.managedr.ui.dialogs.NewDivisionFragment
import com.geekydroid.managedr.utils.DialogInputType
import com.geekydroid.managedr.utils.GenericDialogOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewServiceFragment : Fragment(),GenericDialogOnClickListener {

    private lateinit var binding: FragmentNewServiceBinding
    private val viewmodel: AddNewServiceFragmentViewmodel by viewModels()
    private val args: NewServiceFragmentArgs by navArgs()
    private var doctorId: Int = -1
    private lateinit var newDivisionFragment:NewDivisionFragment
    private lateinit var citySpinnerAdapter:ArrayAdapter<String>
    private var citySpinnerList:MutableList<String> = mutableListOf("Select a city")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_service, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner

        doctorId = args.doctorId
        viewmodel.getDoctorName(doctorId)
        observeUiEvents()
        viewmodel.cityData.observe(viewLifecycleOwner){
            setupCitySpinner()
        }

    }

    private fun setupCitySpinner() {
        citySpinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,citySpinnerList)
        binding.spinnerCity.adapter = citySpinnerAdapter
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.newServiceEvents.collect {
                when (it) {
                    NewServiceFragmentEvents.showDatePickerDialog -> TODO()
                    NewServiceFragmentEvents.showNewCategoryDialog -> showBottomSheetDialog(
                        DialogInputType.DIVISION
                    )
                    NewServiceFragmentEvents.showNewCityDialog -> showBottomSheetDialog(
                        DialogInputType.CITY
                    )
                }
            }
        }
    }

    private fun showBottomSheetDialog(type: DialogInputType) {
        val bundle = Bundle()
        when (type) {
            DialogInputType.DIVISION -> {
                bundle.putString("title", "Add new division")
                bundle.putString("hint", "Division name")
            }
            DialogInputType.CITY -> {
                bundle.putString("title", "Add new City")
                bundle.putString("hint", "City name")
            }
        }
        bundle.putString("inputType",type.toString())
        requireActivity().supportFragmentManager.let {
            newDivisionFragment = NewDivisionFragment.newInstance(bundle).apply {
                show(it,"NewDivisionFragment")
            }

        }
    }

    override fun onClickDialog(vararg args: Any) {
        val input = (args[0] as String)
        if (!viewmodel.isCityDuplicate(input))
        {
            viewmodel.addNewCity(input)
            newDivisionFragment.dismissDialog()
        }

    }
}