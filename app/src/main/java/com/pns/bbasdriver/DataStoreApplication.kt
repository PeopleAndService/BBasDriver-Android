package com.pns.bbasdriver

import android.app.Application

class DataStoreApplication : Application() {

    private lateinit var dataStore: DataStoreModule

    companion object {
        private lateinit var dataStoreApplication: DataStoreApplication
        fun getInstance(): DataStoreApplication = dataStoreApplication
    }

    override fun onCreate() {
        super.onCreate()
        dataStoreApplication = this
        dataStore = DataStoreModule(this)
    }

    fun getDataStore(): DataStoreModule = dataStore
}