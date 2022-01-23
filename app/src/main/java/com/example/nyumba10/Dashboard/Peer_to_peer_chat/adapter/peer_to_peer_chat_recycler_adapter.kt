package com.example.nyumba10.Dashboard.Peer_to_peer_chat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nyumba10.R
import com.example.nyumba10.dataclasses.association_chat_messages_dataclass
import kotlinx.android.synthetic.main.layout.view.*
import kotlinx.android.synthetic.main.layout_2.view.*
import java.util.ArrayList

class peer_to_peer_chat_recycler_adapter  (var chats: ArrayList<association_chat_messages_dataclass>, val c: Context,var view:View): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1)
        {
            val view= LayoutInflater.from(parent.context).inflate(R.layout.peer_to_peer_layout_my_message,parent,false)

            return peer_to_peer_chat_recycler_adapter.ViewHolder_one(view)

        }
        else
        {


            val view= LayoutInflater.from(parent.context).inflate(R.layout.peer_to_peer_other_party_message,parent,false)

            return peer_to_peer_chat_recycler_adapter.Guest_viewholder(view)

        }
    }

    override fun getItemCount(): Int {
return chats.size   }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val user_data: association_chat_messages_dataclass=chats[position]



        if (holder.itemViewType==2)
        {
            var holder_resident=holder as Guest_viewholder

            holder_resident?.itemview.message_2.text=user_data.messages
            holder_resident?.itemview.time_send_2.text=user_data.time
          //  holder_resident?.itemview.username_2.text=user_data.username
        }
        else
        {
            var holder_guest=holder as ViewHolder_one
            holder_guest?.itemview.message.text=user_data.messages
            holder_guest?.itemview.time_send.text=user_data.time
         //   holder_guest?.itemview.username_.text=user_data.username
        }


    }


    override fun getItemViewType(position: Int): Int {
        val user_data: association_chat_messages_dataclass=chats[position]
        val MyPreferences = "mypref"
        val sharedPreferences =view.context.
        getSharedPreferences(MyPreferences, Context.MODE_PRIVATE)
        // String session_id= sharedPreferences.getString("sessions_ids","");

        val id_no = sharedPreferences.getString("id_no", "")

        Log.d("holder",id_no.toString())
        if (user_data.from_id_no.equals(id_no))
        {
            return 1
        }
        else{
        return 2
    }
    }



    class ViewHolder_one(var itemview: View):RecyclerView.ViewHolder(itemview) {

    }

    class Guest_viewholder(var itemview: View) :RecyclerView.ViewHolder(itemview) {

    }
}