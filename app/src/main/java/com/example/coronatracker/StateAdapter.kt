package com.example.coronatracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.list_header.view.*

class StateAdapter(private val list: List<StatewiseItem?>):BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view=convertView?:LayoutInflater.from(parent?.context).inflate(R.layout.list_header,parent,false)
        val stateResult=list[position]

        view.confirmed.text=stateResult?.confirmed
        view.recovered.text=stateResult?.recovered
        view.death.text=stateResult?.deaths
        view.active.text=stateResult?.active
        view.state.text=stateResult?.state
        return view
    }

    override fun getItem(position: Int)= list.get(position)


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}