package com.example.nyumba10.Dashboard.Admin.Crime_incidences

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class crime_incidences_data_class (var latong: String,var id_no: String,var tag: String,var time: String
                                        ,var incident_type: String,var crime_description: String,var location_description: String) : Serializable{
}