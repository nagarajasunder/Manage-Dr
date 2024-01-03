package com.geekydroid.managedr.ui.add_doctor.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.FragmentAddNewDoctorBinding
import com.geekydroid.managedr.ui.add_doctor.viewmodel.AddNewDoctorEvents
import com.geekydroid.managedr.ui.add_doctor.viewmodel.AddNewDoctorViewModel
import com.geekydroid.managedr.ui.dialogs.NewDivisionFragment
import com.geekydroid.managedr.utils.DateUtils
import com.geekydroid.managedr.utils.DialogInputType
import com.geekydroid.managedr.utils.GenericDialogOnClickListener
import com.geekydroid.managedr.utils.uiutils.PickerUtils
import com.geekydroid.managedr.viewmodel.ManageDrViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "AddNewDoctorFragment"
@AndroidEntryPoint
class AddNewDoctorFragment : Fragment(), GenericDialogOnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var newCityFragment:NewDivisionFragment
    private lateinit var binding: FragmentAddNewDoctorBinding
    private val viewmodel: AddNewDoctorViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var host: FragmentActivity
    private var citySpinnerList: MutableList<String> = mutableListOf("Select a city")
    private lateinit var citySpinnerAdapter: ArrayAdapter<String>
    private val args:AddNewDoctorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_doctor, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        host = requireActivity()
//        viewmodel.updateExistingDoctorId(args.doctorId)

        setUI()
        observeUiEVents()
        viewmodel.cityData.observe(viewLifecycleOwner) { cities ->
            setupCitySpinner(cities.map { it.cityName })
        }

        host.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_new_doctor_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.cta_save -> viewmodel.onSaveCtaClicked()
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupCitySpinner(cityList: List<String>) {
        citySpinnerList.clear()
        citySpinnerList.addAll(cityList)
        Log.d(TAG, "setupCitySpinner: $citySpinnerList")
        citySpinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            citySpinnerList)
        (binding.spinnerCity.editText as AutoCompleteTextView).setAdapter(citySpinnerAdapter)
        if (viewmodel.selectedCityIndex != -1) {
            (binding.spinnerCity.editText as AutoCompleteTextView).setText(citySpinnerList[viewmodel.selectedCityIndex],false)
        }
    }

    private fun setUI() {
        (binding.spinnerCity.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { _, _, index, _ ->
                viewmodel.updateSelectedCityIndex(index)
            }
    }

    private fun observeUiEVents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.AddNewDoctorEvent.collect { event ->
                when (event) {
                    AddNewDoctorEvents.SaveNewDoctor -> viewmodel.validateAndSaveNewDoctor()
                    AddNewDoctorEvents.EnterDoctorName -> binding.edDoctorName.error =
                        "Please enter a valid doctor name"
                    AddNewDoctorEvents.DoctorSavedSuccessFully -> {
                        showSnackBar("New doctor added successfully")
                        moveToHomeScreen()
                    }
                    AddNewDoctorEvents.EnterValidMobileNumber -> binding.edMobileNumber.error =
                        "Please enter a valid mobile number"
                    AddNewDoctorEvents.OpenDobPicker -> openDatePicker("Select Date of birth",
                        DateInputType.DATE_OF_BIRTH)
                    AddNewDoctorEvents.OpenWeddingPicker -> openDatePicker("Select Wedding Date",
                        DateInputType.WEDDING_ANNIVERSARY_DATE)
                    AddNewDoctorEvents.DoctorUpdatedSuccessfully -> {
                        showSnackBar("Doctor updated successfully")
                        moveToHomeScreen()
                    }
                    AddNewDoctorEvents.openNewCityDialog -> showBottomSheetDialog()
                    AddNewDoctorEvents.DismissAddCityDialog -> dismissNewCityDialog()
                    is AddNewDoctorEvents.ShowDuplicateCityError -> showDuplicateEntryError(event.input)
                    AddNewDoctorEvents.SelectCityError -> showSelectCityError()
                    is AddNewDoctorEvents.PrefillDoctorCity -> prefillDoctorCity(event.selectedCityIndex)
                }
            }
        }

    }

    private fun prefillDoctorCity(selectedCityIndex: Int) {
        if (citySpinnerList.size >= selectedCityIndex)
        {
            (binding.spinnerCity.editText as AutoCompleteTextView).setText(citySpinnerList[selectedCityIndex],false)
        }
    }

    private fun showSelectCityError() {
        binding.spinnerCity.error = getString(R.string.error_please_select_a_city)
    }

    private fun dismissNewCityDialog() {
        newCityFragment.dismissDialog()
    }

    private fun showDuplicateEntryError(input: String) {
        newCityFragment.showDuplicateWarning(input)
    }

    private fun showBottomSheetDialog() {
        val bundle = Bundle()
        bundle.putString("title", "Add new City")
        bundle.putString("hint", "City name")
        bundle.putString("inputType", DialogInputType.CITY.name)
        requireActivity().supportFragmentManager.let {
            newCityFragment = NewDivisionFragment.newInstance(bundle, this).apply {
                show(it, "NewDivisionFragment")
            }

        }
    }

    private fun openDatePicker(header: String, inputType: DateInputType) {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
        val datePicker =
            PickerUtils.getDatePicker(header).setCalendarConstraints(constraintsBuilder.build())
                .build()
        datePicker.addOnPositiveButtonClickListener {
            Log.d("addNewDoctor", "openDatePicker: clicked $inputType ${DateUtils.fromLongToDateString(it)}")
            when (inputType) {
                DateInputType.DATE_OF_BIRTH -> viewmodel.updateDob(it)
                DateInputType.WEDDING_ANNIVERSARY_DATE -> viewmodel.updateWeddingDate(it)
            }
            binding.executePendingBindings()
        }
        datePicker.show(requireActivity().supportFragmentManager, "datepicker")
    }

    private fun moveToHomeScreen() {
        findNavController().navigateUp()
    }

    private fun showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(requireView(), message, duration).show()
    }

    override fun onClickDialog(vararg args: Any) {
        val input = (args[0] as String)
        viewmodel.isCityDuplicate(input)
    }
}

enum class DateInputType {
    DATE_OF_BIRTH,
    WEDDING_ANNIVERSARY_DATE
}