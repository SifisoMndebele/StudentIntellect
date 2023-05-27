package com.ssmnd.studentintellect.activities.modules

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ssmnd.studentintellect.R
import com.ssmnd.studentintellect.activities.auth.AppUser
import com.ssmnd.studentintellect.models.Module
import com.ssmnd.studentintellect.utils.Utils2.tempDisable
import java.util.*

class ModulesAdapter(
    private val context: Context?,
    private val modulesList: List<Module>,
) : RecyclerView.Adapter<ModulesAdapter.ViewHolder>(), Filterable {

    /*init {
        AppUser.getModulesSet().value?.forEach { module ->
            if (!modulesList.itContains(module)){
                AppUser.deleteModule(module.id)
                if (context != null) Toast.makeText(context, module.code+" is deleted by an admin.", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    private var snapshotsFiltered = modulesList.sortedBy { it.code }

    private fun List<Module>.itContains(module : Module) : Boolean {
        return map { it.id.trim() }.contains(module.id.trim())
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val code: TextView = itemView.findViewById(R.id.module_code)
        private val name: TextView = itemView.findViewById(R.id.module_name)
        private val menu: ImageView = itemView.findViewById(R.id.menu_button)
        private val checkBox: CheckBox = itemView.findViewById(R.id.select_check_box)

        fun bind(module: Module) {
            code.text = module.code
            name.text = module.name
            checkBox.isChecked = AppUser.getModulesSet().value?.toList()?.itContains(module)?:false

            checkBox.isClickable = false
            itemView.setOnClickListener {
                it.tempDisable()
                if (checkBox.isChecked) {
                    checkBox.isChecked = false
                    AppUser.deleteModule(module.id)
                } else {
                    checkBox.isChecked = true
                    AppUser.addModule(module)
                }
            }

            menu.setOnClickListener {
                if (context != null) {
                    AlertDialog.Builder(context)
                        .setTitle("TODO")
                        .setMessage("Module Menu.")
                        .show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_module_select, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < snapshotsFiltered.size){
            holder.bind(snapshotsFiltered[position])
        }
    }

    override fun getItemCount(): Int {
        return snapshotsFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val pattern = constraint.toString().lowercase(Locale.getDefault())
                snapshotsFiltered = if (pattern.isEmpty()) {
                    modulesList.sortedBy { it.code }
                } else {
                    val filteredList = mutableListOf<Module>()
                    for (data in modulesList) {
                        if (data.name.lowercase().contains(pattern) || data.code.lowercase().contains(pattern)) {
                            filteredList.add(data)
                        }
                    }
                    filteredList.sortedBy { it.code }
                }

                return FilterResults().apply { values = snapshotsFiltered }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                snapshotsFiltered = (results.values as List<Module>).sortedBy { it.code }
                notifyDataSetChanged()
            }
        }
    }
}