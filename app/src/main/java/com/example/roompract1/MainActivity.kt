package com.example.roompract1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: TodoAdapter

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
        findViewById<Button>(R.id.addButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val description = findViewById<EditText>(R.id.descriptionEditText).text.toString()

            viewModel.addTodo(title, description)

            // Clear input fields
            findViewById<EditText>(R.id.titleEditText).text.clear()
            findViewById<EditText>(R.id.descriptionEditText).text.clear()
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