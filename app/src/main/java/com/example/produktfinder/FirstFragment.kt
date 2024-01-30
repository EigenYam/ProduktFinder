package com.example.produktfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.produktfinder.databinding.FragmentFirstBinding

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.Math.round


data class Shop(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val json = readJsonFromFile()
        val shops = getShopsFromJson(json)

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    false) || permissions.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(requireContext(),"location acces granted", Toast
                        .LENGTH_SHORT).show()

                    if (isLocationEnabled()) {
                        val result = fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            CancellationTokenSource().token
                        )

                        result.addOnCompleteListener { task ->
                            if (task.isSuccessful && task.result != null) {
                                val latitude = task.result.latitude
                                val longitude = task.result.longitude

                                val currentLocation = Location("CurrentLocation")
                                currentLocation.latitude = latitude
                                currentLocation.longitude = longitude

                                //binding.textViewLocation.text = "Latitude: $latitude\nLongitude: $longitude"

                                val homeLocation = Location("HomeLocation")
                                homeLocation.latitude = 47.15210819897755
                                homeLocation.longitude = 7.322771786461776

                                //val closestShops = getClosestShops(shops, currentLocation)
                                val closestShopDistancePairs = getClosestShopsWithDistance(shops, currentLocation)

                                //val shopInfo = closestShops.joinToString("\n") { it.name }
                                val shopInfo = closestShopDistancePairs.joinToString("\n") { (shop, distance) ->
                                    "${shop.name} -  $distance km"
                                }

                                //closestShops.forEach { shop ->
                                closestShopDistancePairs.forEach { (shop, distance) ->
                                    val shopTextView = TextView(requireContext())
                                    // Customize the appearance for each shop item
                                    shopTextView.text = "${shop.name} - $distance km \n"
                                    shopTextView.textSize = 16f
                                    shopTextView.setTextColor(Color.BLACK)
                                    shopTextView.setOnClickListener {
                                        // Pass the selected shop information to the next fragment
                                        val bundle = Bundle().apply {
                                            putString("shopName", shop.name)
                                        }
                                        when {
                                        shop.name.startsWith("Migros", ignoreCase = true) -> {
                                            findNavController().navigate(R.id.action_FirstFragment_to_MigrosFragment, bundle)
                                        }
                                        shop.name.startsWith("Coop", ignoreCase = true) -> {
                                            findNavController().navigate(R.id.action_FirstFragment_to_CoopFragment, bundle)
                                        }
                                        else -> {
                                            findNavController().navigate(R.id.action_FirstFragment_to_MigrosFragment, bundle)
                                        }
                                    }
                                    }
                                    // Add the TextView to your layout
                                    binding.yourContainer.addView(shopTextView)
                                }

                                //binding.textViewLocation.text = shopInfo
                            }
                        }
                    } else  {
                        Toast.makeText(requireContext(),"Please turn ON the location.",
                            Toast.LENGTH_SHORT)
                            .show()
                        createLocationRequest()
                    }
                }

                else -> {
                    Toast.makeText(requireContext(), "no locatation access", Toast
                        .LENGTH_SHORT).show()
                }

            }
        }

        binding.buttonLocation.setOnClickListener {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        return binding.root

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).setMinUpdateIntervalMillis(5000).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        requireActivity(),
                        100
                    )
                }catch (sendEx: java.lang.Exception) {

                }
            }
        }
    }

    private fun readJsonFromFile(): String {
        return resources.openRawResource(R.raw.shoplist).use {
            it.bufferedReader().readText()
        }
    }

    private fun getShopsFromJson(json: String): List<Shop> {
        val gson = GsonBuilder().setLenient().create()
        val shopType = object : TypeToken<List<Shop>>() {}.type
        return gson.fromJson(json, shopType) ?: emptyList()
    }

    private fun getClosestShopsWithDistance(shops: List<Shop>, currentLocation: Location, limit: Int = 5): List<Pair<Shop, Double>> {
        val shopDistancePairs = shops.map { shop ->
            val shopLocation = Location("ShopLocation")
            shopLocation.latitude = shop.latitude
            shopLocation.longitude = shop.longitude

            val distanceM = currentLocation.distanceTo(shopLocation)
            val distanceKm = round((distanceM / 1000.0) * 10.0) / 10.0 // Runde auf eine Dezimalstelle


            Pair(shop, distanceKm)
        }

        val sortedPairs = shopDistancePairs.sortedBy { it.second }

        // Limit is set to 5 to only return the 5 closest shops with distances
        return sortedPairs.subList(0, minOf(limit, sortedPairs.size))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
