package uz.jbnuu.tsc.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.databinding.ItemSemesterBinding
import uz.jbnuu.tsc.model.semester.SemestersData
import uz.jbnuu.tsc.utils.MyDiffUtil
import uz.jbnuu.tsc.utils.lg


class SemesterAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<SemesterAdapter.MyViewHolder>() {

    var dataProduct = emptyList<SemestersData>()
    var next: Int? = null

    fun setData(newData: List<SemestersData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: SemestersData, position: Int, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemSemesterBinding = ItemSemesterBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemSemesterBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: SemestersData) {
            lg("semester -> $data")
            if (data.currentExtra == null) {
                if (data.current == true) {
                    lg("current -> 1 " + data.name?.split("-")?.first())

                    data.weeks?.forEach {
                        lg("weeks " + it.current)
                        if (it.current == true) {
                            lg("current -> " + data.name?.split("-")?.first())
                            binding.semesterName.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
                            listener.onItemClick(data, bindingAdapterPosition, 0)
                            return@forEach
                        }
                    }
                }
            }
            if (data.currentExtra == true) {
                binding.semesterName.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
            } else if (data.currentExtra == false) {
                binding.semesterName.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }

            binding.semesterName.text = data.name?.split("-")?.first()
            binding.semesterName.setOnClickListener {
                listener.onItemClick(data, bindingAdapterPosition, 1)
            }

        }
    }
}
