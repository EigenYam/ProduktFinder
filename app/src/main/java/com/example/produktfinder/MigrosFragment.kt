package com.example.produktfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MigrosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_migros, container, false)

        val shopName = arguments?.getString("shopName")

        // Nutze die Daten im Fragment, z.B. setze sie in TextViews
        val textViewName = view?.findViewById<TextView>(R.id.textViewNameMigros)
        textViewName?.text = "$shopName"

        return view
    }

}