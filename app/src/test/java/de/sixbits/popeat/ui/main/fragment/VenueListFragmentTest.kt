package de.sixbits.popeat.ui.main.fragment

import android.os.Build
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.testing.HiltTestApplication
import de.sixbits.popeat.R
import de.sixbits.popeat.VenueDataModelFactory
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.view_model.HomeViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.junit.jupiter.MockitoExtension
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/**
 * Actions:
 *  1. Observe Recommendation List
 *  2. Observe Loading
 *  3. Request Navigation
 */

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.O_MR1],
    application = HiltTestApplication::class
)
@LooperMode(LooperMode.Mode.PAUSED)
@ExtendWith(MockitoExtension::class)
class VenueListFragmentTest {
    @Mock
    private lateinit var homeViewModel: HomeViewModel


    private lateinit var recommendationsLiveData: MutableLiveData<List<VenueDataModel>>
    private lateinit var loadingLiveData: MutableLiveData<Boolean>

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        loadingLiveData = MutableLiveData()
        recommendationsLiveData = MutableLiveData()

        `when`(homeViewModel.loadingLiveData).thenReturn(loadingLiveData)
        `when`(homeViewModel.recommendationsLiveData).thenReturn(recommendationsLiveData)
    }

    @Test
    fun whenLoadingTheProgressBarIsVisible() {
        loadingLiveData.postValue(true)
        val fragment = VenueListFragment { ViewModelStore() }

        fragment.injectViewModel(homeViewModel)

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        launchFragmentInContainer { fragment }
            .onFragment {
                assert(it.view?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility == View.VISIBLE)
            }
    }

    @Test
    fun whenNotLoadingTheProgressBarIsGone() {
        loadingLiveData.postValue(false)
        val fragment = VenueListFragment { ViewModelStore() }

        fragment.injectViewModel(homeViewModel)

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        launchFragmentInContainer { fragment }
            .onFragment {
                assert(it.view?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility == View.GONE)
            }
    }

    @Test
    fun whenFragmentNewRecommendationsEmittedTheyShowInRecyclerView() {
        recommendationsLiveData.postValue(VenueDataModelFactory.getVenueList())

        val fragment = VenueListFragment { ViewModelStore() }

        fragment.injectViewModel(homeViewModel)

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        launchFragmentInContainer (themeResId = R.style.Base_Theme_MaterialComponents) { fragment }
            .onFragment {
                assert(it.view?.findViewById<RecyclerView>(R.id.rv_venue_recommendations)?.visibility == View.VISIBLE)
                assert(it.view?.findViewById<RecyclerView>(R.id.rv_venue_recommendations)?.adapter?.itemCount == 2)
            }
    }
}