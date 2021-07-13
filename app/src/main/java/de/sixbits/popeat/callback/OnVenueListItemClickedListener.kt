package de.sixbits.popeat.callback

import de.sixbits.popeat.data_model.VenueDataModel

interface OnVenueListItemClickedListener {
    fun onVenueListItemClicked(venue: VenueDataModel)
}
