package utils

import java.util.concurrent.TimeUnit

/**
 * Creates a string: $n minutes and $x seconds
 */
fun convertTimeToMinutesAndSeconds(milliSeconds: Long): String {
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds))
    return "${TimeUnit.MILLISECONDS.toMinutes(milliSeconds)} minutes and $seconds seconds"
}