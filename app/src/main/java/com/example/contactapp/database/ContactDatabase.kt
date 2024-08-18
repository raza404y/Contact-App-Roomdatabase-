package com.example.contactapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contacts::class], version = 1, exportSchema = false)
abstract class ContactDatabase: RoomDatabase() {

    abstract fun getDao():ContactDao

    companion object{
        private var INSTANCE: ContactDatabase? = null
        fun createDatabase(context: Context):ContactDatabase{
            if (INSTANCE == null){
                synchronized(Contacts::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "contact_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }

}