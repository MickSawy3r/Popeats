package de.sixbits.popeat.ui.main.fragment

import android.os.Build
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStore
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.sixbits.popeat.R
import de.sixbits.popeat.VenueDataModelFactory
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.ui.main.state.HomeState
import de.sixbits.popeat.ui.main.state.HomeStateExploration
import de.sixbits.popeat.ui.main.state.HomeStateNavigation
import de.sixbits.popeat.view_model.HomeViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.junit.jupiter.MockitoExtension
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
@ExtendWith(MockitoExtension::class)
class NavigationHeaderFragmentTest {
    @Mock
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var recommendationsLiveData: MutableLiveData<List<VenueDataModel>>
    private lateinit var loadingLiveData: MutableLiveData<Boolean>
    private lateinit var homeStateViewModel: MutableLiveData<HomeState>

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        loadingLiveData = MutableLiveData()
        recommendationsLiveData = MutableLiveData()
        homeStateViewModel = MutableLiveData()

        Mockito.`when`(homeViewModel.homeStateLiveData).thenReturn(homeStateViewModel)
    }

    @Test
    fun whenViewModelContainsAVenue_VenueDetailsShowsCorrectly() {
        val state = HomeStateNavigation(
            LatLng(12.0, 12.0),
            VenueDataModelFactory.getVenueItem1(),
            "3"
        )
        homeStateViewModel.postValue(state)

        val fragment = NavigationHeaderFragment { ViewModelStore() }
        fragment.injectViewModel(homeViewModel)

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        launchFragmentInContainer(themeResId = R.style.Base_Theme_MaterialComponents) { fragment }
            .onFragment {
                assert(it.view?.findViewById<TextView>(R.id.tv_distance)?.text == "3")
                assert(it.view?.findViewById<TextView>(R.id.tv_header_venue_name)?.text == state.venue.name)
                assert(it.view?.findViewById<TextView>(R.id.tv_header_venue_category)?.text == state.venue.category)
                assert(it.view?.findViewById<TextView>(R.id.tv_header_venue_address)?.text == state.venue.location)
            }
    }

    @Test
    fun whenExplorationMode_ErrorViewShouldBeVisible() {
        val state = HomeStateExploration(LatLng(12.0, 12.0))
        homeStateViewModel.postValue(state)

        val fragment = NavigationHeaderFragment { ViewModelStore() }
        fragment.injectViewModel(homeViewModel)

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        launchFragmentInContainer(themeResId = R.style.Base_Theme_MaterialComponents) { fragment }
            .onFragment {
                assert(it.view?.findViewById<ConstraintLayout>(R.id.cl_data)?.visibility == View.GONE)
                assert(it.view?.findViewById<FrameLayout>(R.id.fl_error)?.visibility == View.VISIBLE)
            }
    }
}