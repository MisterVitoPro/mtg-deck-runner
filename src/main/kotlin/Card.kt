import constants.CardType
import constants.Color

class Card(val name: String, val type: CardType, val color: Color, val cost: Int = 0, val attack: Int = 0, val defense: Int = 0) {

    var isTapped: Boolean = false

}