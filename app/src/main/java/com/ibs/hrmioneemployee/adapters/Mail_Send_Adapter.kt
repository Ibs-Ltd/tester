package com.ibs.hrmioneemployee.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.databinding.SendMailRecyclerItemBinding

class Mail_Send_Adapter(val arrayList: ArrayList<String>, private val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<Mail_Send_Adapter.ViewHolder>(){

    class ViewHolder(val binding: SendMailRecyclerItemBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SendMailRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(arrayList[position]){
                val splitdata = this.split("/").toTypedArray()
                binding.filePath.text = splitdata.get(splitdata.size - 1)
                binding.close.setOnClickListener(){
                    onItemClickListener.OnItemClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }

}