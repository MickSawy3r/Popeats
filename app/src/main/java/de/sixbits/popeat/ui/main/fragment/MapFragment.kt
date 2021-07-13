package de.sixbits.popeat.ui.main.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.sixbits.popeat.EspressoIdlingResource
import de.sixbits.popeat.R
import de.sixbits.popeat.databinding.MapFragmentBinding
import de.sixbits.popeat.ui.main.state.HomeStateExploration
import de.sixbits.popeat.ui.main.state.HomeStateNavigation
import de.sixbits.popeat.utils.LocationHelper
import de.sixbits.popeat.view_model.HomeViewModel

private const val TAG = "MapFragment"
private const val DEFAULT_ZOOM = 16.0f

@AndroidEntryPoint
class MapFragment @JvmOverloads constructor(
    private val viewModelStoreOwner: ViewModelStoreOwner? = null
) : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener {

    private lateinit var uiBinding: MapFragmentBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiBinding = MapFragmentBinding.inflate(inflater, container, false)

        if (!this::homeViewModel.isInitialized) {
            homeViewModel = if (viewModelStoreOwner == null) {
                ViewModelProvider(this).get(HomeViewModel::class.java)
            } else {
                ViewModelProvider(viewModelStoreOwner).get(HomeViewModel::class.java)
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        EspressoIdlingResource.increment()

        mapFragment.getMapAsync(this)

        return uiBinding.root
    }

    private fun checkReadyThen(stuffToDo: () -> Unit) {
        if (!::map.isInitialized) {
            Snackbar.make(uiBinding.root, "Map is Not Ready", Snackbar.LENGTH_SHORT).show()
        } else {
            stuffToDo()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "onMapReady: ")
        EspressoIdlingResource.decrement()
        map = googleMap ?: return

        with(googleMap) {
            setOnCameraIdleListener(this@MapFragment)
        }

        homeViewModel.homeStateLiveData.observe(viewLifecycleOwner) {
            EspressoIdlingResource.increment()
            if (it is HomeStateExploration) {
                showExplorationMarker(it)
            } else if (it is HomeStateNavigation) {
                showNavigationMarker(it)
            }
        }
    }

    override fun onCameraIdle() {
        EspressoIdlingResource.decrement()
        checkReadyThen {
            homeViewModel.getRecommendations(
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude
            )
        }
    }

    private fun showExplorationMarker(it: HomeStateExploration) {
        checkReadyThen {
            map.clear()

            val bitmap = homeViewModel.getBitmapDescriptorFrom(R.drawable.ic_map_marker)

            val mapMarker = MarkerOptions()
            mapMarker.position(it.marker).icon(bitmap).title("My Location")

            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    it.marker,
                    DEFAULT_ZOOM
                )
            )
            map.addMarker(mapMarker)
        }
    }

    private fun showNavigationMarker(it: HomeStateNavigation) {
        checkReadyThen {
            map.clear()

            val bitmap = homeViewModel.getBitmapDescriptorFrom(R.drawable.ic_map_marker_red)
            val mapMarker = MarkerOptions()
            mapMarker.position(it.marker).icon(bitmap).title(it.venue.name)

            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    it.marker,
                    DEFAULT_ZOOM
                )
            )
            map.addMarker(mapMarker)
        }
    }

    @VisibleForTesting
    fun injectViewModel(testViewModel: HomeViewModel) {
        homeViewModel = testViewModel
    }

    @VisibleForTesting
    fun injectMap(testMap: GoogleMap) {
        this.map = testMap
    }
}
