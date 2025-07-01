package com.bkkbnjabar.sipenting.ui.child

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.databinding.ItemChildBinding

/**
 * Adapter untuk RecyclerView yang menampilkan daftar ibu hamil.
 */
class ChildAdapter(
    private val onItemClicked: (Int) -> Unit // Pass the mother's localId instead of the whole entity
) : ListAdapter<ChildListViewModel.ChildUI, ChildAdapter.MotherViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotherViewHolder {
        val binding = ItemChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MotherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MotherViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentItem.localId)
        }
        holder.bind(currentItem)
    }

    class MotherViewHolder(private val binding: ItemChildBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(motherUI: ChildListViewModel.ChildUI) {
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

    class DiffCallback : DiffUtil.ItemCallback<ChildListViewModel.ChildUI>() {
        override fun areItemsTheSame(oldItem: ChildListViewModel.ChildUI, newItem: ChildListViewModel.ChildUI) =
            oldItem.localId == newItem.localId

        override fun areContentsTheSame(oldItem: ChildListViewModel.ChildUI, newItem: ChildListViewModel.ChildUI) =
            oldItem == newItem
    }
}
