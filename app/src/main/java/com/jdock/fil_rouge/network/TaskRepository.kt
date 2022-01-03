package com.jdock.fil_rouge.network

import android.util.Log
import com.jdock.fil_rouge.taskList.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksRepository {
    private val tasksWebService = Api.tasksWebService


    suspend fun refresh(): List<Task>? {
        val response = tasksWebService.getTasks()
        if (!response.isSuccessful) {
            Log.e("TasksRepository", "Error while fetching tasks: $response")
            return null
        } else {
            Log.e("TasksRepository", "Error: ${response.message()}")
        }
        return response.body()
    }

    suspend fun delete(task: Task) : Boolean {
        val response = tasksWebService.delete(task.id)
        return response.isSuccessful
    }

    suspend fun create(task: Task) : Task? {
        val response = tasksWebService.create(task);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

    suspend fun update(task: Task): Task? {
        val response = tasksWebService.update(task, task.id);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }


    /*
    suspend fun createOrUpdate(task: Task) {
        val oldTask = taskList.value.firstOrNull { it.id == task.id }
        val response = when {
            oldTask != null -> tasksWebService.update(task)
            else -> tasksWebService.create(task)
        }
        if (response.isSuccessful) {
            val updatedTask = response.body()!!
            if (oldTask != null) taskList.value = taskList.value - oldTask
            taskList.value = taskList.value + updatedTask
        }
    }
    */


}