package com.example.diary

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diary.databinding.ActivityUpdateNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1
    private var selectedDate: String = "" // Variable to store the selected date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        val note = db.getNoteByID(noteId)

        // Populate the EditText fields with the retrieved note data
        binding.updateTitleEditText.setText(note.title)
        binding.updateContentEditText.setText(note.content)
        selectedDate = note.date // Initialize the selected date with the note's current date

        // Set up the CalendarView with the existing date
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentNoteDate = sdf.parse(note.date)
        currentNoteDate?.let {
            binding.updateCalendarView.date = it.time
        }

        // Listen for date changes in the CalendarView
        binding.updateCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year" // Update the selected date
        }

        // Set up the save button click listener
        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()

            // Validate input fields
            if (newTitle.isBlank() || newContent.isBlank()) {
                Toast.makeText(this, "Title and Content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create an updated Note object with the selected date
            val updatedNote = Note(noteId, newTitle, newContent, selectedDate)

            // Update the note in the database
            db.updateNote(updatedNote)

            // Show confirmation and close the activity
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
