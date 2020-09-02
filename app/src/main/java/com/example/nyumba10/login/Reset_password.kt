package com.example.nyumba10.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.forgot_password.*
import kotlinx.android.synthetic.main.reset_password.*
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException

class Reset_password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password)
        
        var get_intent=intent
        var mobile_no=get_intent.getStringExtra("mobile_no")

        reset_password.setOnClickListener {

            if (password1.text.isEmpty())
            {
                password1.error="password cannot be empty"
            }
            else {

                if (password1.text.length < 6) {
                    Toast.makeText(
                        this@Reset_password,
                        "password must be at least 6 characters",
                        Toast.LENGTH_LONG
                    ).show()
                    //  progressbar!!.visibility = View.INVISIBLE
                    signUp!!.visibility = View.VISIBLE
                }else
                {
                if (password2.text.isEmpty()) {
                    password2.error = "password cannot be empty"

                } else {
                    if (password1.text == password2.text) {
                        reset_password_to_db_volley(it, mobile_no!!, password2.text.toString())

                    } else {
                        Toast.makeText(this, "Pawwords must match", Toast.LENGTH_LONG).show()
                    }
                }
            }

            }

        }
    }

private fun reset_password_to_db_volley(view: View,mobile_no: String,new_passowrd: String)
    {


        val requestQueue = Volley.newRequestQueue(this)
        val url="https://daudi.azurewebsites.net/nyumbakumi/login/update_forgotten_password.php?mobile_no="+ mobile_no+"&new_password="+new_passowrd 
        val stringRequest: StringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
            Log.d("response_crime_data",response)

            try {

                var response_json_object= JSONObject(response)
                var status=response_json_object.getString("status")


                when(status)
                {
                    "success"->{
                        reset_password.visibility= View.VISIBLE
                        rest_password_progressbar.visibility= View.GONE


                    }
                    "error"->{


                        reset_password.visibility= View.VISIBLE

                        rest_password_progressbar.visibility= View.GONE
                    }
                    "unsuccessful"->{

                        reset_password.visibility= View.VISIBLE

                        rest_password_progressbar.visibility= View.GONE
                    }
                }

            }
            catch (e: JSONException)
            {

                reset_password.visibility= View.VISIBLE

                rest_password_progressbar.visibility= View.GONE
                Log.d("JSONException",e.toString())

            }
            catch (e : ParseException)
            {

                reset_password.visibility= View.VISIBLE

                rest_password_progressbar.visibility= View.GONE
                Log.d("ParseException",e.toString())

            }


        }, Response.ErrorListener {

            val err= Volley_ErrorListener_Handler()
            reset_password.visibility= View.VISIBLE


            rest_password_progressbar.visibility= View.GONE
            err.check_error(it,view)

        })
        {

        }
        requestQueue.add(stringRequest)




    }
}