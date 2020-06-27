package com.example.nyumba10.Dashboard.MyAssociation.Association_search

import android.R.attr.data
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nyumba10.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.association_add.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


val statement_data = ArrayList<associations_data_class>()
var list = ArrayList<String>()
var sub_counties_list = ArrayList<String>()

class Association_add : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.association_add)



        sub_county_value.setOnClickListener {
            sub_counties_alert(it)
        }
               counties_alert.setOnClickListener {
            counties_alert(it)
        }

       list= get_counties("county")
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView: SearchView =searchview as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                fetch_associations_volley(query)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                statement_data.clear()
                if (newText.isEmpty())
                {recycler_view.visibility=View.GONE}
                else
                {

                    fetch_associations_volley(newText)

                }

                return false
            }
        })

    }


    fun counties_alert(view: View)
    {

        val MyPreferences = "mypref"
        val sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
       // var vv= sharedPreferences.getString("sessions_ids","");


        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle(R.string.title_activity_admin).setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
            ,DialogInterface.OnClickListener { dialog, which ->
var chosen_county= list.get(which)
                Toast.makeText(this,chosen_county,Toast.LENGTH_LONG).show()
                county_value.setText(chosen_county)


                var sub_v: String="sub_county"

                sub_counties_list=   get_sub_counties(chosen_county)
                Log.d("sub_counties_list", sub_counties_list.toString())

                // The 'which' argument contains the index position
                    // of the selected item
                })

        val alert=builder.create()
        alert.show()
    }

    fun sub_counties_alert(view: View)
    {

        val MyPreferences = "mypref"
        val sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // var vv= sharedPreferences.getString("sessions_ids","");


        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle(R.string.title_activity_admin).setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, sub_counties_list)
            ,DialogInterface.OnClickListener { dialog, which ->
                var chosen_county= sub_counties_list.get(which)
                Toast.makeText(this,chosen_county,Toast.LENGTH_LONG).show()
                sub_county_value.setText(chosen_county)


                var sub_v: String="sub_county"


                // The 'which' argument contains the index position
                // of the selected item
            })

        val alert=builder.create()
        alert.show()
    }
    private fun get_counties(value: String): ArrayList<String> {


        val url =
            "https://daudi.azurewebsites.net/nyumbakumi/my_associations/get_counties.php"+
                    "?value="+value
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->

                if (response.equals("unsuccessful")) {
                    Log.d("counties_data", response)
                }
                else {
                    Log.d("counties_data", response)


                    var jsonObject = JSONObject(response)
                    var jsonarray = jsonObject.getJSONArray("counties_list")


                    for (i in 0 until jsonarray.length()) {
                        list.add(jsonarray.getJSONObject(i).getString("name"))
                    }

                    val MyPreferences = "mypref"
                    var sharedPreferences =
                        getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                    // String session_ide= sharedPreferences.getString("sessions_ids","");
                    val editor = sharedPreferences.edit()


                    // String phone_number_= phone_number.getText().toString().trim();
                    editor.remove("counties_json")
                    editor.putString("counties_json", response)
                    // editor.putString("phone_numbers",phone_number_);
                    editor.apply()
                }


            }, Response.ErrorListener {
                Log.i("Volley_Error", it.toString())

            }) {}

        val requestQueue = Volley.newRequestQueue(this)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                80000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
    
    return list
    }


    private fun get_sub_counties(value: String): ArrayList<String> {


        val url =
            "https://daudi.azurewebsites.net/nyumbakumi/my_associations/get_counties.php"+
                    "?value="+value
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->

                if (response.equals("unsuccessful")) {
                    Log.d("sub_counties_data", response)
                }
                else {
                    Log.d("sub_counties_data", response)


                  //  var jsonObject = JSONObject(response)
                 //   var jsonarray = jsonObject.getJSONArray("sub_counties_list")
                   // Log.d("sub_counties_data", jsonarray.toString())

var dd="{\"counties_list\":[" +response+ "]}"
                    Log.d("ggy",dd)

                     var mdd=dd.replace("\\","")
                  //  var jsonarray=JSONArray(mdd)

                    //  Log.d("ggy",mdd)

                  /*  val vvvv = Gson()

                    var message_payload=vvvv.toJson(response).toString()
Log.d("ggy",message_payload)*/
              val jsonObject=JSONObject(mdd)
                    var jsonarray = jsonObject.getJSONArray("counties_list")
sub_counties_list.clear()

                    for (i in 0 until jsonarray.length()) {
Toast.makeText(this,jsonarray.length().toString(),Toast.LENGTH_LONG).show()
                      //  for (i in 0 until jsonarray.length()) {
                        sub_counties_list.add(jsonarray[i].toString())
                    }

                    val MyPreferences = "mypref"
                    var sharedPreferences =
                        getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
                    // String session_ide= sharedPreferences.getString("sessions_ids","");
                    val editor = sharedPreferences.edit()


                    // String phone_number_= phone_number.getText().toString().trim();
                    editor.remove("counties_json")
                    editor.putString("counties_json", response)
                    // editor.putString("phone_numbers",phone_number_);
                    editor.apply()
                }


            }, Response.ErrorListener {
                Log.i("Volley_Error", it.toString())

            }) {}

        val requestQueue = Volley.newRequestQueue(this)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                80000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        return sub_counties_list
    }


    private fun fetch_associations_volley(query: String) {

        Log.d("query_value",query)
if (query.isEmpty())
{
    statement_data.clear()
}
        val url =
            "https://daudi.azurewebsites.net/nyumbakumi/my_associations/association_search.php" +
                    "?query="+query
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,url,
            Response.Listener { response ->
                Log.i("Responsed", response)
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(response)
                 //   val responses = jsonObject.getString("response")
                    when (response) {
                        "unsuccessful" -> {
                            Toast.makeText(applicationContext,"Unsuccessful",Toast.LENGTH_LONG).show()


                            progress!!.visibility = View.GONE

                        }
                      //
                        else -> {
                            val data = jsonObject.getJSONArray("association_list")
                            if (data.length()==0){

                                recycler_view.visibility=View.GONE
                                 statement_data.clear()
}
                            else
{
                            recycler_view.visibility=View.VISIBLE
}
                            for (i in 0..data.length() - 1) {

                                val association_data = data.getJSONObject(i)
                                val association_data_array = associations_data_class(
                                    association_data.getString("association_name"),
                                    association_data.getString("county"),
                                    association_data.getString("sub_county")
                                )

                                val adap = Associations_search_adapter(statement_data, this)



                                statement_data.add(association_data_array)

                                Log.d("association_data",statement_data.toString())

                                recycler_view.layoutManager = LinearLayoutManager(this)

                                adap.notifyDataSetChanged()
                                recycler_view.adapter = adap
                            }
                            //progressbar!!.visibility = View.INVISIBLE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.i("Volley_Error", error.toString())
                //  progressbar!!.visibility = View.INVISIBLE
            }) {

        }
        val requestQueue = Volley.newRequestQueue(this)



        requestQueue.add(stringRequest)
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                80000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        )
    }

    fun association_add_activity_click(view: View) {

        when(view.id){

            R.id.association_name_card->
            {

                counties_card_view.visibility=View.GONE
                associations_details_card.visibility=View.GONE
                searchview.visibility=View.VISIBLE
                search_description_text.visibility=View.VISIBLE
                recycler_view.visibility=View.VISIBLE
            }
            R.id.residense_type_card->
            {
                Toast.makeText(this,"iko hapa",Toast.LENGTH_LONG).show()
            }
        }

    }
}


