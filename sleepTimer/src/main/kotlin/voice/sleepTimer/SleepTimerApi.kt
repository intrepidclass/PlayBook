package voice.sleepTimer

import kotlinx.coroutines.flow.StateFlow
import voice.common.BookId
import kotlin.time.Duration

interface SleepTimerApi {
    val leftSleepTimeFlow: StateFlow<Duration>
    val sleepAtEocFlow: StateFlow<Boolean>
    fun sleepTimerActive(): Boolean
    fun setActive(enable: Boolean)
    fun setEoc(enable: Boolean, bookId: BookId)
}
