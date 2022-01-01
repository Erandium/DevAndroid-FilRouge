package com.jdock.fil_rouge.taskList

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
    fun onClickShare(task:Task)
}