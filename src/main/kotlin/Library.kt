import constants.CardType
import data.getCopy
import data.getRandomLand
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Library(var cards: MutableList<Card> = mutableListOf()) {

    init {
        if (cards.isEmpty()) {
            val myList: MutableList<Card> = getCopy()
            val cardCount = HashMap<String, Int>()
            // Random select starting land count
            val range = 0..ThreadLocalRandom.current().nextInt(20, 26)
            for (i in range) {
                cards.add(getRandomLand())
            }

            while (cards.size < 60) {
                val card = getRandomCard(myList)
                cardCount[card.name] = if (!cardCount.containsKey(card.name)) {
                    cards.add(card)
                    1
                } else {
                    val count = cardCount[card.name]
                    if (count?.compareTo(4)!! < 0) {
                        cards.add(card)
                        count.plus(1)
                    } else {
                        myList.remove(card)
                        count
                    }
                }
            }
            shuffle()
        }
    }

    /**
     * @return If there are 4 or less cards in the deck
     */
    private fun checkIfCardCountLegality(cardName: String): Boolean {
        return checkIfCardCountLegality(cards, cardName)
    }

    fun addCardLegally(card: Card): Boolean {
        return if(checkIfCardCountLegality(card.name)) cards.add(card) else false
    }

    companion object{
        /**
         * @return If there are 4 or less cards in the deck
         */
        fun checkIfCardCountLegality(cardList: MutableList<Card>, cardName: String): Boolean {
            val cardCount: Int? = cardList.groupingBy { it.name }.eachCount()[cardName]
            return cardCount == null || cardCount < 4
        }
    }


    fun draw(): Card {
        return cards.removeAt(0)
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun print(){
        println(getPrintableLibrary())
    }

    fun getPrintableLibrary(): String {
        val cardNonLandCounts = this.nonLands().groupingBy { it.name }.eachCount()
        val cardLandCounts = this.lands().groupingBy { it.name }.eachCount()

        fun sortedCountsPrettyPrint(cardCounts: Map<String, Int>): String {
            return cardCounts.toSortedMap()
                    .map { (t, u) -> "${cards.find{ c -> c.name == t }!!.printShort()}: $u" }
                    .joinToString(separator = "") {"$it\n"}
        }

        return StringBuilder()
                .appendLine("----- Creatures / Spells -----")
                .append(sortedCountsPrettyPrint(cardNonLandCounts))
                .appendLine("----- Lands -----")
                .append(sortedCountsPrettyPrint(cardLandCounts)).toString()
    }

    fun lands(): List<Card> {
        return cards.filter { card -> card.type == CardType.LAND }
    }

    fun nonLands(): List<Card> {
        return cards.filter { card -> card.type != CardType.LAND }
    }
}

fun getRandomCard(myList: MutableList<Card>): Card {
    return myList[ThreadLocalRandom.current().nextInt(myList.size)]
}

