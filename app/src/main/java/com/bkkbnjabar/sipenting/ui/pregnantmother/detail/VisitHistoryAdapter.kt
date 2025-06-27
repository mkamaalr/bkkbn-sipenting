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
        holder.bind(currentItem)
    }

    class VisitViewHolder(private val binding: ItemVisitHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(visit: PregnantMotherVisitsEntity) {
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
