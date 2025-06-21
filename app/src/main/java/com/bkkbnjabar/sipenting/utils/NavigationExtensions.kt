package com.bkkbnjabar.sipenting.utils

import androidx.navigation.NavController
import androidx.navigation.NavOptions

/**
 * Pops the back stack up to a specific destination and navigates to it inclusively.
 * This is useful for clearing intermediate fragments from the back stack.
 */
fun NavController.popToInclusive(destinationId: Int, inclusive: Boolean) {
    val navOptions = NavOptions.Builder()
        .setPopUpTo(destinationId, inclusive)
        .build()
    navigate(destinationId, null, navOptions)
}
