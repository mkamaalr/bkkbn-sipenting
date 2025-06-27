package com.bkkbnjabar.sipenting.ui.pregnantmother.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.databinding.ItemVisitHistoryBinding

class VisitHistoryAdapter(
    private val onItemClick: (PregnantMotherVisitsEntity) -> Unit
) : ListAdapter<PregnantMotherVisitsEntity, VisitHistoryAdapter.VisitViewHolder>(DiffCallback()) {

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
        fun bind(visit: PregnantMotherVisitsEntity, visitNumber: Int) {
            binding.tvVisitTitle.text = "Kunjungan ke-${visitNumber}"
            binding.tvVisitDate.text = visit.visitDate
            val summary = "Usia Hamil: ${visit.pregnancyWeekAge ?: '-'} mg, BB: ${visit.currentWeight ?: '-'} kg"
            binding.tvVisitSummary.text = summary
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PregnantMotherVisitsEntity>() {
        override fun areItemsTheSame(oldItem: PregnantMotherVisitsEntity, newItem: PregnantMotherVisitsEntity) =
            oldItem.localVisitId == newItem.localVisitId

        override fun areContentsTheSame(oldItem: PregnantMotherVisitsEntity, newItem: PregnantMotherVisitsEntity) =
            oldItem == newItem
    }
}