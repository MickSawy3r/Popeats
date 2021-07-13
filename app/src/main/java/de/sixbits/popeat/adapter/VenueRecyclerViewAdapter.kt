package de.sixbits.popeat.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.sixbits.popeat.R
import de.sixbits.popeat.callback.OnVenueListItemClickedListener
import de.sixbits.popeat.data_model.VenueDataModel
import de.sixbits.popeat.databinding.ItemVenueBinding

private const val TAG = "VenueRecyclerViewAdapte"

class VenueRecyclerViewAdapter constructor(
    private var venues: List<VenueDataModel>,
    private val requestBuilder: RequestBuilder<Drawable>,
    private val onVenueListItemClickedListener: OnVenueListItemClickedListener
) : RecyclerView.Adapter<VenueRecyclerViewAdapter.VenueViewHolder>(),
    ListPreloader.PreloadModelProvider<VenueDataModel> {

    class VenueViewHolder(val itemVenueBinding: ItemVenueBinding) :
        RecyclerView.ViewHolder(itemVenueBinding.root) {
        fun bind(item: VenueDataModel) {
            itemVenueBinding.venueObject = item
            itemVenueBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val venueItemBinding = ItemVenueBinding.inflate(layoutInflater, parent, false)
        return VenueViewHolder(venueItemBinding)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(venues[position])
        if (venues[position].image.isEmpty()) {
            holder.itemVenueBinding.ivVenueImage.setImageResource(R.drawable.ic_restaurant)
            holder.itemVenueBinding.ivVenueImage.scaleType = ImageView.ScaleType.FIT_CENTER
        } else {
            requestBuilder
                .load(venues[position].image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(
                    RequestOptions()
                        .error(R.drawable.ic_restaurant)
                        .centerInside()
                )
                .into(holder.itemVenueBinding.ivVenueImage)
        }
        holder.itemVenueBinding.cardVenue.setOnClickListener {
            onVenueListItemClickedListener.onVenueListItemClicked(venues[position])
        }
    }

    override fun getItemCount(): Int = venues.size

    fun replaceItems(newVenus: List<VenueDataModel>) {
        val diffCallback = VenueListDiffCallback(venues, newVenus)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        venues = newVenus
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getPreloadItems(position: Int): MutableList<VenueDataModel> {
        return if (venues.isNotEmpty())
            mutableListOf(venues[position])
        else
            mutableListOf()
    }

    override fun getPreloadRequestBuilder(item: VenueDataModel): RequestBuilder<*> {
        return requestBuilder.load(item)
    }
}
