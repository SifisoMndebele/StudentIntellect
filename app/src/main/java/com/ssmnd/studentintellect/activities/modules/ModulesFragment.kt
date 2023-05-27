package com.ssmnd.studentintellect.activities.modules

import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssmnd.studentintellect.R
import com.ssmnd.studentintellect.activities.modules.ModulesFirestoreDatabase.Companion.addOnFailureListener
import com.ssmnd.studentintellect.activities.modules.ModulesFirestoreDatabase.Companion.addOnSuccessListener
import com.ssmnd.studentintellect.databinding.FragmentModulesBinding
import com.ssmnd.studentintellect.utils.LoadingDialog
import com.ssmnd.studentintellect.utils.Utils2.hideKeyboard
import com.ssmnd.studentintellect.utils.Utils2.tempDisable

class ModulesFragment : Fragment() {

    private lateinit var binding: FragmentModulesBinding
    private lateinit var loadingDialog: LoadingDialog
    private var addAdapter : ModulesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentModulesBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(activity)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val databaseModules = ModulesFirestoreDatabase(requireContext())
        databaseModules.get().addOnFailureListener {
            loadingDialog.showError(it.message) {
                activity?.finish()
            }
        }
        databaseModules.modulesList.observe(this) { modulesList ->
            addAdapter = modulesList?.let { ModulesAdapter(activity, it) }
            binding.addModuleList.layoutManager = LinearLayoutManager(activity)
            binding.addModuleList.setHasFixedSize(true)
            binding.addModuleList.adapter = addAdapter
        }

        binding.swipeRefresh.setOnRefreshListener {
            databaseModules.get(true).addOnSuccessListener{_,_->
                loadingDialog.dismiss()
                binding.swipeRefresh.isRefreshing = false
            }.addOnFailureListener {
                loadingDialog.showError(it.message) {
                    activity?.finish()
                }
            }
        }

        binding.addModule.setOnClickListener {
            findNavController().navigate(R.id.action_ModulesFragment_to_AddModuleFragment, arguments)
        }


        binding.doneButton.setOnClickListener {
            it.tempDisable()
            activity?.hideKeyboard(binding.root)
            activity?.finish()
        }

        binding.searchView.queryHint = "Search Module..."
        binding.searchView.onActionViewExpanded()
        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(string: String?): Boolean {
                binding.searchView.clearFocus()
                return false
            }
            override fun onQueryTextChange(string: String): Boolean {
                addAdapter?.filter?.filter(string)
                return false
            }
        })
    }
}