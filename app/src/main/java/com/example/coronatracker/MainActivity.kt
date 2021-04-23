package com.example.coronatracker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var stateAdapter: StateAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(checkConnection()) {
            fetchResults()
        }
        else{
            val dialog=Dialog(this)
            dialog.setContentView(R.layout.alert_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    private fun fetchResults() {
        GlobalScope.launch {
            val response= withContext(Dispatchers.IO){Client.api.execute()}
            if (response.isSuccessful){
                val result=Gson().fromJson(response.body?.string(),Response::class.java)
                launch(Dispatchers.Main){
                    result.statewise[0]?.let { bindCombinedData(it) }
                    bindCombinedDataToList( result.statewise.subList(1,result.statewise.size))
                }
            }
        }
    }

    private fun bindCombinedDataToList(subList: List<StatewiseItem?>) {
        stateAdapter= StateAdapter(subList)
        list.adapter=stateAdapter
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun bindCombinedData(get: StatewiseItem) {
        val lastUpdatedTime = get.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastUpdatedTv.text = "Last Updated\n ${getTimeAgo(
            simpleDateFormat.parse(lastUpdatedTime)
        )}"
        confirmedTv.text = get.confirmed
        activeTv.text = get.active
        recoveredTv.text = get.recovered
        deceasedTv.text = get.deaths
    }

    fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }
    }

    private fun checkConnection():Boolean {
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if (BuildConfig.DEBUG && networkInfo == null) {
            return false
        }
        else{
            return networkInfo?.type==ConnectivityManager.TYPE_WIFI || networkInfo?.type==ConnectivityManager.TYPE_MOBILE
        }
    }

    fun closeApp(view: View) {
        recreate()
    }
}