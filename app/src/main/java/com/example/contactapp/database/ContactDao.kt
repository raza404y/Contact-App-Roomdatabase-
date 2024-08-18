package com.example.contactapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contacts: Contacts)

    @Update
    suspend fun update(contacts: Contacts)

    @Delete
    suspend fun delete(contacts: Contacts)

    @Query("Select * from contact_table order by id desc")
    fun getAllContacts():LiveData<List<Contacts>>

}