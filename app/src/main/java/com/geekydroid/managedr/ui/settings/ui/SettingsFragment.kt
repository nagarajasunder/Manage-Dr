package com.geekydroid.managedr.ui.settings.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geekydroid.managedr.R
import com.geekydroid.managedr.application.services.DataExportService
import com.geekydroid.managedr.databinding.FragmentSettingsBinding
import com.geekydroid.managedr.ui.settings.model.SettingsEditType
import com.geekydroid.managedr.ui.settings.viewmodel.SettingsEvents
import com.geekydroid.managedr.ui.settings.viewmodel.SettingsViewmodel
import com.geekydroid.managedr.utils.DateUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding:FragmentSettingsBinding
    private val viewmodel:SettingsViewmodel by viewModels()
    private val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            val uri = result.data!!.data
            viewmodel.exportData(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiEvents()

        binding.tvExportData.setOnClickListener {
            viewmodel.exportDataClicked()
        }

        binding.tvSettingsEditable.setOnClickListener {
            viewmodel.settingsEditableClicked()
        }
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.events.collect{ event ->
                when(event)
                {
                    SettingsEvents.openFilePicker -> openFilePicker()
                    is SettingsEvents.exportDataToExcel -> {
                        startExportingData(event.uri)
                    }
                    SettingsEvents.showNoTransactionError -> "No transaction found to export data".showSnackBar()
                    is SettingsEvents.openCityDivisionScreen -> navigateToCityDivisionFragment()
                }
            }
        }
    }

    private fun navigateToCityDivisionFragment() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToCityDivisionFragment()
        findNavController().navigate(action)
    }

    private fun startExportingData(uri: Uri) {
        val intent = Intent(requireContext(),DataExportService::class.java)
        intent.putExtra("fileuri",uri.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        }
        else
        {
            requireContext().startService(intent)
        }
    }

    private fun String.showSnackBar() {
        Snackbar.make(requireView(), this,Snackbar.LENGTH_SHORT).show()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
            putExtra(
                Intent.EXTRA_TITLE,
                "Manage Dr ${DateUtils.getTimeStampFormatted()}"
            )
        }
        activityResult.launch(intent)
    }


}