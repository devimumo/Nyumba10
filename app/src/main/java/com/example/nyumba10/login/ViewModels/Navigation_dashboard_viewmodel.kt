package com.example.nyumba10.login.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.RequestQueue
import com.example.nyumba10.Dashboard.Admin.Crime_incidences.crime_incidences_data_class
import com.example.nyumba10.login.Repository.Navigation_dashboard_repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Navigation_dashboard_viewmodel(requestQueue: RequestQueue): ViewModel() {


var Crime_data_array=MutableLiveData<ArrayList<crime_incidences_data_class>>()

val navigation_dashboard_repository=Navigation_dashboard_repository(requestQueue)

    init {

        CoroutineScope(Dispatchers.IO).launch {

navigation_dashboard_repository.network_fetch()

        }

        Crime_data_array=navigation_dashboard_repository.crime_data_arraylist_value
        }


}