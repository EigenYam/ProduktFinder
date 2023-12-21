package com.example.produktfinder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

class EinkaufslisteFragment : Fragment(R.layout.fragment_einkaufsliste) {

    private val categories = arrayOf("Fleisch", "Früchte", "Gemüse", "Getränke", "Backwaren", "Milchprodukte")
    private lateinit var categoryListView: ListView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryListView = view.findViewById(R.id.categoryListView)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice, categories)
        categoryListView.adapter = adapter
        categoryListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        // Laden der gespeicherten Kategorien
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val savedCategories = loadSavedCategories()

        for (i in 0 until adapter.count) {
            if (savedCategories.contains(categories[i])) {
                categoryListView.setItemChecked(i, true)
            }
        }

        categoryListView.setOnItemClickListener(AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            val isSelected = categoryListView.isItemChecked(position)

            val message = if (isSelected) {
                "Ausgewählt: $selectedCategory"
            } else {
                "Abgewählt: $selectedCategory"
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            // Aktualisieren der gespeicherten Kategorien
            updateSavedCategories(selectedCategory, isSelected)
        })

        val saveButton: Button = view.findViewById(R.id.bottomButton)
        saveButton.setOnClickListener {
            // Hier kannst du die gespeicherten Kategorien abrufen und weiterverarbeiten
            val savedCategories = loadSavedCategories()
            Toast.makeText(requireContext(), "Gespeicherte Kategorien: $savedCategories", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSavedCategories(category: String, isSelected: Boolean) {
        val savedCategories = HashSet(loadSavedCategories())

        if (isSelected) {
            savedCategories.add(category)
        } else {
            savedCategories.remove(category)
        }

        val editor = sharedPreferences.edit()
        editor.putStringSet("selectedCategories", savedCategories)
        editor.apply()
    }

    private fun loadSavedCategories(): Set<String> {
        return sharedPreferences.getStringSet("selectedCategories", HashSet()) ?: HashSet()
    }
}