package com.example.roompract1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TodoAdapter(
    private val onCheckedChange: (Todo, Boolean) -> Unit,
    private val onDelete: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var todos = listOf<Todo>()

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        private val imageView : ImageView = itemView.findViewById(R.id.todoImageView)

        fun bind(todo: Todo) {
            titleTextView.text = todo.title
            descriptionTextView.text = todo.description
            checkbox.isChecked = todo.isCompleted

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(todo, isChecked)
            }

            deleteButton.setOnClickListener {
                onDelete(todo)
            }

            // Load image if available
            if (!todo.imagePath.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(todo.imagePath)
                    .into(imageView)
            } else {
                imageView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size

    fun submitList(newTodos: List<Todo>) {
        todos = newTodos
        notifyDataSetChanged()
    }
}