package cards

import cards.CardIOUtils.writeMtgCardToYaml
import cards.MtgApiClient.callMtgAPI
import cards.MtgApiClient.getCardsBySetFunc
import com.charleskorn.kaml.Yaml
import com.google.gson.Gson
import configs
import constants.Color
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import java.io.File
import java.net.URLEncoder

//Every card fetched and stored by Set Code as its key
val masterCardCatalog: HashMap<String, MutableList<MtgCard>> = hashMapOf()

// Flat map of cards from masterCardCatalog
private val masterCardCatalogList: List<MtgCard>
    get() {
        return masterCardCatalog.flatMap { it.value }
    }

/**
 * Need to fetch cards by Color, Set, Name, etc from our cached list
 */
object CardFetcherUtils {

    fun filterCardsByColor(color: Color, cards: List<MtgCard> = masterCardCatalogList): List<MtgCard> {
        return cards.filter { it.colors != null && it.colors!!.map { c -> c.toLowerCase() }.contains(color.name.toLowerCase()) }
    }

    fun getCardByName(name: String): MtgCard {
        return masterCardCatalogList.first { it.name.equals(name, true) }
    }

}

object CardIOUtils {

    private const val CARD_DIRECTORY = "cards"

    private val gson = Gson()

    fun initializeCardsFromFiles() {
        File(CARD_DIRECTORY).mkdirs()

        val f = File(CARD_DIRECTORY)
        val files = f.listFiles()

        if (files != null) for (i in files.indices) {
            val mtgCard: MtgCard = readMtgCardFromYaml(files[i])!!
            if (!masterCardCatalog.containsKey(mtgCard.set)) {
                masterCardCatalog[mtgCard.set] = mutableListOf()
            }
            masterCardCatalog[mtgCard.set]!!.add(mtgCard)
        }
    }

    fun writeMtgCardToYaml(mtgCard: MtgCard) {
        val json: String = gson.toJson(mtgCard)
        val serializedCard = gson.fromJson(json, SerializedMtgCard::class.java)
        val yamlString = Yaml.default.encodeToString(SerializedMtgCard.serializer(), serializedCard)
        File("$CARD_DIRECTORY/${URLEncoder.encode(mtgCard.name, "utf-8")}.yaml").writeText(yamlString)
    }

    private fun readMtgCardFromYaml(yFile: File): MtgCard? {
        val file = yFile.readText()
        val serializedMtg = Yaml.default.decodeFromString(SerializedMtgCard.serializer(), file)
        val json: String = gson.toJson(serializedMtg)
        return gson.fromJson(json, MtgCard::class.java)
    }
    
}

// Need to add a list of cards that cannot be ran by AI from Forge
val EXCLUDE_AI_LIST: List<String> = listOf("Raging River")

fun callGetCardsBySet(sCode: String): List<MtgCard> {
    if (masterCardCatalog.containsKey(sCode)) return masterCardCatalog[sCode]!!
    for (x in 1..3) {
        val body = callMtgAPI(getCardsBySetFunc(sCode, x)).body()
                ?: error("Unable to fetch body for cards.callGetCardsBySet [$sCode]")

        // Filter out cards that cannot be used by AI and if there is a format, filter for Legal only
        var filteredBody = body.filter { !EXCLUDE_AI_LIST.contains(it.name) }
        if (configs.format.isNotEmpty() && configs.format != "none") {
            filteredBody = filteredBody.filter { c -> c.legalities!!.any { it.format.equals(configs.format, true) && it.legality == "Legal" } }
        }

        if (!masterCardCatalog.containsKey(sCode)) {
            masterCardCatalog[sCode] = filteredBody.toMutableList()
        } else {
            masterCardCatalog[sCode]!!.addAll(filteredBody)
        }

        // Write all cards to files
        filteredBody.forEach {
            writeMtgCardToYaml(it)
        }
    }
    return masterCardCatalog[sCode]!!
}