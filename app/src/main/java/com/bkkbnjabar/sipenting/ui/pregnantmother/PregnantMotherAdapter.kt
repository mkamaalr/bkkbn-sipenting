package com.bkkbnjabar.sipenting.ui.pregnantmother

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkkbnjabar.sipenting.R
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData
import com.bkkbnjabar.sipenting.databinding.ItemPregnantMotherBinding

class PregnantMotherAdapter(private val onItemClick: (PregnantMotherRegistrationData) -> Unit) :
    ListAdapter<PregnantMotherRegistrationData, PregnantMotherAdapter.PregnantMotherViewHolder>(PregnantMotherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PregnantMotherViewHolder {
        val binding = ItemPregnantMotherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PregnantMotherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PregnantMotherViewHolder, position: Int) {
        val pregnantMother = getItem(position)
        holder.bind(pregnantMother, onItemClick)
    }

    class PregnantMotherViewHolder(private val binding: ItemPregnantMotherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pregnantMother: PregnantMotherRegistrationData, onItemClick: (PregnantMotherRegistrationData) -> Unit) {
            binding.apply {
                // Contoh: Sesuaikan dengan ID View di layout item Anda (misalnya, R.id.tv_mother_name)
                // Pastikan TextViews ini ada di item_pregnant_mother.xml
                tvName.text = pregnantMother.name ?: "Nama Tidak Tersedia"
                tvNik.text = pregnantMother.nik ?: "NIK Tidak Tersedia"
                tvLocation.text = pregnantMother.syncStatus.name // Menggunakan nama enum untuk status
                // Jika Anda ingin menampilkan createdAt
                // tvCreatedAt.text = pregnantMother.createdAt ?: "Tanggal Pembuatan Tidak Tersedia"

                root.setOnClickListener {
                    onItemClick(pregnantMother)
                }
            }
        }
    }

    private class PregnantMotherDiffCallback : DiffUtil.ItemCallback<PregnantMotherRegistrationData>() {
        override fun areItemsTheSame(oldItem: PregnantMotherRegistrationData, newItem: PregnantMotherRegistrationData): Boolean {
            // Menggunakan localId yang mungkin null, tapi untuk perbandingan ID harus non-null
            return oldItem.localId == newItem.localId
        }

        override fun areContentsTheSame(oldItem: PregnantMotherRegistrationData, newItem: PregnantMotherRegistrationData): Boolean {
            return oldItem == newItem
        }
    }
}
