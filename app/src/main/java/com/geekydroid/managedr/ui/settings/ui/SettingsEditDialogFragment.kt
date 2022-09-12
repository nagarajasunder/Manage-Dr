package com.geekydroid.managedr.ui.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.AddNewCategoryDialogBinding
import com.geekydroid.managedr.ui.dialogs.NewDivisionFragment
import com.geekydroid.managedr.ui.settings.viewmodel.DialogEvents
import com.geekydroid.managedr.ui.settings.viewmodel.DialogViewmodel
import com.geekydroid.managedr.utils.GenericDialogOnClickListener
import com.geekydroid.managedr.utils.setWidthPercent
import dagger.hilt.android.AndroidEntryPoint

class SettingsEditDialogFragment : DialogFragment() {

    private lateinit var binding:AddNewCategoryDialogBinding
    private lateinit var title: String
    private lateinit var hint: String
    private lateinit var dialogInputType: String
    private lateinit var placeHolder:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_new_category_dialog,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthPercent(90)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = arguments?.getString("title", "") ?: ""
        hint = arguments?.getString("hint", "") ?: ""
        dialogInputType = arguments?.getString("inputType", "") ?: ""
        placeHolder = arguments?.getString("placeholder", "") ?: ""

        binding.title = title
        binding.hint = hint
        binding.etDivisionName.editText?.setText(placeHolder)

        binding.btnAdd.setOnClickListener {
            val input = binding.etDivisionName.editText?.text
            validateInput(input.toString())
        }

        binding.btnCancel.setOnClickListener {
            dismissDialog()
        }


    }

    private fun validateInput(input: String) {
        if (input.isEmpty() || input.length > 20)
        {
            binding.etDivisionName.error = "Please enter a valid $dialogInputType"
        }
        else
        {
            listener.onClickDialog(input,dialogInputType)
        }
    }

    fun showDuplicateWarning(input:String) {
        binding.etDivisionName.error =
            "$input is already present. Duplicates not allowed"
    }

    fun dismissDialog() {
        dismiss()
    }

    companion object{
        private lateinit var listener: GenericDialogOnClickListener
        fun newInstance(
            bundle: Bundle? = null,
            callback: GenericDialogOnClickListener,
        ): SettingsEditDialogFragment {
            val fragment = SettingsEditDialogFragment()
            fragment.arguments = bundle
            listener = callback
            return fragment
        }
    }
}