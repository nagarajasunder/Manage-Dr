package com.geekydroid.managedr.ui.addnewservice.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import java.util.*

@Entity(
    tableName = "MDR_SERVICE",
    foreignKeys = [ForeignKey(
        entity = MdrDoctor::class,
        parentColumns = ["doctor_id"],
        childColumns = ["serviced_doctor_id"],
        onUpdate = CASCADE,
        onDelete = CASCADE
    )]
)
data class MdrService(
    @ColumnInfo(name = "service_id")
    @PrimaryKey(autoGenerate = true)
    val serviceId: Int = 0,
    @ColumnInfo(name = "serviced_doctor_id")
    val servicedDoctorId: Int = 0,
    @ColumnInfo(name = "category_id")
    val categoryId: Int = 0,
    @ColumnInfo(name = "city_id")
    val cityId: Int = 0,
    @ColumnInfo(name = "service_amount")
    val serviceAmount: Int = 0,
    @ColumnInfo(name = "transaction_type")
    val transactionType:String = "",
    @ColumnInfo(name = "service_date")
    val serviceDate: Date? = null,
    @ColumnInfo(name = "created_on")
    val createdOn: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_on")
    val updatedOn: Long = System.currentTimeMillis()
)