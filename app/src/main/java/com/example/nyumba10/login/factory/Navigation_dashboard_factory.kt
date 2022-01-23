package com.example.nyumba10.login.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.example.nyumba10.login.ViewModels.Navigation_dashboard_viewmodel

class Navigation_dashboard_factory(var requestQueue: RequestQueue): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(Navigation_dashboard_viewmodel::class.java)){
            return Navigation_dashboard_viewmodel(requestQueue) as T
        }
        throw IllegalArgumentException("Unknown class")
    }
}