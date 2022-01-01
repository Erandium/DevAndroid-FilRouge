package com.jdock.fil_rouge.network

import com.jdock.fil_rouge.taskList.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksRepository {
    private val tasksWebService = Api.tasksWebService

    // Ces deux variables encapsulent la même donnée:
    // [_taskList] est modifiable mais privée donc inaccessible à l'extérieur de cette classe
    private val taskList = MutableStateFlow<List<Task>>(emptyList())
    // [taskList] est publique mais non-modifiable:
    // On pourra seulement l'observer (s'y abonner) depuis d'autres classes
    public val taskListFlow: StateFlow<List<Task>> = taskList.asStateFlow()

    suspend fun refresh() {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.getTasks()
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val fetchedTasks = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (fetchedTasks != null) taskList.value = fetchedTasks
        }
    }

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

    suspend fun deleteTask(task: Task) {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.delete(task)
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val deletedTask = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (deletedTask != null) taskList.value = taskListFlow.value - deletedTask
        }
    }

}