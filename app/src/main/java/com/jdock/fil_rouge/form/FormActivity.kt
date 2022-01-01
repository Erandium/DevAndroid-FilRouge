package com.jdock.fil_rouge.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.jdock.fil_rouge.R
import com.jdock.fil_rouge.databinding.FragmentTaskListBinding
import com.jdock.fil_rouge.taskList.Task
import java.util.*

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val titleText = findViewById<EditText>(R.id.form_title)
        val descriptionText = findViewById<EditText>(R.id.form_description)
        val buttonCreate = findViewById<Button>(R.id.create_button)

        titleText.hint = "Title"
        descriptionText.hint = "Description"

        val task = intent.getSerializableExtra("task") as? Task
        val taskId = task?.id ?: UUID.randomUUID().toString()

        titleText.setText(task?.title)
        descriptionText.setText(task?.description)

        if (task != null)
        {
            buttonCreate.text = "Confirm"
        }
        else
        {
            buttonCreate.text = "Create"
        }


        buttonCreate.setOnClickListener {
            val title = titleText.text.toString()
            val description = descriptionText.text.toString()
            val newTask = Task(id = taskId, title = title, description = description)
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}