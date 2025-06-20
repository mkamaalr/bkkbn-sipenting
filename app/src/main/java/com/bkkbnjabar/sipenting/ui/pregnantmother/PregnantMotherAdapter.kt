package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.databinding.ItemPregnantMotherBinding

class PregnantMotherAdapter(
    private val onItemClick: (PregnantMotherRegistrationData) -> Unit
) : ListAdapter<PregnantMotherRegistrationData, PregnantMotherAdapter.PregnantMotherViewHolder>(PregnantMotherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PregnantMotherViewHolder {
        val binding = ItemPregnantMotherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PregnantMotherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PregnantMotherViewHolder, position: Int) {
        val pregnantMother = getItem(position)
        holder.bind(pregnantMother)
    }

    inner class PregnantMotherViewHolder(private val binding: ItemPregnantMotherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(pregnantMother: PregnantMotherRegistrationData) {
            binding.tvName.text = pregnantMother.name ?: "Nama Tidak Tersedia"
            binding.tvNik.text = "NIK: ${pregnantMother.nik ?: "Tidak Tersedia"}"

            val locationBuilder = StringBuilder()
            pregnantMother.kelurahanName?.let { locationBuilder.append("Kelurahan: $it") }
            if (!pregnantMother.rwName.isNullOrEmpty()) {
                if (locationBuilder.isNotEmpty()) locationBuilder.append(", ")
                locationBuilder.append("RW: ${pregnantMother.rwName}")
            }
            if (!pregnantMother.rtName.isNullOrEmpty()) {
                if (locationBuilder.isNotEmpty()) locationBuilder.append(", ")
                locationBuilder.append("RT: ${pregnantMother.rtName}")
            }
            binding.tvLocation.text = locationBuilder.toString().ifEmpty { "Lokasi tidak tersedia" }

            val syncIconRes = when (pregnantMother.syncStatus) {
                SyncStatus.PENDING_UPLOAD -> R.drawable.ic_sync_pending
                SyncStatus.ERROR_UPLOAD -> R.drawable.ic_sync_error
                SyncStatus.UPLOADED -> R.drawable.ic_sync_done
            }
            binding.ivSyncStatus.setImageResource(syncIconRes)
        }
    }

    private class PregnantMotherDiffCallback : DiffUtil.ItemCallback<PregnantMotherRegistrationData>() {
        override fun areItemsTheSame(oldItem: PregnantMotherRegistrationData, newItem: PregnantMotherRegistrationData): Boolean {
            return oldItem.localId == newItem.localId
        }

        override fun areContentsTheSame(oldItem: PregnantMotherRegistrationData, newItem: PregnantMotherRegistrationData): Boolean {
            // PERBAIKAN: Menggunakan nama metode yang benar 'areContentsTheSame'
            return oldItem == newItem
        }
    }
}
