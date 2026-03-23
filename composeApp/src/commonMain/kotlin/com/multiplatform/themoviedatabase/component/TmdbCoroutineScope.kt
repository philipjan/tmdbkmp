package com.multiplatform.themoviedatabase.component

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class TmdbCoroutineScope(
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    val useSuperVisorJob: Boolean = false
) : CoroutineScope {
    private val job = Job()
    private val superVisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = dispatcher + if (useSuperVisorJob) superVisorJob else job

    fun cancel() {
        if (useSuperVisorJob) {
            superVisorJob.cancel()
        } else {
            job.cancel()
        }
    }

    val isActive: Boolean get() = if (useSuperVisorJob) superVisorJob.isActive else job.isActive
}