package gb.dom55.bme.smarthome.devicecontrol.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.devices.IrRemote


class SetIRCodesDialog(var remote: IrRemote, val listener: SetCodesListener) : DialogFragment() {

    private lateinit var adapter: CodesAdapter

    interface SetCodesListener {
        fun updateCodes(codesMap: MutableMap<String, String>)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)

            builder.setView(getContentView())
                    .setPositiveButton("Save") { _, _ ->
                        listener.updateCodes(adapter.codesMap)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        dismiss()
                    }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    private fun getContentView() : View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_set_codes, null)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.setCodesRV)
        adapter = CodesAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        setContentView()
        return contentView
    }

    private fun setContentView() {
        for (item in remote.buttonMapping) {
            if (item.key != "") {
                adapter.addItem(item, remote.codeMapping[item.key]!!)
            }
        }
    }

    inner class CodesAdapter: RecyclerView.Adapter<CodesAdapter.CodesViewHolder>() {

        private var buttonIds: MutableList<String> = mutableListOf()
        private var buttonMap: MutableMap<String, String> = mutableMapOf()
        var codesMap: MutableMap<String, String> = mutableMapOf()

        fun addItem(newCodeItem: MutableMap.MutableEntry<String, String>, currentCode: String) {
            val position = buttonIds.size
            buttonIds.add(newCodeItem.key)
            buttonMap[newCodeItem.key] = newCodeItem.value
            codesMap[newCodeItem.key] = currentCode
            notifyItemInserted(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodesViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set_ir_code, parent, false)
            return CodesViewHolder(view)
        }

        override fun getItemCount(): Int {
            return buttonIds.size
        }

        override fun onBindViewHolder(holder: CodesViewHolder, position: Int) {
            holder.buttonName.text = "${buttonMap[buttonIds[position]]}   :   0x"
            holder.buttonCode.setText(codesMap[buttonIds[position]])
            holder.buttonCode.doAfterTextChanged {
                codesMap[buttonIds[position]] = it.toString()
            }
        }

        inner class CodesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var buttonName: TextView = itemView.findViewById(R.id.irSetButtonName)
            var buttonCode: EditText = itemView.findViewById(R.id.irSetButtonCode)

        }

    }

}