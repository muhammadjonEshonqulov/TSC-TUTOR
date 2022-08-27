package uz.jbnuu.tsc.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.databinding.ItemLocationHistoryBinding
import uz.jbnuu.tsc.model.history_location.LocationHistoryData
import uz.jbnuu.tsc.utils.MyDiffUtil


class LocationHistoryAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<LocationHistoryAdapter.MyViewHolder>() {

    var dataProduct = emptyList<LocationHistoryData>()
    var next: Int? = null

    fun setData(newData: List<LocationHistoryData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: LocationHistoryData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemLocationHistoryBinding = ItemLocationHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemLocationHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: LocationHistoryData) {
            binding.date.text = data.data_time
            binding.latLang.text = data.lat + " " + data.long

            binding.itemBack.setOnClickListener {
                listener.onItemClick(data)
            }
        }
    }
}
