package uz.jbnuu.tsc.tutor.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.tutor.databinding.ItemLocationHistoryBinding
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryData
import uz.jbnuu.tsc.tutor.utils.MyDiffUtil


class LocationHistoryAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<LocationHistoryAdapter.MyViewHolder>() {

    var dataProduct = ArrayList<LocationHistoryData>()
    var next: Int? = null

    fun setData(dataProduct: List<LocationHistoryData>) {
//        val diffUtil = MyDiffUtil(dataProduct, newData)
//        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
//        dataProduct = newData
//        diffUtilResult.dispatchUpdatesTo(this)

        var size = this.dataProduct.size
        this.dataProduct.addAll(dataProduct)
        var sizeNew = this.dataProduct.size
        notifyItemRangeChanged(size, sizeNew)
    }

    interface OnItemClickListener {
        fun onItemClick(data: LocationHistoryData, type: Int)
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
                listener.onItemClick(data, 1)
            }
            if (bindingAdapterPosition == dataProduct.size - 1) {
                listener.onItemClick(data, 2)
            }
        }
    }
}
