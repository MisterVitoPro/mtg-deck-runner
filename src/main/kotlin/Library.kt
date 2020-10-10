import constants.CardType
import constants.Color
import constants.LIMITED_EDITION_ALPHA
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import utils.CardFetcherUtils.filterCardsByColor
import utils.CardFetcherUtils.getCardByName
import utils.CardUtil.getRandomCard
import utils.CardUtil.printableDataShort
import utils.callGetCardsBySet
import java.util.concurrent.ThreadLocalRandom

class Library(var cards: MutableList<MtgCard> = mutableListOf()) {

    init {
        if (cards.isEmpty()) {
            val myCardList: MutableList<MtgCard> = filterCardsByColor(Color.RED, callGetCardsBySet(LIMITED_EDITION_ALPHA)).toMutableList()
            // Random select starting land count
            val range = 0..ThreadLocalRandom.current().nextInt(20, 26)
            for (i in range) {
                cards.add(getCardByName("Mountain"))
            }

            var count = 0
            val maxTries = 100
            while (cards.size < 60) {
                val card = getRandomCard(myCardList)
                if (!addCardLegally(card)) {
                    count += 1
                    myCardList.remove(card)
                }
            }
            if (count >= maxTries) throw Exception("Too many tries to add cards. Rethink this shit!")
            shuffle()
        }
    }

    /**
     * @return If there are 4 or less cards in the deck
     */
    private fun checkIfCardCountLegality(cardName: String): Boolean {
        return checkIfCardCountLegality(cards, cardName)
    }

    fun addCardLegally(card: MtgCard): Boolean {
        return if (checkIfCardCountLegality(card.name)) cards.add(card) else false
    }

    companion object {
        /**
         * @return If there are 4 or less cards in the deck
         */
        fun checkIfCardCountLegality(cardList: MutableList<MtgCard>, cardName: String): Boolean {
            val cardCount: Int? = cardList.groupingBy { it.name }.eachCount()[cardName]
            return cardCount == null || cardCount < 4
        }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun print() {
        println(getPrintableLibrary())
    }

    fun getPrintableLibrary(): String {
        val cardNonLandCounts = this.nonLands().groupingBy { it.name }.eachCount()
        val cardLandCounts = this.lands().groupingBy { it.name }.eachCount()

        fun sortedCountsPrettyPrint(cardCounts: Map<String, Int>): String {
            return cardCounts.toSortedMap()
                    .map { (t, u) -> "${u}x ${cards.find { c -> c.name == t }!!.printableDataShort()}" }
                    .joinToString(separator = "") { "$it\n" }
        }

        return StringBuilder()
                .appendLine("----- Creatures / Spells -----")
                .append(sortedCountsPrettyPrint(cardNonLandCounts))
                .appendLine("----- Lands -----")
                .append(sortedCountsPrettyPrint(cardLandCounts)).toString()
    }

    fun lands(): List<MtgCard> {
        return cards.filter { card -> card.types.map { it.toLowerCase() }.contains(CardType.LAND.name.toLowerCase()) }
    }

    fun nonLands(): List<MtgCard> {
        return cards.filter { card -> !card.types.map { it.toLowerCase() }.contains(CardType.LAND.name.toLowerCase()) }
    }
}
