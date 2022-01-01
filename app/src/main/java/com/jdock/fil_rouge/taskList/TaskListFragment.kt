package com.jdock.fil_rouge.taskList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jdock.fil_rouge.databinding.FragmentTaskListBinding
import com.jdock.fil_rouge.form.FormActivity
import com.jdock.fil_rouge.network.TasksRepository
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private lateinit var binding: FragmentTaskListBinding

    private lateinit var adapter : TaskListAdapter

    private val tasksRepository = TasksRepository()

    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task
        if (task != null)
        {
            lifecycleScope.launch {
                tasksRepository.createOrUpdate(task)
                tasksRepository.refresh()
            }
        }
    }


    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            lifecycleScope.launch {
                tasksRepository.deleteTask(task)
                tasksRepository.refresh()
            }
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
        //adapter.submitList(taskList.toList())


        val button = binding.floatingActionButton1
        button.setOnClickListener {
            val intent = Intent(context, FormActivity::class.java)
            formLauncher.launch(intent)
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            tasksRepository.taskListFlow.collect { newList ->
                adapter.submitList(newList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            tasksRepository.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        }
    }
}