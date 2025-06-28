package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.databinding.ItemPregnantMotherBinding

/**
 * Adapter untuk RecyclerView yang menampilkan daftar ibu hamil.
 */
class PregnantMotherAdapter(
    private val onItemClicked: (Int) -> Unit // Pass the mother's localId instead of the whole entity
) : ListAdapter<PregnantMotherListViewModel.PregnantMotherUI, PregnantMotherAdapter.MotherViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotherViewHolder {
        val binding = ItemPregnantMotherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MotherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MotherViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentItem.localId)
        }
        holder.bind(currentItem)
    }

    class MotherViewHolder(private val binding: ItemPregnantMotherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(motherUI: PregnantMotherListViewModel.PregnantMotherUI) {
            binding.apply {
                tvMotherName.text = motherUI.name
                tvMotherNik.text = "NIK: ${motherUI.nik}"
                tvMotherStatus.text = "Status: ${motherUI.statusName}"
                tvMotherVisitDetails.text = motherUI.details

                val syncIconRes = when (motherUI.syncStatus) {
                    SyncStatus.PENDING -> R.drawable.ic_sync_pending
                    SyncStatus.DONE -> R.drawable.ic_sync_done
                    SyncStatus.ERROR -> R.drawable.ic_sync_error
                }
                ivSyncStatus.setImageResource(syncIconRes)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PregnantMotherListViewModel.PregnantMotherUI>() {
        override fun areItemsTheSame(oldItem: PregnantMotherListViewModel.PregnantMotherUI, newItem: PregnantMotherListViewModel.PregnantMotherUI) =
            oldItem.localId == newItem.localId

        override fun areContentsTheSame(oldItem: PregnantMotherListViewModel.PregnantMotherUI, newItem: PregnantMotherListViewModel.PregnantMotherUI) =
            oldItem == newItem
    }
}
