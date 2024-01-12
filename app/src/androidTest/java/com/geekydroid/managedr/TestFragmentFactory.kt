package com.geekydroid.managedr

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.geekydroid.managedr.ui.add_doctor.fragment.AddNewDoctorFragment
import javax.inject.Inject

class TestFragmentFactory (private val viewModelFactory:ViewModelProvider.Factory) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            AddNewDoctorFragment::class.java.name -> AddNewDoctorFragment(viewModelFactory)
            else -> super.instantiate(classLoader, className)
        }
    }
}