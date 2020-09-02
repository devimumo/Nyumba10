package com.example.nyumba10.login

import android.content.Context
import android.content.Intent
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
import kotlinx.android.synthetic.main.forgot_password.*
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
private  var otp=""
class Forgot_Password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)
    }



    private fun confirm_otp(view: View) {
        //  get_OTP.visibility=View.VISIBLE
        confirm_otp.visibility=View.GONE
        forgot_password_progressbar.visibility=View.VISIBLE

        if (otp.equals(otp_from_text_view.text.toString()))
        {
            confirm_otp.visibility=View.VISIBLE
            forgot_password_progressbar.visibility=View.GONE

            val intent = Intent(applicationContext, Reset_password::class.java)

              startActivity(intent)
        }
        else
        {
            confirm_otp.visibility=View.VISIBLE

            forgot_password_progressbar.visibility=View.GONE

            Toast.makeText(this,"No match",Toast.LENGTH_LONG).show()
            otp=""
        }

    }

    private fun send_otp_to_receiver_to_acivate_account(view: View)
    {
        get_otp.visibility= View.GONE

        forgot_password_progressbar.visibility= View.VISIBLE



        val MyPreferences="mypref"
        val sharedPreferences =
            getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

         var mobile_no=phonenumber.text.toString()
        var id_no=""
        if ( mobile_no.isEmpty())
        {
            get_otp.visibility= View.VISIBLE

            forgot_password_progressbar.visibility= View.GONE

            Toast.makeText(this,"This number cannot be empty",Toast.LENGTH_LONG).show()

          //  val intent = Intent(applicationContext, RegisterActivity::class.java)

          //  startActivity(intent)
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
                            confirm_otp.visibility= View.VISIBLE
                            forgot_password_progressbar.visibility= View.GONE
                            otp=response_json_object.getString("OTP")

                            Log.d("otp", otp)

                        }
                        "error"->{


                            get_otp.visibility= View.VISIBLE

                            forgot_password_progressbar.visibility= View.GONE
                        }
                        "unsuccessful"->{

                            get_otp.visibility= View.VISIBLE

                            forgot_password_progressbar.visibility= View.GONE
                        }
                    }

                }
                catch (e: JSONException)
                {

                    get_otp.visibility= View.VISIBLE

                    forgot_password_progressbar.visibility= View.GONE
                    Log.d("JSONException",e.toString())

                }
                catch (e : ParseException)
                {

                    get_otp.visibility= View.VISIBLE

                    forgot_password_progressbar.visibility= View.GONE
                    Log.d("ParseException",e.toString())

                }


            }, Response.ErrorListener {

                val err= Volley_ErrorListener_Handler()

                get_otp.visibility= View.VISIBLE

                forgot_password_progressbar.visibility= View.GONE
                err.check_error(it,view)

            })
            {

            }
            requestQueue.add(stringRequest)





        }



    }
}