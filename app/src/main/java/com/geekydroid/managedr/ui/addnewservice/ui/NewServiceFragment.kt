package com.geekydroid.managedr.ui.addnewservice.ui

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
class NewServiceFragment : Fragment(), GenericDialogOnClickListener {

    private lateinit var binding: FragmentNewServiceBinding
    private val viewmodel: AddNewServiceFragmentViewmodel by viewModels()
    private val args: NewServiceFragmentArgs by navArgs()
    private var doctorId: Int = -1
    private lateinit var newDivisionFragment: NewDivisionFragment
    private lateinit var citySpinnerAdapter: ArrayAdapter<String>
    private lateinit var divisionSpinnerAdapter: ArrayAdapter<String>
    private var citySpinnerList: MutableList<String> = mutableListOf("Select a city")
    private var divisionSpinnerList: MutableList<String> = mutableListOf("Select a division")
    private lateinit var host: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_service, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        host = requireActivity()
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner

        doctorId = args.doctorId
        viewmodel.getDoctorName(doctorId)
        setUI()
        observeUiEvents()
        viewmodel.cityData.observe(viewLifecycleOwner) {
            setupCitySpinner(it.map { it.cityName })
        }
        viewmodel.categoryData.observe(viewLifecycleOwner) {
            setupDivisionSpinner(it.map { it.categoryName })
        }

        host.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_new_doctor_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.cta_save -> {
                        viewmodel.onSaveCtaClicked()
                        return true
                    }
                }
                return false
            }

        })

    }

    private fun setUI() {
        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                viewmodel.updateSelectedCity(index)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                    viewmodel.updateSelectedCategory(index)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    private fun setupDivisionSpinner(list: List<String>) {
        divisionSpinnerList.clear()
        divisionSpinnerList.add("Select a division")
        divisionSpinnerList.addAll(list)
        divisionSpinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            divisionSpinnerList)
        binding.spinnerCategory.adapter = divisionSpinnerAdapter
    }

    private fun setupCitySpinner(list: List<String>) {
        citySpinnerList.clear()
        citySpinnerList.add("Select a city")
        citySpinnerList.addAll(list)
        citySpinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            citySpinnerList)
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
                    NewServiceFragmentEvents.newServiceCreated -> TODO()
                    NewServiceFragmentEvents.selectCategoryError -> TODO()
                    NewServiceFragmentEvents.selectCityError -> TODO()
                    NewServiceFragmentEvents.transactionAmountError -> TODO()
                    NewServiceFragmentEvents.dismissNewDivisionDialog -> dismissNewDivisionDialog()
                    is NewServiceFragmentEvents.showDuplicateWarningInDialog -> showDuplicateWarning(it.input)
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
        bundle.putString("inputType", type.toString())
        requireActivity().supportFragmentManager.let {
            newDivisionFragment = NewDivisionFragment.newInstance(bundle, this).apply {
                show(it, "NewDivisionFragment")
            }

        }
    }

    override fun onClickDialog(vararg args: Any) {
        val input = (args[0] as String)
        val dialogType = (args[1] as String)
        if (dialogType == DialogInputType.CITY.toString()) {
            viewmodel.isCityDuplicate(input)
        } else {
            viewmodel.isDuplicateDivision(input)
        }

    }

    private fun showDuplicateWarning(input: String)
    {
        newDivisionFragment.showDuplicateWarning(input)
    }

    private fun dismissNewDivisionDialog()
    {
        newDivisionFragment.dismissDialog()
    }

}