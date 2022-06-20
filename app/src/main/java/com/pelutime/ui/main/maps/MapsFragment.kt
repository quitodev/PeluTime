package com.pelutime.ui.main.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import com.pelutime.R
import com.pelutime.application.State
import com.pelutime.application.cluster.ClusterMap
import com.pelutime.application.cluster.ClusterMarker
import com.pelutime.application.mapSuccess
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.databinding.FragmentMapsBinding
import com.pelutime.presentation.main.maps.MapsViewModel
import com.pelutime.ui.main.maps.dialogs.MapsDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // GOOGLE MAPS
    private var mMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null
    private val clusterManager by lazy { ClusterManager<ClusterMap>(context, mMap) }

    // VIEW BINDING
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by activityViewModels<MapsViewModel>()

    companion object { private const val MY_PERMISSIONS_REQUEST_UBICATION = 1 }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observerGetPlaces()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observerGetPlaces() {
        viewModel.liveGetPlaces.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    setPlacesOnMap(result.data)
                    binding.layoutProgress.visibility = View.GONE
                }
                is State.Failure -> {
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE PLACES", result.exception.toString())

                    when {
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-19.335295, -64.686791)))
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(3f))
        mMap?.setOnCameraIdleListener(clusterManager)
        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.custom_map))
        mMap?.setOnMarkerClickListener(this)

        getLocationPermission()
    }

    private fun getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                mMap?.isMyLocationEnabled = true

                locationRequest()
                locationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
                fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
            }
        } else {
            mMap?.isMyLocationEnabled = true

            locationRequest()
            locationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
    }

    private fun checkLocationPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_UBICATION)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_UBICATION)
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_UBICATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (checkLocationPermission()) {
                        mMap?.isMyLocationEnabled = true

                        locationRequest()
                        locationCallback()

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
                        fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
                    }
                }
            }
        }
    }

    private fun locationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 25000
            fastestInterval = 20000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun locationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                location = locationResult!!.locations[locationResult.locations.size-1]

                if (locationResult.locations.isNotEmpty()) {
                    val geopoint = location?.latitude?.let { location?.longitude?.let { it1 -> LatLng(it, it1) } }
                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(geopoint))
                    mMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
                }
            }
        }
    }

    private fun setPlacesOnMap(places: List<Ubication>) {
        mMap?.clear()
        clusterManager.clearItems()
        clusterManager.renderer = ClusterMarker(requireContext(), mMap!!, clusterManager)

        val arrayList: ArrayList<ClusterMap> = ArrayList()

        for (place in places) {
            val point = ClusterMap(place.place, LatLng(place.geopoint!!.latitude, place.geopoint!!.longitude))
            arrayList.add(point)
        }

        clusterManager.addItems(arrayList)
        clusterManager.cluster()
        arrayList.clear()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        sendDataMarker(p0)
        return true
    }

    private fun sendDataMarker(p0: Marker?) {
        viewModel.liveGetPlaces.value!!.mapSuccess { resultPlaces ->
            if (resultPlaces.isNotEmpty()) {
                for (place in resultPlaces) {
                    if (p0?.title == place.place) {
                        val bundle = Bundle()
                        bundle.putString("address", place.address)
                        bundle.putString("image", place.image)
                        bundle.putString("name", place.name)
                        bundle.putString("phone", place.phone)
                        bundle.putString("place", place.place)
                        bundle.putString("schedule", place.schedule)
                        bundle.putString("website", place.website)

                        val mapsDialogFragment = MapsDialogFragment()
                        mapsDialogFragment.arguments = bundle

                        findNavController().navigate(R.id.mapsDialogFragment, bundle)
                    }
                }
            }
        }
    }

    override fun onStop() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback!!)
        }
        super.onStop()
    }

    override fun onResume() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
        super.onResume()
    }
}