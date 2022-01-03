package com.jdock.fil_rouge.taskList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdock.fil_rouge.network.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {
    private val tasksRepository = TasksRepository()
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList

    fun refresh() {
        viewModelScope.launch {
            var taskList = tasksRepository.refresh();
            if (taskList != null)
            {
                _taskList.value = taskList;
            }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            if (tasksRepository.delete(task))
            {
                _taskList.value = taskList.value - task;
            }
        }
    }

    fun addOrEdit(task: Task) {
        viewModelScope.launch {
            val oldTask = taskList.value.firstOrNull { it.id == task.id }
            val task = when {
                oldTask != null -> tasksRepository.update(task);
                else -> tasksRepository.create(task);
            }

            if(task != null) {
                if(oldTask != null) {
                    _taskList.value = taskList.value - oldTask;
                }
                _taskList.value = taskList.value + task;
            }
        }
    }

}