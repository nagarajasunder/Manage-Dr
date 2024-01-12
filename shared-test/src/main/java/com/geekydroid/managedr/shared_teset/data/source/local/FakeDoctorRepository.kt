package com.geekydroid.managedr.shared_teset.data.source.local

import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDoctorRepository : DoctorRepository {
    override suspend fun addNewDoctor(mdrDoctor: MdrDoctor) {
        TODO("Not yet implemented")
    }

    override fun getDoctorById(doctorId: Int): Flow<MdrDoctor> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDoctor(doctor: MdrDoctor) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewCity(newCity: MdrCity) {
        TODO("Not yet implemented")
    }

    override fun getAllCities(): Flow<List<MdrCity>> {
        return flow {
            emit(MockData.cityMockData)
        }
    }
}