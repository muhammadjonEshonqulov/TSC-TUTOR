package uz.jbnuu.tsc.adapters

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.databinding.ItemGroupBinding
import uz.jbnuu.tsc.model.group.GroupData
import uz.jbnuu.tsc.utils.MyDiffUtil


class GroupAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<GroupAdapter.MyViewHolder>() {

    var dataProduct = emptyList<GroupData>()
    var next: Int? = null

    fun setData(newData: List<GroupData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: GroupData, view: ImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemGroupBinding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GroupData) {
            binding.nameGroup.text = "" + data.name
            binding.groupImage.transitionName = "ImageGroup$bindingAdapterPosition"

            binding.itemBack.setOnClickListener {
                listener.onItemClick(data, binding.groupImage)
            }
        }
    }
}
