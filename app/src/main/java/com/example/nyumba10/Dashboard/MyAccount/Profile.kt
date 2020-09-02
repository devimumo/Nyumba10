package com.example.nyumba10.Dashboard.MyAccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.login.DashBoard
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.emerge
import kotlinx.android.synthetic.main.activity_profile.gender
import kotlinx.android.synthetic.main.activity_profile.marital_status
import kotlinx.android.synthetic.main.activity_profile.no_of_children
import kotlinx.android.synthetic.main.activity_profile.occupation
import kotlinx.android.synthetic.main.activity_profile.primary_contact_id
import kotlinx.android.synthetic.main.activity_profile.primary_contact_name
import kotlinx.android.synthetic.main.activity_profile.primary_contact_relationship
import kotlinx.android.synthetic.main.activity_profile.progress
import kotlinx.android.synthetic.main.activity_profile.update_profile
import kotlinx.android.synthetic.main.activity_profile.wife_id_no
import kotlinx.android.synthetic.main.profile.*
import kotlinx.android.synthetic.main.profile.female
import kotlinx.android.synthetic.main.profile.male
import kotlinx.android.synthetic.main.profile.no
import kotlinx.android.synthetic.main.profile.view.*
import kotlinx.android.synthetic.main.profile.yes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject





var occupations_value=""
var gender_value=""
var designation_value=""
var marital_status_value=""
var wife_id_passport_no_value=""
var no_of_children_value=""
var name_of_primary_emergency_contact_value=""
var id_of_primary_emergency_contact_value=""
var relationship_of_primary_emergency_contact_value=""

var marital_status_value_edit_value=""
var gender_value_edit_value=""

var data_payload=""

class Profile : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.profile, container, false)

               view.activity_progress_bar.visibility=View.VISIBLE
                get_profile_details(view)

        //on click event handler for edit user profile
        view.edit_profile.setOnClickListener {
            view.profile_textview_layout.visibility=View.GONE
            view.profile_edittext_layout.visibility=View.VISIBLE

            set_profile_details_to_edittexts(view)
        }


        view.gender_radio.setOnCheckedChangeListener { group, checkedId ->
            onRadioButton_gender_Clicked(checkedId)

        }

        view.primary_contact_details_opener.setOnClickListener {

            if (view.primary_details_holder.visibility==View.VISIBLE)
            {
                view.primary_details_holder.visibility=View.GONE
            }
            else
            {
                view.primary_details_holder.visibility=View.VISIBLE

            }
        }

view.marraige_status_radio.setOnCheckedChangeListener { group, checkedId ->

    onRadioButton_marrital_status_Clicked(checkedId)
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

    private fun set_profile_details_to_edittexts(view: View?) {

        view?.firstname_value_inedittext?.text=view?.firstname_value?.text
        view?.id_no_value_inedittext?.text=view?.id_no_value?.text
        view?.occupation?.setText(occupations_value)
        view?.no_of_children?.setText(no_of_children_value)
        view?.primary_contact_name?.setText(name_of_primary_emergency_contact_value)
        view?.primary_contact_id?.setText(id_of_primary_emergency_contact_value)
        view?.primary_contact_relationship?.setText(relationship_of_primary_emergency_contact_value)


        if (gender_value.equals("male"))
        {
            view?.male?.isChecked=true
        }
        else{

            view?.female?.isChecked=true
        }

        if (marital_status_value.equals("yes"))
        {
view?.yes?.isChecked=true        }
        else{
view?.no?.isChecked=true      }

    }

    private fun get_profile_details(view: View) {
        val MyPreferences="mypref"
        val sharedPreferences =view?.context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        var id_no=sharedPreferences?.getString("id_no","")
        val queue = Volley.newRequestQueue(view.context)

        Log.d("profile_details",id_no!!)
        val url="https://daudi.azurewebsites.net/nyumbakumi/profile/get_profile_data.php"+"?id_no="+id_no;

// Request a string response from the provided URL.
        val stringRequest = StringRequest( Request.Method.GET, url,
            Response.Listener<String> { response ->
var success_state=JSONObject(response)
                var success=success_state.getString("response")

                when(success)
{
    "successful"->{
        data_payload=success_state.getString("data")
        handle_profile_data_response(data_payload)

    }
    "!data"->{
        this.progress!!.visibility = View.GONE

        snack_bar("Please update your profile details",view)

    }
    else->{

        Log.d("db_errors",data_payload)
        this.progress!!.visibility = View.GONE


    }
}
                Log.d("profile_response",response)
                // Display the first 500 characters of the response string.
            },
            Response.ErrorListener {
                val err= Volley_ErrorListener_Handler()

                err.check_error(it,view)
            })

// Add the request to the RequestQueue.
        queue.add(stringRequest)

    }

    private fun handle_profile_data_response(dataPayload: String) {

        var data_array=JSONArray(dataPayload)
        var data_json_object=data_array.getJSONObject(0)


         occupations_value=data_json_object.getString("occupation")
         gender_value=data_json_object.getString("gender")
        designation_value=data_json_object.getString("designation")
        marital_status_value=data_json_object.getString("marital_status")
        wife_id_passport_no_value=data_json_object.getString("wife_id_passport_no")
        no_of_children_value=data_json_object.getString("no_of_children")
        name_of_primary_emergency_contact_value=data_json_object.getString("name_of_primary_emergency_contact")
        id_of_primary_emergency_contact_value=data_json_object.getString("id_of_primary_emergency_contact")
        relationship_of_primary_emergency_contact_value=data_json_object.getString("relationship_of_primary_emergency_contact")

        val MyPreferences = "mypref"
        val sharedPreferences = activity?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val lastname = sharedPreferences?.getString("lastname", "")

        val firstname = sharedPreferences?.getString("firstname", "")+"  "+lastname
        val id_no = sharedPreferences?.getString("id_no", "")


        view?.firstname_value?.text=firstname
        view?.id_no_value?.text=id_no
        view?.firstname_value?.text=firstname


        view?.occupation_textview?.text="Occupation : "+ occupations_value
        view?.id_no_value?.text="Id no: "+id_no
        view?.gender_textview?.text="Gender : "+ gender_value
        view?.marrital_status_textview?.text="Married ? : "+ marital_status_value
        view?.wife_id_no_textview?.text="Wife Id no : "+ wife_id_passport_no_value
        view?.no_of_children_textview?.text="No of children : "+ no_of_children_value
        view?.primary_contact_name_textview?.text="Name  : "+ name_of_primary_emergency_contact_value
        view?.primary_contact_id_textview?.text="Id no : "+ id_of_primary_emergency_contact_value
        view?.primary_contact_relationship_textview?.text="Relationship ? : "+ relationship_of_primary_emergency_contact_value



        view?.activity_progress_bar?.visibility=View.GONE
        view?.profile_textview_layout?.visibility=View.VISIBLE

    }

    fun onBackPressed(): Boolean {

         var boolean_value=false

        var profile_textview_layout_state= this.profile_textview_layout.visibility

        if(profile_textview_layout_state==View.VISIBLE)
        {

        boolean_value=false
        }
        else
        {
           this.profile_textview_layout.visibility=View.VISIBLE
            this.profile_edittext_layout.visibility=View.GONE

            boolean_value=true

        }
        return boolean_value
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

        val occupation_value =view.occupation.text.toString()
        val gender_value = view.gender.getText().toString()
        val marital_status_value = view.marital_status.getText().toString()
        val wife_id_no_value = view.wife_id_no.getText().toString()
        val no_of_children_value = view.no_of_children.getText().toString()
        
        val url =            "https://daudi.azurewebsites.net/nyumbakumi/login/profile.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
                Log.i("Responsed", response)
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(response)
                    val responses = jsonObject.getString("response")
                    when (responses) {
                        "successful" -> {
                            Toast.makeText(context, "Profile Updated successfully", Toast.LENGTH_LONG).show()
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
                            Toast.makeText(context," unsuccessful. Try again", Toast.LENGTH_LONG).show()
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
            }, Response.ErrorListener { error ->
                Log.i("Volley_Error", error.toString())
                this.progress.visibility = View.INVISIBLE
                this.update_profile.visibility = View.VISIBLE
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =HashMap()

                val MyPreferences = "mypref"
                val sharedPreferences =view?.context?.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                // String session_id= sharedPreferences.getString("sessions_ids","");

                val id_no = sharedPreferences?.getString("id_no", "")
                Log.d("id_number",id_no!!)

                params["id_no"] = id_no.toString()
                params["occupation"] = occupation
                params["gender"] = gender_value_edit_value
                params["marital_status"] = marital_status_value_edit_value
                params["wife_id_no"] = wife_id_no
                params["no_of_children"] = no_of_children
                params["name_of_primary_emergency_contact"] = primary_contact_name.text.toString()
                params["id_of_primary_emergency_contact"] = primary_contact_id.text.toString()
                params["relationship_of_primary_emergency_contact"] = primary_contact_relationship.text.toString()
                params["emergency_contact_phone_number"] = view.primary_contact_phonenumber.text.toString()

                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    fun onRadioButton_gender_Clicked(id: Int) {
            // Is the button now checked?

            // Check which radio button was clicked
            when (id) {
                R.id.male ->{
                    gender_value_edit_value="male"
                }

                R.id.female -> {

                    gender_value_edit_value="female"

                }
            }
    }

    fun onRadioButton_marrital_status_Clicked(state: Int) {
            // Is the button now checked?
         when (state) {
                R.id.yes -> {
                    marital_status_value_edit_value="yes"
                }
                R.id.no -> {
                    marital_status_value_edit_value="no"

                }
            }
    }

    fun profile_onclick_event(views: View) {}

    private fun snack_bar(message: String,view: View)
    {
        var snack=Snackbar.make(view,message,Snackbar.LENGTH_LONG)
        snack.show()
    }

}

