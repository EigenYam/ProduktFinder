package com.example.produktfinder

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.graphics.Typeface
import android.view.ViewGroup

class CoopFragment : Fragment(R.layout.fragment_coop) {

    private lateinit var savedCategoriesTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shopName = arguments?.getString("shopName")

        // Nutze die Daten im Fragment, z.B. setze sie in TextViews
        val textViewName = view?.findViewById<TextView>(R.id.textViewNameCoop)
        textViewName?.text = "$shopName"


        savedCategoriesTextView = view.findViewById(R.id.savedCategoriesTextView)

        // Lade die gespeicherten Kategorien
        val savedCategories = loadSavedCategories()

        // Überprüfe, ob die Liste leer ist
        if (savedCategories.isEmpty()) {
            // Zeige die Nachricht an, dass der Kunde eine Liste erstellen sollte
            savedCategoriesTextView.text = "Erstelle zuerst deine Einkaufsliste"

            // Füge Formatierungsanpassungen hinzu
            savedCategoriesTextView.textSize = 18f // Ändere die Textgröße nach Bedarf
            savedCategoriesTextView.setTextColor(requireContext().getColor(R.color.white)) // Ändere die Textfarbe nach Bedarf (z.B., R.color.white)
            savedCategoriesTextView.setTypeface(null, Typeface.BOLD) // Setze den Text fett
            savedCategoriesTextView.setPadding(16, 16, 16, 16) // Füge Padding hinzu (in Pixeln)
            savedCategoriesTextView.layoutParams?.let { params ->
                if (params is ViewGroup.MarginLayoutParams) {
                    params.topMargin = 24 // Füge Margin hinzu (in Pixeln)
                }
            }
        } else {
            // Zeige die gespeicherten Kategorien an
            savedCategoriesTextView.text = "${savedCategories.joinToString("\n")}"

            // Füge Formatierungsanpassungen hinzu
            savedCategoriesTextView.textSize = 18f // Ändere die Textgröße nach Bedarf
            savedCategoriesTextView.setTextColor(requireContext().getColor(R.color.white)) // Ändere die Textfarbe nach Bedarf (z.B., R.color.white)
            savedCategoriesTextView.setTypeface(null, Typeface.BOLD) // Setze den Text fett
            savedCategoriesTextView.setPadding(16, 16, 16, 16) // Füge Padding hinzu (in Pixeln)
            savedCategoriesTextView.layoutParams?.let { params ->
                if (params is ViewGroup.MarginLayoutParams) {
                    params.topMargin = 24 // Füge Margin hinzu (in Pixeln)
                }
            }
        }
    }

    private fun Fragment.loadSavedCategories(): Set<String> {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("selectedCategories", HashSet()) ?: HashSet()
    }

}
