package com.example.nyumba10.login

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import kotlinx.android.synthetic.main.confirm_account_activation_pin.*
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
private var otp=""
private var id_no: String=""
private var mobile_no: String=""
class Confirm_account_activation_pin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_account_activation_pin)


        if (otp.equals(""))
        {
            get_view.visibility=View.VISIBLE

            confirm_view.visibility=View.GONE
        }
    }


    private fun send_otp_to_receiver_to_acivate_account(view: View)
    {


        val MyPreferences="mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
id_no=sharedPreferences.getString("id_no","").toString()
        mobile_no=sharedPreferences.getString("mobile_no","").toString()


        if (id_no.isEmpty() && mobile_no.isEmpty())
        {
            get_view.visibility=View.VISIBLE

            progress_GET_OTP.visibility=View.GONE

            val intent = Intent(applicationContext, RegisterActivity::class.java)

          startActivity(intent)
        }
        else{

                val requestQueue = Volley.newRequestQueue(this)
                val url="https://daudi.azurewebsites.net/nyumbakumi/login/send_sms.php?id_no="+ id_no+"&receiver="+ mobile_no
                val stringRequest: StringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
                    Log.d("response_crime_data",response)

                    try {

                        var response_json_object= JSONObject(response)
                        var status=response_json_object.getString("status")


                        when(status)
                        {
                            "success"->{
                                   confirm_view.visibility=View.VISIBLE
                                  progress_GET_OTP.visibility=View.GONE
                                 otp=response_json_object.getString("OTP")

                                Log.d("otp", otp)

                            }
                            "error"->{


                                get_view.visibility=View.VISIBLE

                                progress_GET_OTP.visibility=View.GONE
                            }
                            "unsuccessful"->{

                                get_view.visibility=View.VISIBLE

                                progress_GET_OTP.visibility=View.GONE
                            }
                        }

                    }
                    catch (e: JSONException)
                    {

                        get_view.visibility=View.VISIBLE

                        progress_GET_OTP.visibility=View.GONE
                        Log.d("JSONException",e.toString())

                    }
                    catch (e : ParseException)
                    {

                        get_view.visibility=View.VISIBLE

                        progress_GET_OTP.visibility=View.GONE
                        Log.d("ParseException",e.toString())

                    }


                }, Response.ErrorListener {

                    val err= Volley_ErrorListener_Handler()

                    get_view.visibility=View.VISIBLE

                    progress_GET_OTP.visibility=View.GONE
                    err.check_error(it,view)

                })
                {

                }
                requestQueue.add(stringRequest)





        }



    }

    private fun confirm_otp(view: View) {
      //  get_OTP.visibility=View.VISIBLE
      //  confirm_pin.visibility=View.GONE
        progress_GET_OTP.visibility=View.VISIBLE

        if (otp.equals(otp_from_text_view.text.toString()))
        {

           update_account_status_volley(view)


        }
        else
        {
            get_view.visibility=View.VISIBLE

            progress_GET_OTP.visibility=View.GONE

            Toast.makeText(this,"No match",Toast.LENGTH_LONG).show()
            otp=""
        }

    }

    fun click_action(view: View) {

        when(view.id)
        {
            R.id.get_OTP->{

                progress_GET_OTP.visibility=View.VISIBLE
                get_view.visibility=View.GONE
                send_otp_to_receiver_to_acivate_account(view)
            }

            R.id.confirm_pin->
            {
                progress_GET_OTP.visibility=View.VISIBLE
                confirm_view.visibility=View.GONE
                confirm_otp(view)
            }
            R.id.signup->{
                val intent = Intent(view.context, RegisterActivity::class.java)

                startActivity(intent)
            }
        }
    }



    private fun update_account_status_volley(view: View)
    {

       val requestQueue = Volley.newRequestQueue(this)
        val url="https://daudi.azurewebsites.net/nyumbakumi/login/update_account_status.php?id_no="+ id_no
        val stringRequest: StringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
            Log.d("response_crime_data",response)

            try {

                var response_json_object= JSONObject(response)
                var status=response_json_object.getString("response")



                when(status)
                {
                    "success"->{


                        val MyPreferences="mypref"
                        val sharedPreferences =
                            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

                        val editor: SharedPreferences.Editor = sharedPreferences.edit()

                        editor.remove("account_regstration_pin")
                        editor.putString("account_regstration_pin","confirmed")

                        editor.apply()

                        if (editor.commit())
                        {
                            val intent = Intent(view.context, Profile::class.java)

                            startActivity(intent)
                        }

                        progress_GET_OTP.visibility=View.GONE

                    }
                    "error"->{


                        progress_GET_OTP.visibility=View.GONE
                    }
                    "unsuccessful"->{

                        progress_GET_OTP.visibility=View.GONE
                    }
                }

            }
            catch (e: JSONException)
            {

                progress_GET_OTP.visibility=View.GONE
                Log.d("JSONException",e.toString())

            }
            catch (e : ParseException)
            {


                progress_GET_OTP.visibility=View.GONE
                Log.d("ParseException",e.toString())

            }

        }, Response.ErrorListener {

            val err= Volley_ErrorListener_Handler()

            progress_GET_OTP.visibility=View.GONE
            err.check_error(it,view)

        })
        {

        }
        requestQueue.add(stringRequest)


    }


    override fun onBackPressed() {

       // super.onBackPressed()

        val MyPreferences = "mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val account_regstration_pin = sharedPreferences.getString("account_regstration_pin", "!confirmed")
        if (account_regstration_pin.equals("!confirmed"))
        {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("You have not conluded registration. \n" +"Do you want exit?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->

                        finish()

                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog

                    })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        }
        else{
            super.onBackPressed()

        }
    }

}