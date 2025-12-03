package com.worker.status

import kotlinx.coroutines.flow.Flow

/**
 * Reports on if work manager is in progress
 */
interface WorkStatusManager {
    val isRunning: Flow<Boolean>
    fun run()
    fun cancel()
}