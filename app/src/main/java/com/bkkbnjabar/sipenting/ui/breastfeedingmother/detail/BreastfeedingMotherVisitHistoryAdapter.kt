package com.bkkbnjabar.sipenting.ui.breastfeedingmother.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.databinding.ItemVisitHistoryBinding

class BreastfeedingMotherVisitHistoryAdapter(
    private val onItemClick: (BreastfeedingMotherVisitsEntity) -> Unit
) : ListAdapter<BreastfeedingMotherVisitsEntity, BreastfeedingMotherVisitHistoryAdapter.VisitViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val binding = ItemVisitHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VisitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
        // Since the list is ordered new to old, we reverse the index for display.
        val visitNumber = itemCount - position
        holder.bind(currentItem, visitNumber)
    }

    class VisitViewHolder(private val binding: ItemVisitHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        // Updated bind function to accept the visit number
        fun bind(visit: BreastfeedingMotherVisitsEntity, visitNumber: Int) {
            binding.tvVisitTitle.text = "Kunjungan ke-${visitNumber}"
            binding.tvVisitDate.text = visit.visitDate
            val summary = ""
            binding.tvVisitSummary.text = summary
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BreastfeedingMotherVisitsEntity>() {
        override fun areItemsTheSame(oldItem: BreastfeedingMotherVisitsEntity, newItem: BreastfeedingMotherVisitsEntity) =
            oldItem.localVisitId == newItem.localVisitId

        override fun areContentsTheSame(oldItem: BreastfeedingMotherVisitsEntity, newItem: BreastfeedingMotherVisitsEntity) =
            oldItem == newItem
    }
}