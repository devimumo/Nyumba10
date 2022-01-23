package com.example.nyumba10.login

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.R
import com.example.nyumba10.Security.Encrypt
import com.example.nyumba10.login.Firebase_Instance_id.Get_firebase_Instanse_id
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.crime_info_layout.view.*
import kotlinx.android.synthetic.main.reportcrime.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        already_have_account_prob.setOnClickListener {

            val login_activity_intent=Intent(this,Login::class.java)
            startActivity(login_activity_intent)
        }
        signUp.setOnClickListener(View.OnClickListener {
            signup()

        })
    }

    companion object {
        //methods to check characters in password
        private val inputRegexes =
            arrayOfNulls<Pattern>(4)

        private fun isMatchingRegex(input: String): Boolean {
            var inputMatches = true
            for (inputRegex in inputRegexes) {
                if (!inputRegex!!.matcher(input).matches()) {
                    inputMatches = false
                }
            }
            return inputMatches
        }

        init {
            inputRegexes[0] =Pattern.compile(".*[A-Z].*")
            inputRegexes[1] =Pattern.compile(".*[a-z].*")
            inputRegexes[2] =Pattern.compile(".*\\d.*")
            inputRegexes[3] =Pattern.compile(".*[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?].*")
        }
    }

    private fun signup() {
        signUp.visibility = View.GONE
        this.progress.visibility = View.VISIBLE

        val FirstName = firstName.getText().toString()
        val LastName = lastName.getText().toString()
        val Email = email.getText().toString()
        val id_no = id_no.getText().toString()
        val Mobile_no = mobileNumber.getText().toString()
        val Password = passWord.getText().toString()


        val Confirmpassword = confirmpassword.getText().toString()
        val username_value = Mobile_no.length
        val pass_value = Password.length
        if (FirstName.isEmpty() or LastName.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Names required", Toast.LENGTH_LONG).show()
            signUp!!.visibility = View.VISIBLE
        } else if (Email.isEmpty()) {
            email.setError("email required")
          //  progressbar!!.visibility = View.INVISIBLE
            signUp!!.visibility = View.VISIBLE
        } else if (TextUtils.isEmpty(Mobile_no)) {
            mobileNumber.setError("Phone number  required ")
            //progressbar!!.visibility = View.INVISIBLE
            signUp!!.visibility = View.VISIBLE
        } else if (username_value != 9) {
            Toast.makeText(this@RegisterActivity, "not a valid phone number", Toast.LENGTH_LONG)
                .show()
          //  progressbar!!.visibility = View.INVISIBLE
            signUp!!.visibility = View.VISIBLE
        } else if (TextUtils.isEmpty(Password)) {
            passWord.setError("Password required ")
           // progressbar!!.visibility = View.INVISIBLE
            signUp!!.visibility = View.VISIBLE
        } else if (pass_value < 6) {
            Toast.makeText(
                this@RegisterActivity,
                "password must be at least 6 characters",
                Toast.LENGTH_LONG
            ).show()
          //  progressbar!!.visibility = View.INVISIBLE
            signUp!!.visibility = View.VISIBLE
        }
       /* else if (!isMatchingRegex(Password)) {
            Toast.makeText(
                this@RegisterActivity,
                "password must include alphabets,special characters,numbers",
                Toast.LENGTH_LONG
            ).show()
         //   progressbar!!.visibility = View.INVISIBLE
            signUp!!.visibility = View.VISIBLE
        }
        */
        else if (Password != Confirmpassword) {
            confirmpassword.setError("Psswords must match")
            this.progress.visibility = View.GONE
            signUp!!.visibility = View.VISIBLE
        } else {



get_crime_info_alert_dialog(FirstName, LastName, Email,id_no, Mobile_no, Password)

        }
    }



    private fun get_crime_info_alert_dialog(FirstName: String,LastName: String,Email: String,id_no: String,Mobile_no: String,Password: String)
    {
        val builder = AlertDialog.Builder(this)
        // Get the layout inflater
        val inflater: LayoutInflater = LayoutInflater.from(this)
        var inflated=inflater.inflate(R.layout.association_rules_layout, null)

        builder.setView(inflated)
        builder.setTitle("By clicking okay you agree to the rules set by the Nyumba Kumi associations")

        builder.setPositiveButton("Okay",
            DialogInterface.OnClickListener { dialog, id ->

                register(FirstName, LastName, Email,id_no, Mobile_no, Password)

            })
        builder.setCancelable(true)



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Add action buttons

        builder.create()




        builder.show()

    }

    private fun register(FirstName: String,LastName: String,Email: String,id_no: String,Mobile_no: String,Password: String
    ) {
        val encrypt = Encrypt()
//+"?firstname="+FirstName+"&lastname="+LastName+"&email="+Email+"&id_no="+id_no+"&mobile_no="+Mobile_no+"&password="+Password
        val url ="https://daudi.azurewebsites.net/nyumbakumi/login/register.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST,url, Response.Listener { response ->
                Log.i("Responsed", response)
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(response)
                    val responses = jsonObject.getString("response")
                    when (responses) {
                        "successful" -> {

                            Get_firebase_Instanse_id().get_instanse_id(id_no,this)

                            Toast.makeText(applicationContext, "Registration successful", Toast.LENGTH_LONG).show()

                            val MyPreferences="mypref"
                            val sharedPreferences =
                                getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()

                            //    editor.putString("phone_number",email);

                            editor.remove("mobile_no")
                            editor.remove("registration_status")
                            editor.remove("id_no")

                            editor.putString("registration_status", "registered")
                            editor.putString("id_no", id_no)
                            editor.putString("mobile_no", Mobile_no)


                            editor.apply()
                            progress!!.visibility = View.GONE
                            signUp.visibility=View.VISIBLE


                            val intent =Intent(applicationContext, Confirm_account_activation_pin::class.java)
                            startActivity(intent)

                        }
                        "mobile_no_exists" -> {
                            Toast.makeText(applicationContext, "User already exists. \n Register with a different mobile  no", Toast.LENGTH_LONG).show()
                            progress!!.visibility = View.GONE
                            signUp!!.visibility = View.VISIBLE
                        }
                        "unsuccessful" -> {
                            Toast.makeText(applicationContext, "Registration unsuccessful", Toast.LENGTH_LONG).show()
                            progress!!.visibility = View.GONE
                            signUp!!.visibility = View.VISIBLE
                        }
                        else -> {
                            Toast.makeText(applicationContext, "Try again$response", Toast.LENGTH_LONG).show()
                            progress!!.visibility = View.GONE
                            signUp!!.visibility = View.VISIBLE
                        }
                    }
                } catch (e: JSONException) {

                    progress!!.visibility = View.GONE
                    signUp.visibility=View.VISIBLE
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                Log.i("Volley_Error", error.toString())
               progress!!.visibility = View.GONE
                signUp!!.visibility = View.VISIBLE
            }) {
            @Throws(AuthFailureError::class)
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

                try {
                    //SecretKeySpec keys=encrypt.generateKey(Password);
                    //String key=keys.toString();
                    params["password"] = Password
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(DefaultRetryPolicy(80000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
    }

    fun snack_bar(error: String?, view: View) {
        val mysnackbar = Snackbar.make(view, error!!, Snackbar.LENGTH_LONG)
        mysnackbar.show()
    }

  /*  private fun get_crime_info_alert_dialog()
    {
        val builder = AlertDialog.Builder(this)
        // Get the layout inflater
        val inflater: LayoutInflater = LayoutInflater.from(this)
        var inflated=inflater.inflate(R.layout.association_rules_layout, null)
        builder.setView(inflated)


        builder.setPositiveButton("Okay",
            DialogInterface.OnClickListener { dialog, id ->
                var text_crime_description_radio=inflated.crime_description_edittext.text
                //location_description =inflated.location_description.text.toString()
                var checked_incident_type=inflated.incident_type_radio
               // crime_description.text=text_crime_description_radio
            })
        builder.setCancelable(true)



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Add action buttons

        builder.create()
        builder.show()
    }*/

}


