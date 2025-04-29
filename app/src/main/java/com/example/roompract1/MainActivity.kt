package com.example.roompract1

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: TodoAdapter

    private var selectedImagePath: String? = null
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val file = bitmapToFile(it)
            selectedImagePath = file.absolutePath
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //necessary initializations
//        val db = Room.databaseBuilder(applicationContext, TodoDatabase::class.java, "todo-db").build()
        val repository = (application as TodoApplication).repository

        val factory = TodoViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TodoViewModel::class.java]


        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(cacheDir, "todo_image_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }



    private fun setupRecyclerView() {
        adapter = TodoAdapter(
            onCheckedChange = { todo, isChecked ->
                viewModel.updateTodo(todo.copy(isCompleted = isChecked))
            },
            onDelete = { todo ->
                viewModel.deleteTodo(todo)
            }
        )

//        findViewById<RecyclerView>(R.id.todoRecyclerView).adapter = adapter

        val recyclerView = findViewById<RecyclerView>(R.id.todoRecyclerView)
        recyclerView.adapter = adapter
        // Set the LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        //add button click
        findViewById<Button>(R.id.addButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val description = findViewById<EditText>(R.id.descriptionEditText).text.toString()

            viewModel.addTodo(title, description, selectedImagePath)

            selectedImagePath = null // reset after use

            // Clear input fields
            findViewById<EditText>(R.id.titleEditText).text.clear()
            findViewById<EditText>(R.id.descriptionEditText).text.clear()
        }

        //image button click
        findViewById<Button>(R.id.addImageButton).setOnClickListener {
            PermissionX.init(this)
                .permissions(android.Manifest.permission.CAMERA)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(deniedList, "Need camera permission to take picture", "OK", "Cancel")
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(deniedList, "Please allow camera permission in settings", "OK", "Cancel")
                }
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        takePictureLauncher.launch(null)
                    }
                }
        }

    }

    private fun observeViewModel() {


        //this is an async task, so we put it inside coroutine launch block
        lifecycleScope.launch {
            viewModel.todos.collectLatest { todos ->
                adapter.submitList(todos)
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is TodoViewModel.UiState.Error -> {
                        findViewById<TextView>(R.id.errorTextView).apply {
                            text = state.message
                            visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        findViewById<TextView>(R.id.errorTextView).visibility = View.GONE
                    }
                }
            }
        }
    }
}