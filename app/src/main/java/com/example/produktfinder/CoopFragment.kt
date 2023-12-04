package com.example.produktfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class CoopFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_coop, container, false)

        // Zugriff auf die AutoCompleteTextView in Ihrem XML-Layout
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        // Zugriff auf die String-Array-Ressource
        val stringArray = resources.getStringArray(R.array.lebensmittel_array)

        // Erstellen Sie ein ArrayAdapter, um die String-Array-Ressource an die AutoCompleteTextView zu binden
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stringArray)

        // Binden Sie den ArrayAdapter an die AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter)

        return view
    }
}