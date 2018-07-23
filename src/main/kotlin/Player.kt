import constants.CardType
import java.util.*

class Player(name: String, val library: Library = Library()) {

    var life: Int = 20
    var manaPool: Int = 0
    var hand: MutableList<Card> = mutableListOf()
    private val graveyard: MutableList<Card> = mutableListOf()
    var battleField: MutableList<Card> = mutableListOf()

    init {
        //println("- $name Deck -")
        //library.printLibrary()
    }

    fun drawOpeningHand(cardsToDraw: Int){
        if(cardsToDraw > 5 && !library.cards.subList(0, cardsToDraw).stream().allMatch { card -> card.type == CardType.LAND }){
            library.shuffle()
            drawOpeningHand(cardsToDraw-1)
        } else {
            drawOpeningHand(cardsToDraw)
        }
    }

    fun drawFromLibrary(n: Int) {
        for (i in 1..n)
            hand.add(library.draw())
        println("Player Hand: ${hand.map { card -> card.name }.sorted()}")
    }

    fun discard(card: Card) {
        hand.remove(card)
        graveyard.add(card)
    }

    fun playLand() {
        val land: Optional<Card> = getCardByType(CardType.LAND)
        if (land.isPresent) {
            playCard(land.get())
        }
    }

    private fun getCardByType(type: CardType): Optional<Card> {
        return hand.stream()
                .filter { c -> c.type == type }
                .findFirst()
    }

    fun playCard(card: Card) {
        hand.remove(card)
        battleField.add(card)
        println("Played ${card.printShort()}")
    }

    fun tapLands() {
        manaPool = battleField.stream().filter { c -> c.type == CardType.LAND }.count().toInt()
        println("Player's Mana Pool = $manaPool")
    }


}