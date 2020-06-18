package com.example.nyumba10.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.nyumba10.Dashboard.Admin.Admin
import com.example.nyumba10.Dashboard.Chat.Chat
import com.example.nyumba10.Dashboard.History.History
import com.example.nyumba10.Dashboard.MyAccount.MyAccount
import com.example.nyumba10.Dashboard.MyAssociation.MyAssociation
import com.example.nyumba10.Dashboard.Security.Security
import com.example.nyumba10.R

class DashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
    }

    fun Card_click(view: View) {


        when (view.id)
        {
            R.id.my_association->{


                val intent= Intent(this,MyAssociation::class.java)
                startActivity(intent)
            }

            R.id.sos->{
                //    val intent= Intent(this,::class.java)
                //  startActivity(intent)
            }

            R.id.my_account->{


                val intent= Intent(this,MyAccount::class.java)
                startActivity(intent)
            }

            R.id.report_crime->{


                val intent= Intent(this,MyAssociation::class.java)
                startActivity(intent)
            }

            R.id.chat->{


                val intent= Intent(this,Chat::class.java)
                startActivity(intent)
            }

            R.id.history->{


                val intent= Intent(this,History::class.java)
                startActivity(intent)
            }

            R.id.admin->{


                val intent= Intent(this,Admin::class.java)
                startActivity(intent)
            }

            R.id.security->{


                val intent= Intent(this,Security::class.java)
                startActivity(intent)
            }

        }

    }

}