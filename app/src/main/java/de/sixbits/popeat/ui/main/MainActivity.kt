package de.sixbits.popeat.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.sixbits.popeat.R
import de.sixbits.popeat.databinding.ActivityMainBinding
import de.sixbits.popeat.ui.main.fragment.MapFragment
import de.sixbits.popeat.ui.main.fragment.NavigationHeaderFragment
import de.sixbits.popeat.ui.main.fragment.RequestPermissionsFragment
import de.sixbits.popeat.ui.main.fragment.VenueListFragment
import de.sixbits.popeat.ui.main.state.HomeStateExploration
import de.sixbits.popeat.ui.main.state.HomeStateNavigation
import de.sixbits.popeat.view_model.HomeViewModel

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var uiBinding: ActivityMainBinding

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(uiBinding.root)

        if (!this::homeViewModel.isInitialized) {
            homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        }

        if (!homeViewModel.hasLocationPermissions) {
            inflatePermissionsScreen()
        } else {
            inflateHome()
        }
    }

    private fun inflateHome() {
        supportFragmentManager.commit {
            replace(
                uiBinding.fragmentContainerMap.id,
                MapFragment { viewModelStore },
                MapFragment::class.java.name
            )

            replace(
                uiBinding.fragmentContainerVenueList.id,
                VenueListFragment { viewModelStore },
                VenueListFragment::class.java.name
            )
        }

        if (ActivityCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            homeViewModel.requestCurrentLocation()
        }

        homeViewModel.errorLiveData.observe(
            this,
            {
                Snackbar.make(uiBinding.root, it.toString(), Snackbar.LENGTH_LONG).show()
            }
        )

        homeViewModel.homeStateLiveData.observe(this, {
            if (it is HomeStateExploration) {
                val activeFrag =
                    supportFragmentManager.findFragmentByTag(NavigationHeaderFragment::class.java.name)
                if (activeFrag != null) {
                    supportFragmentManager.commit {
                        remove(activeFrag)
                    }
                }
            } else if (it is HomeStateNavigation) {
                supportFragmentManager.commit {
                    replace(
                        uiBinding.fragmentContainerNavigationHeader.id,
                        NavigationHeaderFragment { viewModelStore },
                        NavigationHeaderFragment::class.java.name
                    )
                }
            }
        })
    }

    private fun inflatePermissionsScreen() {
        supportFragmentManager
            .commit {
                replace(
                    uiBinding.containerHome.id,
                    RequestPermissionsFragment(),
                    RequestPermissionsFragment::class.java.name
                )
            }
    }

    private fun removePermissionsFragmentIfExists() {
        val requestPermissionsFragment =
            supportFragmentManager.findFragmentByTag(RequestPermissionsFragment::class.java.name)
        if (requestPermissionsFragment != null) {
            supportFragmentManager.commit {
                remove(requestPermissionsFragment)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!homeViewModel.hasLocationPermissions) {
            inflatePermissionsScreen()
        } else {
            removePermissionsFragmentIfExists()
            inflateHome()
        }
    }


    override fun onBackPressed() {
        if (homeViewModel.homeStateLiveData.value is HomeStateNavigation) {
            homeViewModel.enterExplorationMode()
        } else {
            super.onBackPressed()
        }
    }

    @VisibleForTesting
    fun injectViewModel(testViewModel: HomeViewModel) {
        homeViewModel = testViewModel
    }
}
