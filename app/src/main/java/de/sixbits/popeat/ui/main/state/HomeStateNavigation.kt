package de.sixbits.popeat.ui.main.state

import com.google.android.gms.maps.model.LatLng
import de.sixbits.popeat.data_model.VenueDataModel

class HomeStateNavigation constructor(val marker: LatLng, val venue: VenueDataModel, val distance: String) :
    HomeState()