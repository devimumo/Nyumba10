package com.example.nyumba10.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.Helper_classes.Volley_ErrorListener_Handler
import com.example.nyumba10.R
import com.example.nyumba10.Security.Encrypt
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


private var attempts = 3

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R
            .layout.login)

        //login delay handler
        val  mVolHandler = Handler()
        val  mVolRunnable = Runnable {
            login_loginScreen.setVisibility(View.VISIBLE)
            login_progressBar.visibility=View.GONE
        }

        login_loginScreen.visibility=View.VISIBLE


        signUp_button.setOnClickListener {
            var intent=Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        login_loginScreen.setOnClickListener {

            login(this,user_name,passWord,login_progressBar,mVolHandler,mVolRunnable)

        }


        //login delay handler

    }

    fun login(
        view: Login,
        user_name: EditText,
        passWord: EditText,
        login_progressBar: ProgressBar,mVolHandler: Handler,mVolRunnable: Runnable
    )
    {
        var  username_value: String=user_name.text.toString()
        var  password: String=passWord.text.toString()

        // val username_value=username.text.toString()
        //  val password=passWord.text.toString()

        if (TextUtils.isEmpty(username_value))
        {
            user_name.setError("Username required ")
        }
        else if (username_value.length!=9)
        {

            user_name.setError("Wrong number format")
        }
        else if (TextUtils.isEmpty(password))
        {
            passWord.setError("password required ")


        }
        else
        {
            check_login(view,username_value,password,login_progressBar,user_name,mVolHandler,mVolRunnable)

        }

    }

    fun check_login(
        view: Login,
        username_value: String,
        password: String,
        login_progressBar: ProgressBar,
        user_name: EditText,mVolHandler: Handler,mVolRunnable: Runnable
    ) {
        login_progressBar.visibility= View.VISIBLE
        login_loginScreen.visibility= View.GONE
        val requestQueue = Volley.newRequestQueue(view)
val login_url="https://daudi.azurewebsites.net/nyumbakumi/login/login.php";
      //  val login_url =   "https://project-daudi.000webhostapp.com/ladies_group/login.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            login_url,
            Response.Listener { response ->
                Log.i("Response", response)
                try {
                    login_in_function(view,response,login_progressBar,user_name,mVolHandler,mVolRunnable)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    //   responses.equals(login_response);
                    Log.i("JSONEXCEPTION", e.toString())
                    login_progressBar.visibility= View.GONE
                    login_loginScreen.visibility= View.VISIBLE
                }
            },
            Response.ErrorListener {


                val err= Volley_ErrorListener_Handler()

                err.check_error(it,view)
                login_progressBar.visibility= View.GONE
                login_loginScreen.visibility= View.VISIBLE

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                val encrypt = Encrypt()
                var cc=view
                val MyPreferences = "mypref"
                val sharedPreferences =
                    getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                // String session_id= sharedPreferences.getString("sessions_ids","");


                val session_idss = sharedPreferences.getString("session_ids", "")


                params["session_ids"] = session_idss!!
                params["phone_number"] =  username_value
                try {
                  //  params["encrypted_password"] = encrypt.encrypt(password)
                    params["encrypted_password"] = password

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return params
            }
        }
        requestQueue.add(stringRequest)
    }


    @Throws(JSONException::class)
    private fun login_in_function(view: Login, response: String, login_progressBar: ProgressBar,
                                  user_name: EditText,mVolHandler: Handler,mVolRunnable: Runnable
    )
    {
        val jsonObject_response = JSONObject(response)
        val responses = jsonObject_response.getString("response")

        if (responses == "wrong_pass") {
            attempts--
            if (attempts < 3 && attempts != 0) {
                //bothe the username and password are false
                Toast.makeText(    view,"Wrong credentials.Try again", Toast.LENGTH_SHORT).show()
                login_progressBar.visibility= View.GONE
                login_loginScreen.visibility= View.VISIBLE

            } else { //both the username and password are false
                login_loginScreen.visibility=View.GONE
                mVolHandler.postDelayed(mVolRunnable, 60000);
                Toast.makeText(view, "Too many login attempts. Try again after 1 minute", Toast.LENGTH_LONG)
                    .show()
                //  finish()

            }
        } else if (responses == "!phone_number") {
            Toast.makeText(view, "Wrong credentials.Enter the correct username", Toast.LENGTH_LONG)
                .show()
            login_progressBar.visibility= View.GONE
            login_loginScreen.visibility= View.VISIBLE

        } else if (responses == "exists") {
            finish()
        } else {

            val jsonObject = JSONObject(response)
            val responsed = jsonObject.getString("response")
            val session_id = jsonObject.getString("session_id")


            if (responsed == "successful") {
                Toast.makeText(view, "successful", Toast.LENGTH_LONG).show()
                val MyPreferences = "mypref"
                var sharedPreferences =  getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                // String session_ide= sharedPreferences.getString("sessions_ids","");
                val editor = sharedPreferences.edit()


                // String phone_number_= phone_number.getText().toString().trim();
                val user_name: String = user_name.getText().toString()
                editor.remove("sessions_ids")
                editor.remove("phone_number")
                editor.putString("sessions_ids", session_id)
                editor.putString("phone_number", user_name)
                // editor.putString("phone_numbers",phone_number_);
                editor.apply()
                //  get_data(response)
                login_progressBar.visibility= View.GONE
                login_loginScreen.visibility= View.VISIBLE

                // String session_id= sharedPreferences.getString("sessions_ids","");


                val profile_status = sharedPreferences.getString("profile_status", "!updated")

                if (profile_status.equals("updated"))
                { val intent = Intent(this, DashBoard::class.java)
                    startActivity(intent)
                }
                else
                {

                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                }


            }
            ///////
        }
    }
}