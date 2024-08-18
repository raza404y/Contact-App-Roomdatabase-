package com.example.contactapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.contactapp.database.ContactDao
import com.example.contactapp.database.ContactDatabase
import com.example.contactapp.database.Contacts
import com.example.contactapp.respository.ContactRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ContactDao = ContactDatabase.createDatabase(application).getDao()
    private val repository: ContactRepository = ContactRepository(dao)
    var contactList: LiveData<List<Contacts>> = repository.getAll()


    suspend fun delete(contacts: Contacts) = viewModelScope.launch {
        repository.delete(contacts)
    }


}