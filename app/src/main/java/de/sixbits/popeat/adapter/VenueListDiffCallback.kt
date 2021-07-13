package de.sixbits.popeat.adapter

import androidx.recyclerview.widget.DiffUtil
import de.sixbits.popeat.data_model.VenueDataModel

class VenueListDiffCallback constructor(
    private val oldList: List<VenueDataModel>,
    private val newList: List<VenueDataModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
                && oldList[oldItemPosition].image == newList[newItemPosition].image
    }
}