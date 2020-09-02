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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

var instanse_id=""
class Get_firebase_Instanse_id {

    fun get_instanse_id(id_no: String,context: Context) {

     //   val conte = context
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("refused", "getInstanceId failed", task.exception)

                    instanse_id="unsuccessful"
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                // val msg = getString(R.string.msg_token_fmt, token)
                Log.d("agreed", token!!)
                if (token != null) {

                    // val vii=View(context)
                  //  var send_to_server = Update_firebase_instance_id()

                  //  send_to_server.update_instance_id(context, token, phone_number, username)

                    instanse_id=token
                    Log.d("tokeeens",token)
                    send_to_db_instanse_id(id_no,token,context)

                }

                //  Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })

    }


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

}