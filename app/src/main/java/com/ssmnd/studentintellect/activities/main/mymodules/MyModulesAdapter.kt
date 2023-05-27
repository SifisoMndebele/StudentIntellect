package com.ssmnd.studentintellect.activities.main.mymodules

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ssmnd.studentintellect.R
import com.ssmnd.studentintellect.activities.auth.AppUser
import com.ssmnd.studentintellect.activities.main.MainActivity
import com.ssmnd.studentintellect.activities.main.materials.MaterialsFragment
import com.ssmnd.studentintellect.models.Module
import com.ssmnd.studentintellect.utils.Utils2.hideKeyboard
import com.ssmnd.studentintellect.utils.Utils2.tempDisable
import java.text.DateFormat
import java.util.*

class MyModulesAdapter(
    private val activity: MainActivity,
    private val myModulesList: List<Module>,
    private val requestMultiplePermissions: ActivityResultLauncher<Array<String>>) :
    RecyclerView.Adapter<MyModulesAdapter.ViewHolder>() {

    private var clickedModule: Module? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val code: TextView = itemView.findViewById(R.id.module_code)
        private val name: TextView = itemView.findViewById(R.id.module_name)
        private val menu: ImageView = itemView.findViewById(R.id.menu_button)

        @SuppressLint("SetTextI18n")
        fun bind(module: Module) {
            code.text = module.code
            name.text = module.name

            itemView.setOnClickListener {
                it.tempDisable(2000)
                itemView.context.hideKeyboard(itemView.rootView)
                clickedModule = module
                checkPermissionsAndOpenMaterialsList()
            }

            val popupMenu = getPopupMenu(module)
            menu.setOnClickListener { menu->
                menu.tempDisable()
                popupMenu.show()
            }
            menu.visibility = View.VISIBLE
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun getPopupMenu(module: Module) : PopupMenu {
            val popupMenu = PopupMenu(activity, menu)
            popupMenu.gravity = Gravity.END
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                popupMenu.setForceShowIcon(true)
            popupMenu.menuInflater.inflate(R.menu.popup_menu_module, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.item_delete -> {
                        AppUser.deleteModule(module.id)
                    }
                }
                true
            }

            popupMenu.menu.findItem(R.id.item_adder_name).title = "By ${module.adderName}"
            val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            popupMenu.menu.findItem(R.id.item_updated_date).title = "Updated at " +
                    module.timeUpdated.toDate().let { dateFormat.format(it) }
            popupMenu.menu.findItem(R.id.item_verified).title = if (module.isVerified) "Verified" else "Not Verified"

            return popupMenu
        }
    }

    //Permissions Check
    internal fun checkPermissionsAndOpenMaterialsList() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            // You can use the API that requires the permission.
            gotoMaterialsFragment()
        }
        else {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }
    fun gotoMaterialsFragment() {
        val bundle = Bundle().apply {
            putParcelable(MaterialsFragment.MY_MODULE_DATA, clickedModule!!)
            putParcelable(MaterialsFragment.FOLDER_DATA, null)
        }
        activity.findNavController(R.id.nav_host_fragment_content_main)
            .navigate(R.id.action_materials_fragment, bundle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_module, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < myModulesList.size){
            holder.bind(myModulesList[position])
        }
    }

    override fun getItemCount(): Int {
        return myModulesList.size
    }
}