package com.example.hows_this_day.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.hows_this_day.R
import java.util.ArrayList
import java.util.HashMap


class CFragment : Fragment() {


    var dialogItemList: MutableList<Map<String, Any>>? = null

    var image = intArrayOf(R.drawable.redheart, R.drawable.redheart, R.drawable.redheart)
    var text = arrayOf("2019-11-21", "2019-11-22", "2019-11-23")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        dialogItemList = ArrayList()

        for (i in image.indices) {
            val itemMap = HashMap<String, Any>()
            itemMap[TAG_IMAGE] = image[i]
            itemMap[TAG_TEXT] = text[i]
            dialogItemList?.add(itemMap)
        }

        return inflater.inflate(R.layout.fragment_c, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button_run = getView()?.findViewById<View>(R.id.bt_datechooser) as Button?
        button_run?.setOnClickListener { showAlertDialog() }

    }


    private fun showAlertDialog() {

        var tvresult = getView()?.findViewById<View>(R.id.textview_main_text) as TextView?
        val builder = AlertDialog.Builder(getActivity())
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.alert_dialog, null)
        builder.setView(view)

        val listview = view.findViewById<View>(R.id.listview_alterdialog_list) as ListView
        val dialog = builder.create()

        val simpleAdapter = SimpleAdapter(
            getActivity(), dialogItemList,
            R.layout.alert_dialog_row,
            arrayOf(TAG_IMAGE, TAG_TEXT),
            intArrayOf(R.id.alertDialogItemImageView, R.id.alertDialogItemTextView)
        )

        listview.adapter = simpleAdapter
        listview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                tvresult?.text = (text[position] )
                dialog.dismiss()
            }

        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    companion object {

        private val TAG_TEXT = "text"
        private val TAG_IMAGE = "image"
    }

}