package com.example.nyumba10.Dashboard.Stats

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nyumba10.Dashboard.Get_crime_data.get_crime_data
import com.example.nyumba10.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.reportcrime.incident_date
import kotlinx.android.synthetic.main.reportcrime.incident_time
import kotlinx.android.synthetic.main.statistics_layout.*
import kotlinx.android.synthetic.main.statistics_layout.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private  var get_crime_data_instanse=get_crime_data()
private  var incident_type_value_from_radio: String="all"
private var crime_data_response_from_network: String=""
private var rootView_statistics: View? = null
private var from_time_value: String=""
private var to_time_value: String=""
private var crime_time_and_date_valuess: String=""
private var labels = ArrayList<String>()

private var crime_time_and_date_value = ""
private var crime_time_and_date_value_now= ""
private var raw_dateFormat_new_string=""
private var crime_count=0
private var suspicious_count=0
private var safety_count=0

private var crime_bar= ArrayList<BarEntry>()
private var suspicious_bar_= ArrayList<BarEntry>()
private var safety_bar= ArrayList<BarEntry>()

private var crime_time_value = ""
private var crime_time_value_now = ""

private var crime_date_value = ""
private var crime_date_value_now = ""

private var crime_time_text_value = ""
private var crime_time_text_value_now = ""

private var crime_date_text_value = ""
private var crime_date_text_value_now = ""


class Statistics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_layout)

        rootView_statistics = window.decorView.rootView

        set_default_time_values()
        val action=supportActionBar
        action?.setTitle("Statistics")
        incident_type_radio_group.setOnCheckedChangeListener { radioGroup, index ->

            crime_radio_group(index)
        }
                 get_crime_data()



        query_crime_data.setOnClickListener {

            get_crime_data()
        }

    }

    private fun setBarChart(
        entries: ArrayList<BarEntry>,
        entries2: ArrayList<BarEntry>,
        entries3: ArrayList<BarEntry>,
        view: View,labels: ArrayList<String>
    ) {

        Log.d("entries",entries.toString())
       /* val entries = ArrayList<BarEntry>()

        entries.add(BarEntry(0f, 6f))
        entries.add(BarEntry(1f, 2f))
        entries.add(BarEntry(2f, 5.5f))
        entries.add(BarEntry(3f, 4f))
        entries.add(BarEntry(4f, 3f))
        entries.add(BarEntry(5f, 6f))

        val entries2 = ArrayList<BarEntry>()
        entries2.add(BarEntry(0f, 1f))
        entries2.add(BarEntry(1f, 4f))
        entries2.add(BarEntry(2f, 2f))
           entries2.add(BarEntry(3f, 7f))
        entries2.add(BarEntry(4f, 2f))
        entries2.add(BarEntry(5f, 4f))


        val entries3 = ArrayList<BarEntry>()
        entries3.add(BarEntry(0f, 1f))
        entries3.add(BarEntry(1f, 4f))
        entries3.add(BarEntry(2f, 2f))
       entries3.add(BarEntry(3f, 2f))
        entries3.add(BarEntry(4f, 1f))
        entries3.add(BarEntry(5f, 0f))

        val entries4 = ArrayList<BarEntry>()
        entries4.add(BarEntry(0f, 5f))
        entries4.add(BarEntry(1f, 4f))
        entries4.add(BarEntry(2f, 5f))
        entries4.add(BarEntry(3f, 7f))
        entries4.add(BarEntry(4f, 4f))
        entries4.add(BarEntry(5f, 1f))*/

var dataset_array=ArrayList<BarDataSet>()
        val barDataSet = BarDataSet(entries, "Crime")
        barDataSet.setColor(Color.RED)

        val barDataSet2 = BarDataSet(entries2, "Safety")
        barDataSet2.setColor(Color.parseColor("#FF9800"))

        val barDataSet3 = BarDataSet(entries3, "Suspicious")
        barDataSet3.setColor(Color.parseColor("#098C0F"))


        //val barDataSet4 = BarDataSet(entries4, "bbs")
      //  barDataSet4.setColor(Color.BLACK)

        dataset_array.add(barDataSet)
        dataset_array.add(barDataSet2)
        dataset_array.add(barDataSet3)
       // dataset_array.add(barDataSet4)




        var xAxis: XAxis? = view.barChart.xAxis



     //   xAxis?.valueFormatter=labels;
        xAxis?.setCenterAxisLabels(true);
        xAxis?.position = XAxis.XAxisPosition.BOTTOM;
        xAxis?.granularity = 0.1f;
        xAxis?.isGranularityEnabled = true;
        xAxis?.labelCount=labels.size


             //  val xAxisFormatter: LabelFormatter = LabelFormatter(barChart, labels)
       xAxis?.valueFormatter = object :IndexAxisValueFormatter(labels){

           override fun getFormattedValue(value: Float): String {
               return super.getFormattedValue(value)
           }
       }


  //xAxis?.valueFormatter=ValueFormatter(LabelFormatter(labels))
        val data = BarData( dataset_array as List<IBarDataSet>?)
        // val data: BarData= BarData(barDataSet,barDataSet)

        //  barChart.setVisibleXRangeMaximum(6f)
        var bar_space=0.02f
        var groupspace=0.5f
        val barWidth = 0.15f // x2 dataset

        data.barWidth=barWidth
        view.barChart.data = data // set the data and list of lables into chart

       view.barChart.xAxis.axisMinimum=0f
        view.barChart.xAxis.axisMaximum=(0+view.barChart.barData.getGroupWidth(groupspace,bar_space)*entries.size)
        view.barChart.axisLeft.axisMinimum=0f


      //  barChart.setVisibleXRangeMaximum(4f); // allow 5 values to be displayed
       // barChart.moveViewToX(1f);//

        view.barChart.groupBars(0f,groupspace,bar_space)
        view.barChart.invalidate()
//barChart.description=
       // barChart.setDescription("Set Bar Chart Description")  // set the description
view.barChart.description.text="Crime info statistics"
        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        //barDataSet.color = resources.getColor(R.color.colorAccent)

        view.barChart.animateY(2500)
    }

    private fun crime_radio_group(index: Int) {

        when (index) {
            0 -> {

                incident_type_value_from_radio ="crime"

            }
            1 -> {
                incident_type_value_from_radio ="suspicious"
            }
            2 -> {
                incident_type_value_from_radio ="safety"

            }

            else ->{
                incident_type_value_from_radio ="crime"

            }
        }
    }


    private fun date_picker()
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val time=c.get(Calendar.HOUR_OF_DAY)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in TextView
            var date_today = (monthOfYear+1).toString()

            if (monthOfYear<10)
            {
                date_today="0"+date_today
            //    crime_date_value = (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()
               //   time_picker(rootView_statistics!!)


                if (dayOfMonth<10)
                {
                    crime_date_value = (year.toString()+date_today.toString()+"0"+dayOfMonth.toString()).toString()
                    incident_date.setText("" + "0"+dayOfMonth + "/ " + date_today + "/ " + year)

                    time_picker(rootView_statistics!!)
                    Log.d("crime_date_value",crime_date_value)

                }
                else
                {

                    crime_date_value = (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()
                    incident_date.setText("" + dayOfMonth + "/ " + date_today + "/ " + year)

                    time_picker(rootView_statistics!!)
                    Log.d("crime_date_value",crime_date_value)

                }
            }
            else{
                if (dayOfMonth<10)
                {
                    crime_date_value = (year.toString()+date_today.toString()+"0"+dayOfMonth.toString()).toString()
                    time_picker(rootView_statistics!!)
                    Log.d("crime_date_value",crime_date_value)

                }
                else
                {
                    crime_date_value = (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()
                    time_picker(rootView_statistics!!)
                       Log.d("crime_date_value",crime_date_value)
                }
            }
         //   crime_time_and_date_value = crime_date_value.toString()+ crime_time_value
          //  Log.d("crime_time_and_datess", crime_time_and_date_value.toString())


        }, year, month, day-7)

        //calcuate in milliseconds the number of days-minimum days the date picke should show
        var mindate_value=(c.timeInMillis-(10*24*60*60*1000)).toString()

        dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpd.getDatePicker().setMinDate(mindate_value.toLong());

        dpd.show()
    }

   private fun date_picker_now()
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val time=c.get(Calendar.HOUR_OF_DAY)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in TextView
            var date_today = (monthOfYear+1).toString()

            if (monthOfYear<10)
            {
                date_today="0"+date_today
                incident_date.setText("" + dayOfMonth + ", " + date_today + ", " + year)

                // crime_date_value_now = (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()
               // time_picker_now(rootView_statistics!!)

                if (dayOfMonth<10)
                {
                    crime_date_value_now = (year.toString()+date_today.toString()+"0"+dayOfMonth.toString()).toString()
                    incident_date.setText("" +"0"+dayOfMonth + ", " + date_today + ", " + year)

                    time_picker_now(rootView_statistics!!)

                }
                else
                {
                    crime_date_value_now = (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()
                    incident_date.setText("" + dayOfMonth + ", " + date_today + ", " + year)

                    time_picker_now(rootView_statistics!!)

                }

            }
            else{
                if (dayOfMonth<10)
                {
                         crime_date_value_now = (year.toString()+date_today.toString()+"0"+dayOfMonth.toString()).toString()
                    time_picker_now(rootView_statistics!!)

                }
                else
                {
                      crime_date_value_now = (year.toString()+date_today.toString()+dayOfMonth.toString()).toString()
                    time_picker_now(rootView_statistics!!)

                }
            }
            //   crime_time_and_date_value = crime_date_value.toString()+ crime_time_value
            //  Log.d("crime_time_and_datess", crime_time_and_date_value.toString())

        }, year, month-6, day)

        //calcuate in milliseconds the number of days-minimum days the date picke should show
        var mindate_value=(c.timeInMillis-(10*24*60*60*1000)).toString()

        dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
       // dpd.getDatePicker().setMinDate(mindate_value.toLong());

        dpd.show()
    }

  private  fun time_picker(this_view: View)
    {
        val c = Calendar.getInstance()


        val time_pick= TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val currentTime_hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
            val currentTime_minute = SimpleDateFormat("mm", Locale.getDefault()).format(Date())

            c.set(Calendar.HOUR_OF_DAY,hourOfDay)
            c.set(Calendar.MINUTE,minute)
            view.setIs24HourView(true)

            if (currentTime_hour.toInt()<hourOfDay)
            {
                //  Toast.makeText(this,"Time cannot be beyond now! Try again",Toast.LENGTH_LONG).show()
                Snackbar.make(this_view, "Choose the correct time.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                time_picker(view)
            }
            else
            {
                if (currentTime_minute.toInt()<minute)
                {
                    //  Toast.makeText(this,"Time cannot be beyond now! Try again",Toast.LENGTH_LONG).show()
                    Snackbar.make(this_view, "Choose the correct time.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                    time_picker(view)
                }
                else
                {


                    incident_time.text= SimpleDateFormat("HH:mm:a").format(c.time)

                    var secs=10
                    //  crime_time_value =(hourOfDay.toString()+minute.toString()+secs.toString()).toString()
                   crime_time_and_date_value = crime_date_value.toString()+ SimpleDateFormat("HHmmss").format(c.time).toString()
                    Log.d("crime_time_date_values",crime_date_value+"\n time_value="+SimpleDateFormat("HHmmss").format(c.time).toString()+"\n"+ crime_time_and_date_value)

               //     Log.d("crime_time_and_date", crime_time_and_date_value)

                }
            }

        }

        //val milli=((hhd*3600)+52*60*)

        TimePickerDialog(this,time_pick,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show()


    }

    private fun time_picker_now(this_view: View)
    {
        val c = Calendar.getInstance()


        val time_pick= TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val currentTime_hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
            val currentTime_minute = SimpleDateFormat("mm", Locale.getDefault()).format(Date())

            c.set(Calendar.HOUR_OF_DAY,hourOfDay)
            c.set(Calendar.MINUTE,minute)
            view.setIs24HourView(true)

            if (currentTime_hour.toInt()<hourOfDay)
            {
                //  Toast.makeText(this,"Time cannot be beyond now! Try again",Toast.LENGTH_LONG).show()
                Snackbar.make(this_view, "Choose the correct time.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                time_picker(view)
            }
            else
            {
                if (currentTime_minute.toInt()<minute)
                {
                    //  Toast.makeText(this,"Time cannot be beyond now! Try again",Toast.LENGTH_LONG).show()
                    Snackbar.make(this_view, "Choose the correct time.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                    time_picker(view)
                }
                else
                {
                    incident_time_now.text= SimpleDateFormat("HH:mm:a").format(c.time)

                    var secs=10
                    //  crime_time_value =(hourOfDay.toString()+minute.toString()+secs.toString()).toString()
                    crime_time_and_date_value_now= crime_date_value_now.toString()+ SimpleDateFormat("HHmmss").format(c.time).toString()

                         Log.d("crime_time_and_date", crime_time_and_date_value_now)

                }
            }

        }

        //val milli=((hhd*3600)+52*60*)

        TimePickerDialog(this,time_pick,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show()


    }

    private fun set_default_time_values() {


        var dateFormat=SimpleDateFormat("yyyyMMdd")
        var dateFormat_new=SimpleDateFormat("dd/MM/yyyy")
        var raw_dateFormat_new=SimpleDateFormat("yyyyMMdd")


        var date_today=SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val myDate: Date = dateFormat.parse(date_today)

        val newDate = Date(myDate.getTime() - (5 * 24 * 60 * 60 * 1000)) // 5* 24 * 60 * 60 * 1000
        var date_5_days_back=dateFormat_new.format(newDate)

        crime_time_text_value = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date()).toString()
        crime_date_text_value =SimpleDateFormat(" dd /MM/ yyyy", Locale.getDefault()).format(Date()).toString()
        incident_time.text=crime_time_text_value
        incident_time_now.text= crime_time_text_value
        incident_date_now.text= crime_date_text_value
        incident_date.text=date_5_days_back

        crime_time_value = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date()).toString()
       crime_date_value = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toString()
        crime_date_value_now = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toString()

 raw_dateFormat_new_string=raw_dateFormat_new.format(newDate)
        crime_date_value=raw_dateFormat_new_string
        crime_time_and_date_value = raw_dateFormat_new_string+crime_time_value
        crime_time_and_date_value_now= crime_date_value_now+ crime_time_value
        Log.d("crime_time_and_date",crime_time_and_date_value+"now_time="+ crime_time_and_date_value_now)


    }

    fun click(view: View) {

        when(view.id){

            R.id.get_date->{
                date_picker()
            }

            R.id.get_date_now->{
                date_picker_now()
            }
            R.id.report->
            {
            }
        }


    }

   /* fun radioclicks(view: View) {
        if (view is RadioGroup) {
          //  val checked: Boolean = view.id
            when (view.id) {
                R.id.crime_radio -> {
                    if(checked)
                    {
                        incident_type_value_from_radio ="crime"
                    }

                }
                R.id.suspicious_radio -> {
                    if(checked)
                    {

                        incident_type_value_from_radio ="suspicious"

                    }

                }
                R.id.safety_radio -> {
                    if(checked)
                    {

                        incident_type_value_from_radio ="safety"

                    }

                }


            }
        }


    }
*/
    fun radioclick(view: View) {

    }

  /*  private void timePicker(final String date){
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        RangeTimePickerDialog timePickerDialog = new RangeTimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = getResources().getString(R.string.time_format);
                String formatTime = String.format(time, hourOfDay, minute);
                String dateTime = date + "   " + formatTime;
                tvSelectDate.setText(dateTime);
            }
        }, hour + 1, minute, false);
        timePickerDialog.setMin(hour + 1, minute);
        timePickerDialog.show();
    }*/

    private fun get_crime_data()
    {

        if (from_time_value > to_time_value)
        {
         snack_bar("choose correct from duration", rootView_statistics!!)
        }
        else
        {
            get_crime_data_instanse.get_crime_data(raw_dateFormat_new_string, crime_date_value_now,
                incident_type_value_from_radio,this, rootView_statistics!!)
/*
            Log.d("set_crime_incidena", crime_data_response_from_network.toString())

            when(crime_data_response_from_network)
            {
                "!data"->{
                    snack_bar("No data", rootView_statistics!!)
                }
                "error"->{
                    snack_bar("No connection", rootView_statistics!!)

                }
                else->
                {
            //   iterate_crime_data(crime_data_response_from_network, crime_time_and_date_value, crime_time_and_date_value_now)
                }
            }*/
        }
    }

      fun iterate_crime_data(crime_dataa: String, view: View)
    {

        Log.d("set_crime_incidena", crime_dataa.toString().toString())

      //  var from=Integer.parseInt(from_what_time);
        var from: Long = crime_date_value.toLong()

            //  var from=from_what_time.toInt();

        //var to=Integer.parseInt(to_which_time);
      //  var to=to_which_time.toInt();

        val to: Long = crime_date_value_now.toLong()
crime_bar.clear()
        suspicious_bar_.clear()
        safety_bar.clear()
        var xaixs_value=0

        for (x in from .. to)
        {

//var from_new=from.toInt()

            //  save_crime_data_JSON(crime_data) set to viewmodel
           val crime_data_jsonobject= JSONObject(crime_dataa)

            var crime_data_array=crime_data_jsonobject.getJSONArray("crime_data")

                      if ((crime_data_array.length()>0))
            {

                for (i in 0..crime_data_array.length()-1) {



                    var crime_data_array_jsonobjects=crime_data_array.getJSONObject(i)
                    var id_no=crime_data_array_jsonobjects.getString("id_no")
                    var mobile_no=crime_data_array_jsonobjects.getString("mobile_no")
                     crime_time_and_date_valuess=crime_data_array_jsonobjects.getString("crime_time_and_date_value")
                    crime_time_and_date_valuess=crime_time_and_date_valuess.replace("\"","")
                    var incident_type=crime_data_array_jsonobjects.getString("incident_type")
                    var marker_tag=crime_data_array_jsonobjects.getString("marker_tag")

                    var listLatLng_todb=crime_data_array_jsonobjects.getString("listLatLng_todb")
                    var  listLatLng_todb_array= JSONArray(listLatLng_todb)

                    var crime_description=crime_data_array_jsonobjects.getString("crime_description")

                    if ((from.toString()).equals(change_time_format(crime_time_and_date_valuess)))
                  {


                          when(incident_type)
                      {
                          "crime"->{
                              crime_count++
                          }
                          "suspicious"->{
                              suspicious_count++
                          }
                          "safety"->{
                              safety_count++
                          }
                      }

                       }

                }
            }

            crime_bar.add(BarEntry(xaixs_value.toFloat(),crime_count.toFloat()))
            suspicious_bar_.add(BarEntry(xaixs_value.toFloat(),suspicious_count.toFloat()))
            safety_bar.add(BarEntry(xaixs_value.toFloat(),safety_count.toFloat()))

var label_value=change_time_format_one(from.toString())
           // Log.d("lat_looong",xaixs_value.toString()+"from"+from.toString()+"--"+change_time_format(crime_time_and_date_valuess).toString()+"--"+change_time_format_one(from.toString())+"crime_array="+crime_data_array.length())


            labels.add(label_value)
          /*  labels.add("Feb")
            labels.add("Mar")
            labels.add("Apr")
            labels.add("may")
            labels.add("Jun")*/


            crime_count=0
            suspicious_count=0
            safety_count=0
            from++
            xaixs_value++


        }

        setBarChart(crime_bar,suspicious_bar_,safety_bar,view, labels)


    }


    private fun change_time_format_one(date_given: String): String
    {
        var changed_time_format=""

        try {

            var simpleDateFormat=SimpleDateFormat("yyyyMMdd")
            var new_format=SimpleDateFormat("EEE," +System.getProperty("line.separator")+
                    " MMM d,\n ''yyyy ")
            changed_time_format=new_format.format(simpleDateFormat.parse(date_given))
        }catch (e: Exception)
        {

        }

        return  changed_time_format

    }
    private fun change_time_format(date_given: String): String
    {
        var changed_time_format=""

        try {

          var simpleDateFormat=SimpleDateFormat("yyyyMMddHHmmss")
            var new_format=SimpleDateFormat("yyyyMMdd")
           changed_time_format=new_format.format(simpleDateFormat.parse(date_given))
      }catch (e: Exception)
      {

      }

        return  changed_time_format

    }

    fun snack_bar(string: String?, view: View) {
        val mysnackbar = Snackbar.make(view, string!!, Snackbar.LENGTH_LONG)
        mysnackbar.show()
    }

}