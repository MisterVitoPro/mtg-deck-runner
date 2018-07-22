import constants.CardType
import constants.Color
import data.getCopy
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Library {

    var cards: ArrayList<Card> = ArrayList()

    fun create() {
        var myList: MutableList<Card> = getCopy()
        val cardCount = HashMap<String, Int>()
        val range = 0..ThreadLocalRandom.current().nextInt(17, 27)
        for (i in range) {
            cards.add(Card("Mountain", CardType.LAND, Color.LAND))
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
        val frequenciesByFirstChar = cards.groupingBy { it.name }.eachCount()
        println(frequenciesByFirstChar)

        Collections.shuffle(cards)
    }

    fun draw(): Card {
        return cards.removeAt(0)
    }
}

fun getRandomCard(myList: MutableList<Card>): Card {
    return myList[ThreadLocalRandom.current().nextInt(myList.size)]
}