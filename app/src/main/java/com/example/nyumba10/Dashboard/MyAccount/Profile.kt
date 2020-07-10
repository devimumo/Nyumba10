package com.example.nyumba10.Dashboard.MyAccount

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.R
import com.example.nyumba10.login.DashBoard
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.profile.view.*
import org.json.JSONException
import org.json.JSONObject

class Profile : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.profile, container, false)




        //on click event handler for edit user profile
        view.edit_profile.setOnClickListener {
            view.profile_textview_layout.visibility=View.GONE
            view.profile_edittext_layout.visibility=View.VISIBLE


        }



        //on click event handler for updating the profile details
        view.update_profile.setOnClickListener(View.OnClickListener {

            this.progress!!.visibility = View.VISIBLE
            this.update_profile.visibility=View.GONE
            update_profile(view)

            // Intent toLogin = new Intent(update_profile.this, view.LoginScreen.class);
            // startActivity(toLogin);
        })


        //on click event handler for showing emergency contacts layout
        view.emergency_drop.setOnClickListener {

            if (emerge.visibility==View.VISIBLE)
            {

                emerge.visibility=View.GONE

            }
            else
            {
                emerge.visibility=View.VISIBLE


            }

        }

        view.emergency_drop_textview.setOnClickListener {

            if (emerge.visibility==View.VISIBLE)
            {

                emerge.visibility=View.GONE

            }
            else
            {
                emerge.visibility=View.VISIBLE


            }

        }


        return  view
    }

    fun update_profile(view: View) {


val context=view.context
        val occupation_value = occupation.getText().toString()
        val gender_value = gender.getText().toString()
        val marital_status_value = marital_status.getText().toString()
        val wife_id_no_value = wife_id_no.getText().toString()
        val no_of_children_value = no_of_children.getText().toString()



        if (occupation_value.isEmpty() ) {
            Toast.makeText(context, "Names required", Toast.LENGTH_LONG).show()
            occupation.setError("all fields are required")
            this.progress.visibility = View.GONE
            this.update_profile.visibility = View.VISIBLE

            Log.d("profile_insert","occupation_value_error")

            update_profile!!.visibility = View.VISIBLE
        } else if (gender_value.isEmpty()) {

            Toast.makeText(context, "gender value?????", Toast.LENGTH_LONG).show()
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


            profile(occupation_value,gender_value,marital_status_value,wife_id_no_value,no_of_children_value,view)
        }
    }

    fun profile(occupation: String,gender: String,marital_status: String,wife_id_no: String,no_of_children: String,view: View) {


        val context=view.context

        //  String url="http://192.168.43.121/canary_camera/register.php";


        val occupation_value =view.occupation.text.toString()
        val gender_value = view.gender.getText().toString()
        val marital_status_value = view.marital_status.getText().toString()
        val wife_id_no_value = view.wife_id_no.getText().toString()
        val no_of_children_value = view.no_of_children.getText().toString()
        
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
                                context,
                                "Profile Updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent =
                                Intent(context, DashBoard::class.java)
                            startActivity(intent)
                            // String session_ide= sharedPreferences.getString("sessions_ids","");

                            val MyPreferences="mypref"
                            val sharedPreferences =view?.context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)

                            val editor = sharedPreferences?.edit()

                            //    editor.putString("phone_number",email);
                            editor?.putString("profile_status", "updated")

                            editor?.apply()


                        }

                        "unsuccessful" -> {
                            Toast.makeText(
                                context," unsuccessful. Try again", Toast.LENGTH_LONG).show()
                            this.progress.visibility = View.GONE
                            this.update_profile.visibility = View.VISIBLE
                        }
                        else -> {
                            Toast.makeText(
                                context,
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
                val sharedPreferences =view?.context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                // String session_id= sharedPreferences.getString("sessions_ids","");


                val id_no = sharedPreferences?.getString("id_no", "")
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
        val requestQueue = Volley.newRequestQueue(context)
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

    fun profile_onclick_event(views: View) {}


}

