package com.geekydroid.managedr.ui.add_doctor.dao

import androidx.room.*
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import com.geekydroid.managedr.ui.add_doctor.model.SortPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface doctorDao {

    @Insert
    suspend fun insertNewDoctor(newMdrDoctor: MdrDoctor)

    fun getDoctorDataBasedOnFilters(
        searchText: String,
        sortPreferences: String,
    ): Flow<List<HomeScreenDoctorData>> = when (sortPreferences) {
        SortPreferences.SORT_BY_NAME_ASC.name -> getDoctorDataSortByNameAsc(searchText)
        SortPreferences.SORT_BY_NAME_DESC.name -> getDoctorDataSortByNameDesc(searchText)
        SortPreferences.NEWEST_FIRST.name -> getDoctorDataNewestFirst(searchText)
        SortPreferences.OLDEST_FIRST.name -> getDoctorDataOldestFirst(searchText)
        else -> getDoctorDataNewestFirst(searchText)
    }


    //Data to be displayed in the home screen
    @Query(
        "SELECT doctor_id as doctorID, " +
                "city_id as cityId, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "date_of_birth as doctorDateOfBirth, " +
                "wedding_anniversary_date as doctorWeddingDate, "+
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR WHERE doctorName LIKE '%' || :searchText || '%'"
    )
    fun getDoctorDataWithSearchText(searchText:String): Flow<List<HomeScreenDoctorData>>

    @Query(
        "SELECT doctor_id as doctorID, " +
                "city_id as cityId, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "date_of_birth as doctorDateOfBirth, " +
                "wedding_anniversary_date as doctorWeddingDate, "+
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR WHERE doctorName LIKE '%' || :searchText || '%' ORDER BY doctorName ASC"
    )
    fun getDoctorDataSortByNameAsc(searchText: String): Flow<List<HomeScreenDoctorData>>

    @Query(
        "SELECT doctor_id as doctorID, " +
                "city_id as cityId, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "date_of_birth as doctorDateOfBirth, " +
                "wedding_anniversary_date as doctorWeddingDate, "+
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR WHERE doctorName LIKE '%' || :searchText || '%' ORDER BY doctorName DESC"
    )
    fun getDoctorDataSortByNameDesc(searchText: String): Flow<List<HomeScreenDoctorData>>


    @Query(
        "SELECT doctor_id as doctorID, " +
                "city_id as cityId, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "date_of_birth as doctorDateOfBirth, " +
                "wedding_anniversary_date as doctorWeddingDate, "+
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR WHERE doctorName LIKE '%' || :searchText || '%' ORDER BY createdOn DESC"
    )
    fun getDoctorDataNewestFirst(searchText: String): Flow<List<HomeScreenDoctorData>>

    @Query(
        "SELECT doctor_id as doctorID, " +
                "city_id as cityId, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "date_of_birth as doctorDateOfBirth, " +
                "wedding_anniversary_date as doctorWeddingDate, "+
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR WHERE doctorName LIKE '%' || :searchText || '%' ORDER BY createdOn ASC"
    )
    fun getDoctorDataOldestFirst(searchText: String): Flow<List<HomeScreenDoctorData>>

    //Data to be displayed in the doctor dashboard
    @Query(
        "SELECT doctor_id as doctorID, " +
                "city_id as cityId, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "date_of_birth as doctorDateOfBirth, " +
                "wedding_anniversary_date as doctorWeddingDate, "+
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR WHERE doctor_id = :doctorId"
    )
    fun getDoctorDataByDoctorId(doctorId: Int): Flow<HomeScreenDoctorData>

    @Query("SELECT doctor_name FROM MDR_DOCTOR WHERE doctor_id = :doctorId")
    fun getDoctorName(doctorId: Int): Flow<String>

    @Query("SELECT * FROM MDR_DOCTOR WHERE doctor_id = :doctorId")
    fun getDoctorById(doctorId: Int): Flow<MdrDoctor>

    @Update
    suspend fun updateDoctor(doctor: MdrDoctor)

    @Transaction
    suspend fun updateDoctorAndTransactions(doctor: MdrDoctor)
    {
        updateDoctor(doctor)
        updateDoctorTransactions(doctor.cityId,doctor.doctorID)
    }

    @Query("UPDATE MDR_SERVICE SET city_id = :cityId WHERE serviced_doctor_id = :doctorId")
    fun updateDoctorTransactions(cityId: Int,doctorId: Int)


}