package com.geekydroid.managedr.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.AddNewCategoryDialogBinding
import com.geekydroid.managedr.utils.GenericDialogOnClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewDivisionFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AddNewCategoryDialogBinding
    private lateinit var title: String
    private lateinit var hint: String
    private lateinit var dialogInputType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_new_category_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = arguments?.getString("title", "") ?: ""
        hint = arguments?.getString("hint", "") ?: ""
        dialogInputType = arguments?.getString("inputType", "") ?: ""

        binding.title = title
        binding.hint = hint

        binding.btnAdd.setOnClickListener {
            val input = binding.etDivisionName.editText?.text.toString()
            validateInput(input)
        }

        binding.btnCancel.setOnClickListener {
            dismissDialog()
        }

    }

    private fun validateInput(input: String?) {
        if (input.isNullOrEmpty() || input.length > 20) {
            binding.etDivisionName.error = "Please enter a valid $dialogInputType"
        } else {
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

    companion object {
        private lateinit var listener: GenericDialogOnClickListener
        fun newInstance(
            bundle: Bundle? = null,
            callback: GenericDialogOnClickListener,
        ): NewDivisionFragment {
            val fragment = NewDivisionFragment()
            fragment.arguments = bundle
            listener = callback
            return fragment
        }
    }

}