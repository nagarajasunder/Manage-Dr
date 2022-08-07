package com.example.managedr.ui.add_doctor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.managedr.R
import com.example.managedr.databinding.FragmentAddNewDoctorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewDoctorFragment : Fragment()
{
    private lateinit var binding:FragmentAddNewDoctorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_doctor,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}