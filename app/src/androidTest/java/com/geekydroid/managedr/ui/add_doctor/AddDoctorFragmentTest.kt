package com.geekydroid.managedr.ui.add_doctor


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekydroid.managedr.R
import com.geekydroid.managedr.TestFragmentFactory
import com.geekydroid.managedr.launchFragmentInHiltContainer
import com.geekydroid.managedr.ui.add_doctor.fragment.AddNewDoctorFragment
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import com.geekydroid.managedr.ui.add_doctor.viewmodel.AddNewDoctorViewModel
import com.geekydroid.managedr.utils.EspressoIdlingResource
import com.geekydroid.managedr.utils.ViewModelUtil
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AddDoctorFragmentTest {


    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var testViewModel: AddNewDoctorViewModel

    @Inject
    lateinit var repository:DoctorRepository

    private lateinit var fragmentFactory: TestFragmentFactory

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        hiltRule.inject()
        testViewModel = AddNewDoctorViewModel(repository)
        fragmentFactory = TestFragmentFactory(ViewModelUtil.createFor(testViewModel))
    }

    @Test
    fun addNewDoctorTextDisplayed()  = runTest {
        launchFragmentInHiltContainer<AddNewDoctorFragment>(fragmentFactory = fragmentFactory){

        }
        onView(withText("Add New Doctor")).check(matches(isDisplayed()))
    }

    @Test
    fun saveDoctorWithoutCityTest() = runTest {
        launchFragmentInHiltContainer<AddNewDoctorFragment>(fragmentFactory = fragmentFactory) {

        }
        onView(withId(R.id.spinner_city)).perform(click())

    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

}