package com.pelutime.ui.main.users.ubication

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pelutime.R
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.databinding.FragmentUsersUbicationBinding
import com.pelutime.presentation.main.users.ubication.UsersUbicationViewModel
import com.pelutime.ui.main.users.ubication.dialogs.UsersUbicationDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersUbicationFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // GOOGLE MAPS
    private var mMap: GoogleMap? = null

    // VIEW BINDING
    private var _binding: FragmentUsersUbicationBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<UsersUbicationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersUbicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observerGetUbication()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observerGetUbication() {
        viewModel.liveGetUbication.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    setPlaceOnMap(result.data)
                    binding.layoutProgress.visibility = View.GONE
                }
                is State.Failure -> {
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE UBICATION", result.exception.toString())

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
        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.custom_map))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-19.335295, -64.686791)))
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(3f))
        mMap?.setOnMarkerClickListener(this)
    }

    private fun setPlaceOnMap(ubication: Ubication) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(ubication.geopoint!!.latitude, ubication.geopoint!!.longitude)))
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))

        val markerOptions = MarkerOptions()

        val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_pin) }
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        markerOptions.title(ubication.name)
        markerOptions.position(LatLng(ubication.geopoint!!.latitude, ubication.geopoint!!.longitude))
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))

        mMap?.addMarker(markerOptions)
    }

    private fun setBundle() : Bundle {
        val bundle = Bundle()

        viewModel.liveGetUbication.value!!.mapSuccess { ubication ->
            bundle.putString("name", ubication.name)
            bundle.putString("image", ubication.image)
            bundle.putString("address", ubication.address)
            bundle.putString("schedule", ubication.schedule)
            bundle.putString("phone", ubication.phone)
            bundle.putString("website", ubication.website)
        }

        val usersUbicationDialogFragment = UsersUbicationDialogFragment()
        usersUbicationDialogFragment.arguments = bundle

        return bundle
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        findNavController().navigate(R.id.usersUbicationDialogFragment, setBundle())
        return true
    }
}