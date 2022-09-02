package com.geekydroid.managedr.ui.addnewservice.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geekydroid.managedr.R
import com.geekydroid.managedr.application.TransactionType
import com.geekydroid.managedr.databinding.FragmentNewServiceBinding
import com.geekydroid.managedr.ui.addnewservice.viewmodel.AddNewServiceFragmentViewmodel
import com.geekydroid.managedr.ui.addnewservice.viewmodel.NewServiceFragmentEvents
import com.geekydroid.managedr.ui.dialogs.NewDivisionFragment
import com.geekydroid.managedr.utils.DialogInputType
import com.geekydroid.managedr.utils.GenericDialogOnClickListener
import com.geekydroid.managedr.utils.uiutils.PickerUtils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "NewServiceFragment"
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
    private lateinit var host: MenuHost
    private lateinit var transactionType: TransactionType

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
        doctorId = args.doctorId
        transactionType = args.transactionType
        binding.viewmodel = viewmodel
        binding.transactionType = transactionType
        binding.lifecycleOwner = viewLifecycleOwner
        viewmodel.getDoctorName(doctorId)
        setUI()
        observeUiEvents()
        viewmodel.cityData.observe(viewLifecycleOwner) { cities ->
            setupCitySpinner(cities.map { it.cityName })
        }
        viewmodel.categoryData.observe(viewLifecycleOwner) { categories ->
            setupDivisionSpinner(categories.map { it.categoryName })
        }

        host.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_new_doctor_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.cta_save -> {
                        viewmodel.onSaveCtaClicked(doctorId, transactionType)
                        return true
                    }
                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun setUI() {
        (binding.spinnerCity.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { _, _, index, _ ->
                viewmodel.updateSelectedCity(index)
            }

        (binding.spinnerCategory.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { _, _, index, _ ->
                viewmodel.updateSelectedCategory(index)
            }
    }

    private fun setupDivisionSpinner(list: List<String>) {
        divisionSpinnerList.clear()
        divisionSpinnerList.addAll(list)
        divisionSpinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            divisionSpinnerList)
        (binding.spinnerCategory.editText as AutoCompleteTextView).setAdapter(divisionSpinnerAdapter)
        if (viewmodel.selectedCategoryIndex != -1) {
            (binding.spinnerCategory.editText as AutoCompleteTextView).setText(divisionSpinnerList[viewmodel.selectedCategoryIndex])
        }
    }

    private fun setupCitySpinner(list: List<String>) {
        citySpinnerList.clear()
        citySpinnerList.addAll(list)
        citySpinnerAdapter = ArrayAdapter<String>(requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            citySpinnerList)
        if (viewmodel.selectedCityIndex != -1) {
            (binding.spinnerCity.editText as AutoCompleteTextView).setText(citySpinnerList[viewmodel.selectedCityIndex])
        }
        (binding.spinnerCity.editText as AutoCompleteTextView).setAdapter(citySpinnerAdapter)
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.newServiceEvents.collect {
                when (it) {
                    NewServiceFragmentEvents.showDatePickerDialog -> openDatePicker()
                    NewServiceFragmentEvents.showNewCategoryDialog -> showBottomSheetDialog(
                        DialogInputType.DIVISION
                    )
                    NewServiceFragmentEvents.showNewCityDialog -> showBottomSheetDialog(
                        DialogInputType.CITY
                    )
                    NewServiceFragmentEvents.newServiceCreated -> {
                        showSnackbar(requireContext().getString(R.string.new_service_created))
                        findNavController().navigateUp()
                    }
                    NewServiceFragmentEvents.newCollectionCreated -> {
                        showSnackbar(requireContext().getString(R.string.new_collection_created))
                        findNavController().navigateUp()
                    }
                    NewServiceFragmentEvents.selectCategoryError -> showCategorySpinnerError()
                    NewServiceFragmentEvents.selectCityError -> showCitySpinnerError()
                    NewServiceFragmentEvents.transactionAmountError -> showTransactionAmountError()
                    NewServiceFragmentEvents.dismissNewDivisionDialog -> dismissNewDivisionDialog()
                    is NewServiceFragmentEvents.showDuplicateWarningInDialog -> showDuplicateWarning(
                        it.input)
                    NewServiceFragmentEvents.transactionDateError -> showTransactionDateError()
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showTransactionDateError() {
        binding.tvTransactionDatePicker.error =
            requireContext().getString(R.string.error_please_select_a_transaction_date)
    }

    private fun showTransactionAmountError() {
        binding.edServiceAmount.error = "Please enter the transaction amount"
    }

    private fun showCategorySpinnerError() {
        binding.spinnerCategory.error =
            requireContext().getString(R.string.error_please_select_division)
    }

    private fun showCitySpinnerError() {
        binding.spinnerCity.error = requireContext().getString(R.string.error_please_select_a_city)
    }

    private fun openDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
        val datePicker = PickerUtils.getDatePicker().setCalendarConstraints(constraintsBuilder.build()).build()
        datePicker.addOnPositiveButtonClickListener {
            viewmodel.updateTransactionDate(it)
        }
        datePicker.show(requireActivity().supportFragmentManager, "datepicker")
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

    private fun showDuplicateWarning(input: String) {
        newDivisionFragment.showDuplicateWarning(input)
    }

    private fun dismissNewDivisionDialog() {
        newDivisionFragment.dismissDialog()
    }

}