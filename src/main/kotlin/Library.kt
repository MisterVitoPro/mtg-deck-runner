import constants.CardType
import data.getCopy
import data.getRandomLand
import java.io.BufferedWriter
import java.io.File
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.toList

class Library(var cards: MutableList<Card> = mutableListOf()) {

    init {
        if (cards.isEmpty()) {
            val myList: MutableList<Card> = getCopy()
            val cardCount = HashMap<String, Int>()
            val range = 0..ThreadLocalRandom.current().nextInt(17, 27)
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

    fun draw(): Card {
        return cards.removeAt(0)
    }

    fun sortCards() {
        cards.sortBy { it.name }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun print(){
        printCount(cards)
    }

    fun lands(): List<Card> {
        return cards.stream().filter { card -> card.type == CardType.LAND }.toList()
    }

    fun nonLands(): List<Card> {
        return cards.stream().filter { card -> card.type != CardType.LAND }.toList()
    }
}

fun getRandomCard(myList: MutableList<Card>): Card {
    return myList[ThreadLocalRandom.current().nextInt(myList.size)]
}

fun printCount(list: MutableList<Card>) {
    val frequenciesByFirstChar = list.groupingBy { it.name }.eachCount()
    frequenciesByFirstChar.toSortedMap()
            .forEach { t, u ->
                println("$t (${list.find{ c -> c.name == t }!!.cost}): $u")
            }
    print("\n")
}

fun forgeOutput(list: MutableList<Card>, name: String){
    val sb = StringBuilder()
    sb.append("[metadata]\n")
    sb.append("Name=$name\n")
    sb.append("[Main]\n")
    val frequenciesByFirstChar = list.groupingBy { it.name }.eachCount()
    frequenciesByFirstChar.toSortedMap()
            .forEach { name, n ->
                sb.append("$n $name\n")
            }
    val file = File("$name.dck")
    file.writeText(sb.toString())
}

fun csvOut(list: MutableList<Card>){
    val frequenciesByFirstChar = list.groupingBy { it.name }.eachCount()
    frequenciesByFirstChar.toSortedMap()
            .forEach { t, u ->
                println("$t,${list.find{ c -> c.name == t }!!.cost},$u")
            }
}

