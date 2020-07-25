package com.example.nyumba10.Dashboard.MyAssociation.Association_search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nyumba10.R
import kotlinx.android.synthetic.main.association_add.*
import kotlinx.android.synthetic.main.association_layout.view.*


class Associations_search_adapter (val association_details:ArrayList<associations_data_class>,var c: Context):
    RecyclerView.Adapter<Associations_search_adapter.ViewHolder>()
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Associations_search_adapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.association_layout,parent,false)


   return  ViewHolder(view)
    }

    override fun getItemCount(): Int {
return  association_details.size
    }

    override fun onBindViewHolder(holder: Associations_search_adapter.ViewHolder, position: Int) {
val association_data: associations_data_class=association_details[position]

        holder?.itemview.association_name.text=association_data.association_name
        holder?.itemview.county.text=association_data.county
        holder?.itemview.sub_county.text=association_data.sub_county

        holder.itemview.setOnClickListener {
            association_search_recycler_onclick(it,position)
        }



    }

    class ViewHolder(val itemview: View):RecyclerView.ViewHolder(itemview)
    {}

    fun association_search_recycler_onclick(view: View,position: Int)
    {

        val association_data: associations_data_class=association_details[position]
        var association_name_from_search=association_data.association_name
        var latLng_list: String=association_data.association_polygon_list

      //  Toast.makeText(view.context,"$association_name_from_search",Toast.LENGTH_LONG).show()

        val activity = view.getContext() as AppCompatActivity
        if (view.context is Association_add) {
            (view.context as Association_add).polygon(view,latLng_list)
        }
        activity.counties_card_view.visibility=View.VISIBLE
        activity. associations_details_card.visibility=View.VISIBLE
        activity.searchview.visibility=View.GONE
        activity.recycler_view.visibility=View.GONE
        //activity.activitysearch_description_text.visibility=View.GONE
      // activity.association_name.text=association_data.association_name.toString()

        activity.Association_name_value.text=association_name_from_search.toString()
        activity.association_id.text=association_data.association_id



    }

}