package com.example.produktfinder

import CustomAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CoopFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private val selectedProductsList: MutableList<String> = mutableListOf()
    private lateinit var selectedProductsAdapter: CustomAdapter
    private lateinit var selectedProductsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_coop, container, false)

        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val stringArray = resources.getStringArray(R.array.lebensmittel_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stringArray)
        autoCompleteTextView.setAdapter(adapter)

        selectedProductsRecyclerView = view.findViewById(R.id.selectedProductsRecyclerView)
        selectedProductsAdapter = CustomAdapter(selectedProductsList) // Ersetze CustomAdapter durch deinen benutzerdefinierten Adapter
        selectedProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        selectedProductsRecyclerView.adapter = selectedProductsAdapter

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = stringArray[position]
            selectedProductsList.add(selectedProduct)
            selectedProductsAdapter.notifyDataSetChanged()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CoopFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
