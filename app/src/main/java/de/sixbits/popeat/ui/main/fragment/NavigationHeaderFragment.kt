package de.sixbits.popeat.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.transition.TransitionInflater
import dagger.hilt.android.AndroidEntryPoint
import de.sixbits.popeat.R
import de.sixbits.popeat.databinding.FragmentNavigationHeaderBinding
import de.sixbits.popeat.ui.main.state.HomeStateNavigation
import de.sixbits.popeat.view_model.HomeViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class NavigationHeaderFragment @JvmOverloads constructor(
    private val viewModelStoreOwner: ViewModelStoreOwner? = null
) : Fragment() {

    lateinit var uiBinding: FragmentNavigationHeaderBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_from_top)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiBinding = FragmentNavigationHeaderBinding.inflate(inflater)

        if (!this::homeViewModel.isInitialized) {
            homeViewModel = if (viewModelStoreOwner == null) {
                ViewModelProvider(this).get(HomeViewModel::class.java)
            } else {
                ViewModelProvider(viewModelStoreOwner).get(HomeViewModel::class.java)
            }
        }

        val currentState = homeViewModel.homeStateLiveData.value

        if (currentState is HomeStateNavigation) {
            uiBinding.venue = currentState
        } else {
            uiBinding.clData.visibility = View.GONE
            uiBinding.flError.visibility = View.VISIBLE
        }

        return uiBinding.root
    }

    @VisibleForTesting
    fun injectViewModel(testViewModel: HomeViewModel) {
        homeViewModel = testViewModel
    }
}
