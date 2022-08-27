package uz.jbnuu.tsc.base

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.databinding.DialogWaitBinding

class ProgressDialog : AlertDialog {

    var binding:DialogWaitBinding


    constructor(context: Context, message: String) : super(context) {
        this.setCancelable(false)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_wait, null, false)
        binding = DialogWaitBinding.bind(view)

        view?.apply {
            binding.textPd.text = message
        }
        setView(view)
    }

    fun setMessage(s: String) {
        binding.textPd.text = s
    }
}