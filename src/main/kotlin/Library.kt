import cards.CardFetcherUtils.filterCardsByColor
import cards.CardFetcherUtils.getCardByName
import cards.callGetCardsBySet
import constants.CardType
import constants.Color
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import utils.CardUtil.getRandomCard
import utils.CardUtil.printableDataShort
import java.lang.Math.random
import java.util.concurrent.ThreadLocalRandom

class Library(var cards: MutableList<MtgCard> = mutableListOf(), val color: Color) {

    init {
        // Create a new deck
        if (cards.isEmpty()) {
            val myCardList: MutableList<MtgCard> = filterCardsByColor(color, callGetCardsBySet(configs.mtgSet)).toMutableList()
            // Randomly select number of lands in deck
            val range = 0..ThreadLocalRandom.current().nextInt(21, 26)
            for (i in range) {
                cards.add(getCardByName("Mountain"))
            }

            // Populate the rest of the deck with non-land cards
            // Give a probability to add a duplicate card since most decks
            // do not comprise of a ton of 1 off cards
            var count = 0
            val maxTries = 100
            var previousCard: MtgCard = getRandomCard(myCardList)
            while (cards.size < 60) {
                val card = if (random() < 0.3) previousCard else getRandomCard(myCardList)
                previousCard = if (!addCardLegally(card)) {
                    count += 1
                    myCardList.remove(card)
                    getRandomCard(myCardList)
                } else {
                    card
                }
            }
            if (count >= maxTries) throw Exception("Too many tries to add cards. Rethink this shit!")
        }
    }

    /**
     * @return True, if there are 4 or less cards in the deck
     */
    private fun checkIfCardCountLegality(cardName: String): Boolean {
        return checkIfCardCountLegality(cards, cardName)
    }

    /**
     * @return True, if a card was successfully added to the library
     */
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

    /**
     * From the library, get all cards that have the type 'land' in its types
     */
    fun lands(): List<MtgCard> {
        return cards.filter { card -> card.types.map { it.toLowerCase() }.contains(CardType.LAND.name.toLowerCase()) }
    }

    /**
     * From the library, get all cards that do NOT have the type 'land' in its types
     */
    fun nonLands(): List<MtgCard> {
        return cards.filter { card -> !card.types.map { it.toLowerCase() }.contains(CardType.LAND.name.toLowerCase()) }
    }

    /**
     * @return String of a pretty formatted card list
     */
    override fun toString(): String {
        val cardNonLandCounts = this.nonLands().groupingBy { it.name }.eachCount()
        val cardLandCounts = this.lands().groupingBy { it.name }.eachCount()

        fun sortedCountsPrettyPrint(cardCounts: Map<String, Int>): String {
            return cardCounts.toSortedMap()
                    .map { (t, u) -> "${u}x ${cards.find { c -> c.name == t }!!.printableDataShort()}" }
                    .joinToString(separator = "") { "$it\n" }
        }

        return StringBuilder()
                .appendLine("----- Creatures / Spells (${cardNonLandCounts.size}) -----")
                .append(sortedCountsPrettyPrint(cardNonLandCounts))
                .appendLine("----- Lands (${cardLandCounts.size}) -----")
                .append(sortedCountsPrettyPrint(cardLandCounts)).toString()
    }

}
