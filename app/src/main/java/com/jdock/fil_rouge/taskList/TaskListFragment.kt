package com.jdock.fil_rouge.taskList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.jdock.fil_rouge.R
import com.jdock.fil_rouge.authentication.SHARED_PREF_TOKEN_KEY
import com.jdock.fil_rouge.databinding.FragmentTaskListBinding
import com.jdock.fil_rouge.form.FormActivity
import com.jdock.fil_rouge.network.Api
import com.jdock.fil_rouge.user.UserInfoActivity
import com.jdock.fil_rouge.user.UserInfoViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private lateinit var binding: FragmentTaskListBinding

    private lateinit var adapter : TaskListAdapter

    private val taskViewModel: TaskListViewModel by viewModels()
    private val userViewModel: UserInfoViewModel by viewModels()


    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task
        if (task != null)
        {
            taskViewModel.addOrEdit(task)
            taskViewModel.refresh()
        }
    }

    private  val avatarLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    }

    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskViewModel.delete(task)
            taskViewModel.refresh()
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(context, FormActivity::class.java)
            intent.putExtra("task", task)
            formLauncher.launch(intent)
        }

        override fun onClickShare(task: Task){
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "My task " + task.title + " : " + task.description)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = TaskListAdapter(adapterListener)
        recyclerView.adapter = adapter


        val button = binding.addButton
        button.setOnClickListener {
            val intent = Intent(context, FormActivity::class.java)
            formLauncher.launch(intent)
        }

        val avatar = binding.profileImage
        avatar.setOnClickListener {
            val intent = Intent(context, UserInfoActivity::class.java)
            avatarLauncher.launch(intent)
        }

        lifecycleScope.launch {
            taskViewModel.taskList.collect { newList ->
                adapter.submitList(newList)
            }
        }

        val lougoutButton = binding.logoutButton
        lougoutButton.setOnClickListener{
            PreferenceManager.getDefaultSharedPreferences(context).edit{
                putString(SHARED_PREF_TOKEN_KEY,"")
            }
            findNavController().navigate(R.id.action_taskListFragment_to_authenticationFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        taskViewModel.refresh()
        userViewModel.loadUser()

        val userProfileTextView = binding.profileText
        val userProfileAvatar = binding.profileImage


        lifecycleScope.launch {
            userViewModel.userInfo.collect { userInfo ->
                userProfileTextView.text = "${userInfo?.firstName} \n ${userInfo?.lastName}"
                userProfileAvatar.load(userInfo?.avatar) {
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}