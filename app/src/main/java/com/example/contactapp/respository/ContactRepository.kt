package com.example.contactapp.respository

import androidx.lifecycle.LiveData
import com.example.contactapp.database.ContactDao
import com.example.contactapp.database.Contacts

class ContactRepository(private val contactDao: ContactDao) {

    suspend fun insert(contacts: Contacts){
        return contactDao.insert(contacts)
    }

    suspend fun update(contacts: Contacts){
        return contactDao.update(contacts)
    }

    suspend fun delete(contacts: Contacts){
        return contactDao.delete(contacts)
    }

    fun getAll():LiveData<List<Contacts>>{
        return contactDao.getAllContacts()
    }

}