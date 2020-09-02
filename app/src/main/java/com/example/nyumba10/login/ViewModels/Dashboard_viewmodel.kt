package com.example.nyumba10.login.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Security.Encrypt
import com.example.nyumba10.login.DashBoard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.HashMap



class Dashboard_viewmodel(application: Application) :AndroidViewModel(application) {

    val MyPreferences = "mypref"
    var sharedPreferences =application.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

    private  lateinit var  primary_residense_polygon_list: String


   fun set_list(list: String)
    {


        primary_residense_polygon_list=list

    }
      fun get_polygon_list_volley(application: Application) {


              val requestQueue = Volley.newRequestQueue(application.applicationContext)

              val url =
                  "https://daudi.azurewebsites.net/nyumbakumi/login/get_association_polygon.php";
              val stringRequest: StringRequest =object : StringRequest(Method.POST, url, Response.Listener {


                      var response = JSONObject(it)
                      var state = response.getString("state")
                      if (state.equals("successful")) {
                          primary_residense_polygon_list = response.getString("list")

                          Log.d("primary_residense_", primary_residense_polygon_list)

                          val MyPreferences = "mypref"
                          var sharedPreferences =application.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                          val editor = sharedPreferences.edit()
                          editor.remove("primary_residense_polygon_list")
                          editor.putString(
                              "primary_residense_polygon_list",
                              primary_residense_polygon_list
                          )

                          editor.apply()



                      } else {
                          primary_residense_polygon_list = ""
                          //Toast.makeText(this,"N",Toast.LENGTH_LONG).show()
                      }

                  }, Response.ErrorListener {

                  }) {

                      @Throws(AuthFailureError::class)
                      override fun getParams(): Map<String, String> {
                          val params: MutableMap<String, String> =
                              HashMap()
                          val MyPreferences = "mypref"
                          val sharedPreferences =
                              application.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                          var association_id = sharedPreferences.getString("association_id", "");

                          params["association_id"] = association_id!!

                          return params
                      }


                  }
              requestQueue.add(stringRequest)

          }

}
