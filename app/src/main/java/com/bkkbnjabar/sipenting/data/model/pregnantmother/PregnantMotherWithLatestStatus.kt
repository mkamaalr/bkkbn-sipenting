package com.bkkbnjabar.sipenting.data.model.pregnantmother

import androidx.room.Embedded
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity

/**
 * A data class to hold the combined result of a PregnantMotherEntity
 * and the status ID from her most recent visit.
 */
data class PregnantMotherWithLatestStatus(
    @Embedded
    val mother: PregnantMotherEntity,

    val pregnantMotherStatusId: Int?,
    val nextVisitDate: String?, // Also fetching the next visit date is useful for the list
    val pregnancyWeekAge: Int? // And the current pregnancy age
)