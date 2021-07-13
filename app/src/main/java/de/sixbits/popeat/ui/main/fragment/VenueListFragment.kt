package de.sixbits.popeat.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import dagger.hilt.android.AndroidEntryPoint
import de.sixbits.popeat.adapter.VenueRecyclerViewAdapter
import de.sixbits.popeat.callback.OnVenueListItemClickedListener
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.databinding.VenueListFragmentBinding
import de.sixbits.popeat.view_model.HomeViewModel

private const val TAG = "VenueListFragment"

@AndroidEntryPoint
class VenueListFragment @JvmOverloads constructor(
    private val viewModelStoreOwner: ViewModelStoreOwner? = null
) : Fragment(), OnVenueListItemClickedListener {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var uiBinding: VenueListFragmentBinding
    private lateinit var venueListAdapter: VenueRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiBinding = VenueListFragmentBinding.inflate(inflater, container, false)

        if (!this::homeViewModel.isInitialized) {
            homeViewModel = if (viewModelStoreOwner == null) {
                ViewModelProvider(this).get(HomeViewModel::class.java)
            } else {
                ViewModelProvider(viewModelStoreOwner).get(HomeViewModel::class.java)
            }
        }

        createRecyclerAdapter()
        initRecyclerView()
        initListeners()

        return uiBinding.root
    }

    fun createRecyclerAdapter() {
        val imageRecyclerRequestBuilder = Glide
            .with(this)
            .asDrawable()
        venueListAdapter = VenueRecyclerViewAdapter(
            listOf(),
            imageRecyclerRequestBuilder,
            this
        )
        uiBinding.rvVenueRecommendations.adapter = venueListAdapter
    }

    private fun initRecyclerView() {
        val preloadSizeProvider = ViewPreloadSizeProvider<VenueDataModel>()
        // For Preloading images
        val preLoader = RecyclerViewPreloader(
            Glide.with(this),
            venueListAdapter,
            preloadSizeProvider,
            6
        )
        venueListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        uiBinding.rvVenueRecommendations.addOnScrollListener(preLoader)
    }

    private fun initListeners() {
        homeViewModel.recommendationsLiveData.observe(
            viewLifecycleOwner,
            {
                venueListAdapter.replaceItems(it)
            }
        )
        homeViewModel.loadingLiveData.observe(
            viewLifecycleOwner,
            { isLoading ->
                if (isLoading) {
                    uiBinding.pbLoading.visibility =
                        View.VISIBLE
                } else {
                    uiBinding.pbLoading.visibility =
                        View.GONE
                }
            }
        )
    }

    override fun onVenueListItemClicked(venue: VenueDataModel) {
        homeViewModel.enterNavigationMode(venue)
    }

    @VisibleForTesting
    fun injectViewModel(testViewModel: HomeViewModel) {
        homeViewModel = testViewModel
    }
}
