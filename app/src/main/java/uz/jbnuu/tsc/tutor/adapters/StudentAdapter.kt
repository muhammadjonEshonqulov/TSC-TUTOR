package uz.jbnuu.tsc.tutor.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.databinding.ItemStudentBinding
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryData
import uz.jbnuu.tsc.tutor.model.student.StudentData
import uz.jbnuu.tsc.tutor.utils.MyDiffUtil
import java.text.SimpleDateFormat
import java.util.*


class StudentAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {

    var dataProduct = emptyList<StudentData>()
    var next: Int? = null

    fun setData(newData: List<StudentData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: StudentData, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemStudentBinding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(data: StudentData) {
            if (bindingAdapterPosition % 2 == 1) {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_0))
            } else {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_1))
            }
            if (data.last_location != null) {
                binding.lastLocation.alpha = 1f
                binding.historyLocations.alpha = 1f
            } else {
                binding.lastLocation.alpha = 0.5f
                binding.historyLocations.alpha = 0.5f
            }
            var fio = ""
            binding.studentId.text = data.auth_id
            try {
                data.familya?.let {
                    fio += data.familya.substring(0, 1).uppercase() + data.familya.substring(1).lowercase()
                }
                data.ism?.let {
                    fio += " " + data.ism.substring(0, 1).uppercase() + data.ism.substring(1).lowercase()
                }
                data.otasi_ismi?.let {
                    fio += " " + data.otasi_ismi.substring(0, 1).uppercase() + data.otasi_ismi.substring(1).lowercase()
                }
                binding.fullName.text = fio

            } catch (e: Exception) {

            }
            binding.passport.text = data.passport
            binding.address.text = data.viloyat?.split(" ")?.first() + " " + data.viloyat?.split(" ")?.last()?.substring(0, 3) + ". " + data.tuman?.split(" ")?.first() + " " + data.tuman?.split(" ")?.last()?.substring(0, 3) + "."

            binding.lastLocation.setOnClickListener {
                listener.onItemClick(data, 1)
            }
            binding.historyLocations.setOnClickListener {
                listener.onItemClick(data, 2)
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDate = sdf.format(Date())

            val year = currentDate.split(" ").first().split("-").first().toInt()
            val month = currentDate.split(" ").first().split("-")[1].toInt()
            val day = currentDate.split(" ").first().split("-").last().toInt()
            val hour = currentDate.split(" ").last().split(":").first().toInt()
            val minute: Int = currentDate.split(" ").last().split(":")[1].toInt()
            val sekunt = currentDate.split(" ").last().split(":").last().toInt()

            if (data.last_location != null) {

                data.last_location.let {
                    val lastLocation: LocationHistoryData = Gson().fromJson(data.last_location, LocationHistoryData::class.java)
                    val yearBase = lastLocation.data_time?.split(" ")?.first()?.split("-")?.first()?.toInt()
                    val monthBase = lastLocation.data_time?.split(" ")?.first()?.split("-")?.get(1)?.toInt()
                    val dayBase = lastLocation.data_time?.split(" ")?.first()?.split("-")?.last()?.toInt()
                    val hourBase = lastLocation.data_time?.split(" ")?.last()?.split(":")?.first()?.toInt()
                    val minuteBase = lastLocation.data_time?.split(" ")?.last()?.split(":")?.get(1)?.toInt()
                    val sekuntBase = lastLocation.data_time?.split(" ")?.last()?.split(":")?.last()?.toInt()

                    if (year == yearBase && monthBase == month && dayBase == day && hour == hourBase) {
                        minuteBase?.let {
                            sekuntBase?.let {
                                if (minute - minuteBase > 1) {
                                    binding.statusStudent.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.new_tab_color))
                                } else if (minute == minuteBase && sekunt - sekuntBase < 10) {
                                    binding.statusStudent.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.green))
                                } else if (minute - minuteBase == 1 && sekunt - (sekuntBase - 60) < 10) {
                                    binding.statusStudent.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.green))
                                } else {
                                    binding.statusStudent.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.new_tab_color))
                                }
                            }
                        }

                    } else {
                        binding.statusStudent.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.new_tab_color))
                    }

                }
            } else {
                binding.statusStudent.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.new_tab_color))
            }

        }
    }
}
