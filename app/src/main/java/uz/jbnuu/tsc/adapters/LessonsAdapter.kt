package uz.jbnuu.tsc.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.databinding.ItemLessonBinding
import uz.jbnuu.tsc.databinding.ItemTimeTableBinding
import uz.jbnuu.tsc.model.schedule.ScheduleData
import uz.jbnuu.tsc.utils.MyDiffUtil
import java.text.SimpleDateFormat
import java.util.*


class LessonsAdapter() : RecyclerView.Adapter<LessonsAdapter.MyViewHolder>() {

    var dataProduct = emptyList<ScheduleData>()
    var next: Int? = null

    fun setData(newData: List<ScheduleData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

//    interface OnItemClickListener {
//        fun onItemClick(data: ScheduleData, type: Int)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemLessonBinding = ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemLessonBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: ScheduleData) {
            data.subject?.name?.let {
                binding.nameLesson.text = ""+(bindingAdapterPosition+1)+". "+it
            }
            data.auditorium?.name?.let {
                binding.room.text = it
            }
            data.trainingType?.name?.let {
                binding.trainingType.text = it
            }
            data.employee?.name?.let {
                binding.employee.text = it
            }
            data.lessonPair?.start_time?.let {
                binding.startTime.text = it
            }
        }
    }
}
