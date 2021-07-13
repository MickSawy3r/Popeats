package de.sixbits.popeat.ui.main.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import de.sixbits.popeat.databinding.FragmentRequestPermissionsBinding

private const val TAG = "RequestPermissionsFragm"
private const val PERMISSIONS_REQUEST_CODE = 1200

@AndroidEntryPoint
class RequestPermissionsFragment : Fragment() {
    private lateinit var binding: FragmentRequestPermissionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestPermissionsBinding.inflate(inflater, viewGroup, false)

        Log.d(TAG, "onCreateView: ")
        initViews()

        return binding.root
    }

    /**
     * When we request a location, we get OnActivityResult Callback called.
     * This is how this fragment will get replaced automatically
     */
    private fun initViews() {
        binding.btnRequestPermissions.setOnClickListener {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
                ),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }
}
