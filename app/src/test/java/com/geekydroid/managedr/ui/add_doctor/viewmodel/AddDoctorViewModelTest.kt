package com.geekydroid.managedr.ui.add_doctor.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AddDoctorViewModelTest {


    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var repository: DoctorRepository

    private lateinit var viewModel:AddNewDoctorViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun getAllCitiesTest() = runTest {
        val mockResponse = listOf(MdrCity(cityId = 1, cityName = "Chennai"))
        coEvery {
            repository.getAllCities()
        }.coAnswers {
            flow {
                emit(mockResponse)
            }
        }
        viewModel = AddNewDoctorViewModel(repository)
        viewModel.cityData.observeForever {}
        viewModel.getCityData()
        assertThat(viewModel.cityData.value).isEqualTo(mockResponse)
    }

}