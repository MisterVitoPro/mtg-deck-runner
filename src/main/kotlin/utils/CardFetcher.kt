package utils

import constants.Color
import io.magicthegathering.kotlinsdk.api.MtgCardApiClient
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import retrofit2.Response
import java.io.IOException

private val logger = KotlinLogging.logger {}

val masterCardCatalog: HashMap<String, MutableList<MtgCard>> = hashMapOf()

private val masterCardCatalogList: List<MtgCard>
    get() {
        return masterCardCatalog.flatMap { it.value }
    }

object CardFetcherUtils {

    fun filterCardsByColor(color: Color, cards: List<MtgCard>): List<MtgCard> {
        return cards.filter { it.colors != null && it.colors!!.map { c -> c.toLowerCase() }.contains(color.name.toLowerCase()) }
    }

    fun getCardByName(name: String): MtgCard {
        return masterCardCatalogList.first { it.name.equals(name, true) }
    }

}

fun callGetCardsBySet(sCode: String): List<MtgCard> {
    if (masterCardCatalog.containsKey(sCode)) return masterCardCatalog[sCode]!!
    for (x in 0..3) {
        val body = callMtgAPI(getCardsBySetFunc(sCode, x)).body()
                ?: error("Unable to fetch body for utils.callGetCardsBySet [$sCode]")
        if (!masterCardCatalog.containsKey(sCode)) {
            masterCardCatalog[sCode] = body.toMutableList()
        } else {
            masterCardCatalog[sCode]!!.addAll(body)
        }
    }
    return masterCardCatalog[sCode]!!
}

private fun getCardsBySetFunc(sCode: String, page: Int): () -> Response<List<MtgCard>> = { MtgCardApiClient.getAllCardsBySetCode(sCode, 100, page) }

private fun <T> callMtgAPI(func: () -> Response<T>): Response<T> {
    logger.info { "Fetching Cards from API" }
    return runBlocking {
        return@runBlocking retryIO(10) { func.invoke() }
    }
}

private suspend fun <T> retryIO(
        times: Int = Int.MAX_VALUE,
        initialDelay: Long = 100, // 0.1 second
        maxDelay: Long = 1000,    // 1 second
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