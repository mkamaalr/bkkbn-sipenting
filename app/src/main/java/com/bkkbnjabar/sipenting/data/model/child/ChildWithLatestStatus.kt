package com.bkkbnjabar.sipenting.data.model.child

import androidx.room.Embedded
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity

/**
 * A data class to hold the combined result of a ChildEntity
 * and the status ID from her most recent visit.
 */
data class ChildWithLatestStatus(
    @Embedded
    val child: ChildEntity,

    val pregnantMotherStatusId: Int?,
    val nextVisitDate: String?, // Also fetching the next visit date is useful for the list
)