package com.bkkbnjabar.sipenting.data.model.breastfeedingmother

import androidx.room.Embedded
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity

data class BreastfeedingMotherWithLatestStatus(
    @Embedded
    val mother: BreastfeedingMotherEntity,
    val latestVisitDate: String?,
    val isAsiExclusive: Boolean?
)