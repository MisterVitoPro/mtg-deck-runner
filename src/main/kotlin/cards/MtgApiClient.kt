package cards

import io.magicthegathering.kotlinsdk.api.MtgCardApiClient
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import retrofit2.Response
import java.io.IOException

private val logger = KotlinLogging.logger {}

object MtgApiClient {
    /**
     * Wrapper over MTG Developer's SDK to allow retries
     */
    internal fun getCardsBySetFunc(sCode: String, page: Int): () -> Response<List<MtgCard>> = { MtgCardApiClient.getAllCardsBySetCode(sCode, 100, page) }

    /**
     * Generic caller to the Mtg API with a retry since the API timeouts often.
     *
     * Unable to control the timeouts in the SDK, so doing this for now
     */
    internal fun <T> callMtgAPI(func: () -> Response<T>): Response<T> {
        logger.info { "Fetching Cards from API" }
        return runBlocking {
            return@runBlocking retryIO(10) { func.invoke() }
        }
    }

    /**
     * Retry function for API.
     *
     * Thank you StackOverflow
     */
    private suspend fun <T> retryIO(
            times: Int = Int.MAX_VALUE,
            initialDelay: Long = 100, // 0.1 second
            maxDelay: Long = 2000,    // 2 second(s)
            factor: Double = 2.0,
            block: suspend () -> T): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: IOException) {
                e.printStackTrace()
                // you can log an error here and/or make a more finer-grained
                // analysis of the cause to see if retry is needed
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block() // last attempt
    }
}