package com.geekydroid.managedr.ui.add_doctor


import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekydroid.managedr.launchFragmentInHiltContainer
import com.geekydroid.managedr.ui.add_doctor.fragment.AddNewDoctorFragment
import com.geekydroid.managedr.ui.add_doctor.viewmodel.AddNewDoctorViewModel
import com.geekydroid.managedr.utils.ViewModelUtil
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddDoctorFragmentTest {

    lateinit var viewModel: AddNewDoctorViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(relaxed = true)
        viewModel = mockk()
    }

    @Test
    fun dummy() {
        launchFragmentInHiltContainer<AddNewDoctorFragment>{
        }
        onView(withText("Add New Doctor")).check(matches(isDisplayed()))
    }

}