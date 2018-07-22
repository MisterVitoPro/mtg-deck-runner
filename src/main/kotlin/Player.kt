import constants.CardType
import java.util.*

class Player {

    var life: Int = 20
    var manaPool: Int = 0
    var hand: MutableList<Card> = mutableListOf()
    val library: Library = Library()
    private val graveyard: MutableList<Card> = mutableListOf()
    var battleField: MutableList<Card> = mutableListOf()

    init {
        library.create()
    }

    fun drawFromLibrary(n: Int) {
        for (i in 1..n)
            hand.add(library.draw())
        println("Player Hand: ${player.hand.map { card -> card.name }}")
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
        println("Played ${card.name}")
    }

    fun tapLands() {
        manaPool = battleField.stream().filter { c -> c.type == CardType.LAND }.count().toInt()
        println("Player's Mana Pool = $manaPool")
    }
}