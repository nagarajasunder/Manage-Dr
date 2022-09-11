package com.geekydroid.managedr.ui.settings.ui

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.geekydroid.managedr.R
import com.geekydroid.managedr.adapter.GenericAdapter
import com.geekydroid.managedr.databinding.FragmentCityDivisionBinding
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.settings.model.ActionType
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import com.geekydroid.managedr.ui.settings.viewmodel.CityDivisionFragmentEvents
import com.geekydroid.managedr.ui.settings.viewmodel.CityDivisionViewModel
import com.geekydroid.managedr.utils.DialogInputType
import com.geekydroid.managedr.utils.GenericDialogOnClickListener
import com.geekydroid.managedr.utils.UiOnClickListener
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "CityDivisionFragment"
@AndroidEntryPoint
class CityDivisionFragment : Fragment(),UiOnClickListener {

    private val viewModel: CityDivisionViewModel by viewModels()
    private lateinit var binding:FragmentCityDivisionBinding
    private lateinit var adapter:GenericAdapter
    private var SettingsEditDialog:SettingsEditDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_city_division,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()

        observeUiEvents()

        viewModel.editData.observe(viewLifecycleOwner){
            when(it)
            {
                is Resource.Error -> {}
                is Resource.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                }
                is Resource.Success -> {
                    setupAdapter(it.data)
                }
            }
        }
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect{ event ->
                when(event)
                {
                    is CityDivisionFragmentEvents.showDuplicateInputError -> showDuplicateInputWarning(event.input)
                    CityDivisionFragmentEvents.DismissDialog -> dismissDialog()
                }
            }
        }
    }

    private fun dismissDialog() {
        SettingsEditDialog?.dismissDialog()
    }

    private fun showDuplicateInputWarning(input: String) {
        SettingsEditDialog?.showDuplicateWarning(input)
    }

    private fun setupAdapter(data: List<SettingsEditData>?) {
        data?.let {
            adapter.submitList(it)
            binding.settingsRecyclerView.adapter = adapter
        }
    }

    private fun setUI() {
        binding.settingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.settingsRecyclerView.setHasFixedSize(true)
        adapter = GenericAdapter(this,R.layout.city_division_item)
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty())
            {
                if (checkedIds[0] == R.id.chip_cities)
                {
                    viewModel.updateEditType(DialogInputType.CITY)
                }
                else
                {
                    viewModel.updateEditType(DialogInputType.DIVISION)
                }
            }
        }
    }

    override fun onClick(vararg args: Any) {
        val actionType = args[0]
        val data = args[1]
        if (data is SettingsEditData)
        {
            if (actionType is ActionType)
            {
                when(actionType)
                {
                    ActionType.ACTION_TYPE_EDIT -> showEditDialog(data)
                    ActionType.ACTION_TYPE_DELETE -> showDeleteDialog(data)
                }
            }
        }
    }

    private fun showDeleteDialog(data: SettingsEditData) {
        val deleteAlertDialog = AlertDialog.Builder(requireContext())
        deleteAlertDialog.setCancelable(false)
        deleteAlertDialog.setTitle(getString(R.string.are_you_sure))
            .setMessage(getString(R.string.delete_dialog_message,data.name))
            .setPositiveButton(getString(R.string.btn_text_ok)) { dialog, _ ->
                viewModel.deleteData(data)
                dialog.dismiss()
            }
            .setNegativeButton("Close"
            ) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showEditDialog(data: SettingsEditData) {
        val bundle = Bundle()
        if (data.type == DialogInputType.CITY.name)
        {
            bundle.putString("title", "Edit city")
            bundle.putString("hint", "City Name")
        }
        else
        {
            bundle.putString("title", "Edit division")
            bundle.putString("hint", "Division name")
        }
        bundle.putString("inputType",data.type)
        bundle.putString("placeholder",data.name)
        requireActivity().supportFragmentManager.let {
            SettingsEditDialog = SettingsEditDialogFragment.newInstance(bundle,object : GenericDialogOnClickListener{
                override fun onClickDialog(vararg args: Any) {
                    val input = (args[0] as String)
                    viewModel.checkForDuplicateInput(input,data)

                }

            }).apply {
                show(it,"SettingsEditFragment")
            }
        }
    }

}