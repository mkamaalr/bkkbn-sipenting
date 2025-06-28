package com.bkkbnjabar.sipenting.ui.breastfeedingmother

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkkbnjabar.sipenting.databinding.ItemBreastfeedingMotherBinding

class BreastfeedingMotherAdapter(
    private val onItemClicked: (Int) -> Unit
) : ListAdapter<BreastfeedingMotherListViewModel.BreastfeedingMotherUI, BreastfeedingMotherAdapter.MotherViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotherViewHolder {
        val binding = ItemBreastfeedingMotherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MotherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MotherViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentItem.localId)
        }
        holder.bind(currentItem)
    }

    class MotherViewHolder(private val binding: ItemBreastfeedingMotherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(motherUI: BreastfeedingMotherListViewModel.BreastfeedingMotherUI) {
            binding.apply {
                tvMotherName.text = motherUI.name
                tvMotherNik.text = "NIK: ${motherUI.nik}"
                tvMotherVisitDetails.text = motherUI.details
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BreastfeedingMotherListViewModel.BreastfeedingMotherUI>() {
        override fun areItemsTheSame(oldItem: BreastfeedingMotherListViewModel.BreastfeedingMotherUI, newItem: BreastfeedingMotherListViewModel.BreastfeedingMotherUI) =
            oldItem.localId == newItem.localId

        override fun areContentsTheSame(oldItem: BreastfeedingMotherListViewModel.BreastfeedingMotherUI, newItem: BreastfeedingMotherListViewModel.BreastfeedingMotherUI) =
            oldItem == newItem
    }
}