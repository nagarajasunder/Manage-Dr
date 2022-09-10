package com.geekydroid.managedr.ui.settings.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.geekydroid.managedr.R
import com.geekydroid.managedr.adapter.GenericAdapter
import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.databinding.FragmentCityDivisionBinding
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.settings.model.ActionType
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import com.geekydroid.managedr.ui.settings.model.SettingsEditType
import com.geekydroid.managedr.ui.settings.viewmodel.CityDivisionViewModel
import com.geekydroid.managedr.utils.UiOnClickListener
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "CityDivisionFragment"
@AndroidEntryPoint
class CityDivisionFragment : Fragment(),UiOnClickListener {

    private val viewModel: CityDivisionViewModel by viewModels()
    private lateinit var binding:FragmentCityDivisionBinding
    private lateinit var adapter:GenericAdapter

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
                    viewModel.updateEditType(SettingsEditType.EDIT_TYPE_CITY)
                }
                else
                {
                    viewModel.updateEditType(SettingsEditType.EDIT_TYPE_DIVISION)
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
                    ActionType.ACTION_TYPE_EDIT -> showEditDialog()
                    ActionType.ACTION_TYPE_DELETE -> showDeleteDialog()
                }
            }
        }
    }

    private fun showDeleteDialog() {

    }

    private fun showEditDialog() {

    }

}