package com.example.nyumba10.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.progress
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject

class Profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        update_profile.setOnClickListener(View.OnClickListener {
            this.progress!!.visibility = View.VISIBLE
            this.update_profile.visibility=View.GONE
            update_profile()

            // Intent toLogin = new Intent(update_profile.this, view.LoginScreen.class);
            // startActivity(toLogin);
        })

        emergency_drop.setOnClickListener {

if (emerge.visibility==View.VISIBLE)
{

    emerge.visibility=View.GONE

}
            else
{
    emerge.visibility=View.VISIBLE


}

        }
    }



    fun update_profile() {



        val occupation_value = occupation.getText().toString()
        val gender_value = gender.getText().toString()
        val marital_status_value = marital_status.getText().toString()
        val wife_id_no_value = wife_id_no.getText().toString()
        val no_of_children_value = no_of_children.getText().toString()


       
        if (occupation_value.isEmpty() ) {
            Toast.makeText(this, "Names required", Toast.LENGTH_LONG).show()
            occupation.setError("all fields are required")
            this.progress.visibility = View.GONE
            this.update_profile.visibility = View.VISIBLE

            Log.d("profile_insert","occupation_value_error")

            update_profile!!.visibility = View.VISIBLE
        } else if (gender_value.isEmpty()) {
            //  progressbar!!.visibility = View.INVISIBLE

            Toast.makeText(this, "gender value?????", Toast.LENGTH_LONG).show()
           Log.d("profile_insert","gender_value_error")
            gender.setError("all fields are required")
            this.progress.visibility = View.GONE
            this.update_profile.visibility = View.VISIBLE

        } else if (TextUtils.isEmpty(marital_status_value)) {
            marital_status.setError("Phone number  required ")
            Log.d("profile_insert","marital status value")

            this.progress.visibility = View.GONE
            this.update_profile.visibility = View.VISIBLE
        } else if (marital_status_value.equals("yes")) {

            if (wife_id_no_value.isEmpty())
            { wife_id_no.setError("all fields are required")
                Log.d("profile_insert","wife id value")

                this.progress.visibility = View.GONE
                this.update_profile.visibility = View.VISIBLE
            }

        } else if (TextUtils.isEmpty(no_of_children_value)) {
            no_of_children.setError("Password required ")
            Log.d("profile_insert","no of children")

            this.progress.visibility = View.GONE
            this.update_profile.visibility = View.VISIBLE
        }
        else {


            profile(occupation_value,gender_value,marital_status_value,wife_id_no_value,no_of_children_value)
        }
    }

     fun profile(occupation: String,gender: String,marital_status: String,wife_id_no: String,no_of_children: String) {

        //  String url="http://192.168.43.121/canary_camera/register.php";


        val url =
            "https://daudi.azurewebsites.net/nyumbakumi/login/profile.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                Log.i("Responsed", response)
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(response)
                    val responses = jsonObject.getString("response")
                    when (responses) {
                        "successful" -> {
                            Toast.makeText(
                                applicationContext,
                                "Profile Updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent =
                                Intent(applicationContext, DashBoard::class.java)
                            startActivity(intent)
                            // String session_ide= sharedPreferences.getString("sessions_ids","");

                            val MyPreferences="mypref"
                            val sharedPreferences =
                                getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                            val editor: Editor = sharedPreferences.edit()

                            //    editor.putString("phone_number",email);
                            editor.putString("profile_status", "updated")

                            editor.apply()


                        }

                        "unsuccessful" -> {
                            Toast.makeText(
                                applicationContext," unsuccessful. Try again",Toast.LENGTH_LONG).show()
                            this.progress.visibility = View.GONE
                            this.update_profile.visibility = View.VISIBLE
                        }
                        else -> {
                            Toast.makeText(
                                applicationContext,
                                "Try again$response",
                                Toast.LENGTH_LONG
                            ).show()
                            this.progress.visibility = View.GONE
                            this.update_profile.visibility = View.VISIBLE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.i("Volley_Error", error.toString())
                  this.progress.visibility = View.INVISIBLE
                this.update_profile.visibility = View.VISIBLE
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()

                val MyPreferences = "mypref"
                val sharedPreferences =
                    getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                // String session_id= sharedPreferences.getString("sessions_ids","");


                val id_no = sharedPreferences.getString("id_no", "")
                Log.d("id_number",id_no)

                params["id_no"] = id_no.toString()
                params["occupation"] = occupation
                params["gender"] = gender
                params["marital_status"] = marital_status
                params["wife_id_no"] = wife_id_no
                params["no_of_children"] = no_of_children
                params["name_of_primary_emergency_contact"] = primary_contact_name.text.toString()
                params["id_of_primary_emergency_contact"] = primary_contact_id.text.toString()
                params["relationship_of_primary_emergency_contact"] = primary_contact_relationship.text.toString()




                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


    fun onRadioButton_gender_Clicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.male ->
                    if (checked) {
                        gender.setText("male")
                    }
                R.id.female ->
                    if (checked) {
                        gender.setText("female")
                    }
            }
        }
    }

    fun onRadioButton_marrital_status_Clicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.yes ->
                    if (checked) {
                        marital_status.setText("yes")
                        wife_id_no.visibility=View.VISIBLE
                    }
                R.id.no ->
                    if (checked) {
                        marital_status.setText("no")
                        wife_id_no.visibility=View.GONE

                    }
            }
        }
    }

}
