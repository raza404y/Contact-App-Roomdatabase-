package com.example.contactapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactapp.database.ContactDao
import com.example.contactapp.database.ContactDatabase
import com.example.contactapp.database.Contacts
import com.example.contactapp.respository.ContactRepository
import kotlinx.coroutines.launch

class AddUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ContactDao = ContactDatabase.createDatabase(application).getDao()
    private val repository: ContactRepository = ContactRepository(dao)

    suspend fun insert(contacts: Contacts) {
        viewModelScope.launch {
            repository.insert(contacts)
        }
    }

    suspend fun update(contacts: Contacts) {
        viewModelScope.launch {
            repository.update(contacts)
        }
    }

}