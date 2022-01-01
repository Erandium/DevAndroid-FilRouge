package com.jdock.fil_rouge.taskList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jdock.fil_rouge.databinding.FragmentTaskListBinding
import form.FormActivity

class TaskListFragment : Fragment() {



    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private lateinit var binding: FragmentTaskListBinding

    private lateinit var adapter : TaskListAdapter

    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task
        if (task != null)
        {
            taskList.add(task)
            adapter.submitList(taskList.toList())
        }
    }



    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskList.remove(task)
            adapter.submitList(taskList.toList())
        }

        override fun onClickEdit(task: Task) {
            taskList.remove(task)
            adapter.submitList(taskList.toList())
            val intent = Intent(context, FormActivity::class.java)
            intent.putExtra("task", task)
            formLauncher.launch(intent)
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
        adapter.submitList(taskList.toList())


        val button = binding.floatingActionButton1
        button.setOnClickListener {
            val intent = Intent(context, FormActivity::class.java)
            formLauncher.launch(intent)
        }
    }
}