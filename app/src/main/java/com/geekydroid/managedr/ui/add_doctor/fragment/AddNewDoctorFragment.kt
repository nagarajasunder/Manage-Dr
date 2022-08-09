package com.geekydroid.managedr.ui.add_doctor.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.FragmentAddNewDoctorBinding
import com.geekydroid.managedr.ui.add_doctor.viewmodel.AddNewDoctorEvents
import com.geekydroid.managedr.ui.add_doctor.viewmodel.AddNewDoctorViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewDoctorFragment : Fragment()
{
    private lateinit var binding: FragmentAddNewDoctorBinding
    private val viewmodel:AddNewDoctorViewModel by viewModels()
    private lateinit var host:FragmentActivity

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
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        host = requireActivity()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.AddNewDoctorEvent.collect { event ->
                when(event)
                {
                    AddNewDoctorEvents.SaveNewDoctor -> viewmodel.validateAndSaveNewDoctor()
                    AddNewDoctorEvents.EnterDoctorName -> showSnackBar("Please enter all the fields")
                    AddNewDoctorEvents.DoctorSavedSuccessFully -> moveToHomeScreen()
                }
            }
        }


        host.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_new_doctor_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId)
                {
                    R.id.cta_save -> viewmodel.onSaveCtaClicked()
                }
                return true
            }

        },viewLifecycleOwner,Lifecycle.State.RESUMED)
    }

    private fun moveToHomeScreen() {
        showSnackBar("New doctor added successfully")
        findNavController().navigateUp()
    }

    private fun showSnackBar(message: String,duration:Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(requireView(),message,duration).show()
    }
}