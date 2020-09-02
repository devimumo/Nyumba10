package com.example.nyumba10.login.Firebase_Instance_id

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Security.Encrypt
import com.example.nyumba10.login.Login
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


class MyFirebaseInstanceIDService : FirebaseMessagingService()  {


      /**
       * Called if InstanceID token is updated. This may occur if the security of
       * the previous token had been compromised. Note that this is called when the InstanceID token
       * is initially generated so this is where you would retrieve the token.
       */
      override fun onNewToken(token: String) {
          Log.d("new_id", "Refreshed token: $token")
          val MyPreferences="mypref"
          val sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
          var id_no=sharedPreferences.getString("id_no","")
          if (token != null) {

              Log.d("instance_id_error","update_firebase_instance_id_error")
          }
          else
          {
              if (id_no != null) {
                  send_to_db_instanse_id(id_no,token,applicationContext)
              }
          }
          }
          // If you want to send messages to this application instance or
          // manage this apps subscriptions on the server side, send the
          // Instance ID token to your app server.
        //  sendRegistrationToServer(token)

    private fun send_to_db_instanse_id(id_no: String,instanseId: String,context: Context
    ) {
        val encrypt = Encrypt()
//+"?firstname="+FirstName+"&lastname="+LastName+"&email="+Email+"&id_no="+id_no+"&mobile_no="+Mobile_no+"&password="+Password
        val url ="https://daudi.azurewebsites.net/nyumbakumi/login/update_firebase_instance_id.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST,url, Response.Listener { response ->
            Log.i("Responsed", response)
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(response)
                val responses = jsonObject.getString("response")
                when (responses) {
                    "successful" -> {

                    }
                    else -> {

                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            Log.i("Volley_Error", error.toString())
            //  progressbar!!.visibility = View.INVISIBLE
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                val encrypt = Encrypt()
                //    val times = Time_function()
                //  params["time_"] = times.current_time()
                //  params["date_"] = times.current_date()

                params["id_no"] = id_no
                params["firebase_instance_id"] = instanseId
                Log.d("firebase_instance_id",instanseId)


                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(context)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(DefaultRetryPolicy(80000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
    }


    private fun update_firebase_instance_id(id_no: String,instanseId: String) {
        val encrypt = Encrypt()
        val url ="https://daudi.azurewebsites.net/nyumbakumi/login/update_firebase_instance_id.php"+"?firebase_instance_id="+instanseId+"&id_no="+id_no
        val stringRequest: StringRequest = object : StringRequest(Method.GET,url, Response.Listener { response ->
            Log.i("Responsed", response)
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(response)
                val responses = jsonObject.getString("response")
                when (responses) {
                    "successful" -> {
                        val MyPreferences="mypref"
                        val sharedPreferences =getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()

                        //    editor.putString("phone_number",email);
                        editor.putString("firebase_instance_id", "firebase_instance_id")
                        editor.apply()
                    }
                    else -> {

                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            Log.i("Volley_Error", error.toString())
        }) {
            /*  @Throws(AuthFailureError::class)
              override fun getParams(): Map<String, String> {
                  val params: MutableMap<String, String> =
                      HashMap()
                  val encrypt = Encrypt()
                  //    val times = Time_function()
                  //  params["time_"] = times.current_time()
                  //  params["date_"] = times.current_date()
                  params["firstname"] = FirstName
                  params["lastname"] = LastName
                  params["email"] = Email
                  params["id_no"] = id_no
                  params["mobile_no"] = Mobile_no
                  params["firebase_instance_id"] = instanseId

                  try {
                      //SecretKeySpec keys=encrypt.generateKey(Password);
                      //String key=keys.toString();
                      params["password"] = Password
                  } catch (e: Exception) {
                      e.printStackTrace()
                  }
                  return params
              }*/
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(DefaultRetryPolicy(80000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
    }

      }


