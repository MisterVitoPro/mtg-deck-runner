package data

import Card
import constants.CardType
import constants.Color

val AlphaSet: List<Card> = arrayListOf(
        Card("Dragon Whelp", CardType.CREATURE, Color.RED,4, 2,3),
        Card("Dwarven Demolition Team", CardType.CREATURE, Color.RED, 3, 1,1),
        Card("Dwarven Warriors", CardType.CREATURE, Color.RED, 3, 1,1),
        Card("Earth Elemental", CardType.CREATURE, Color.RED,5, 4,5),
        Card("Fire Elemental", CardType.CREATURE, Color.RED,5, 5,4),
        Card("Goblin Balloon Brigade", CardType.CREATURE, Color.RED,1, 1,1),
        Card("Goblin King", CardType.CREATURE, Color.RED,3, 2,2),
        Card("Granite Gargoyle", CardType.CREATURE, Color.RED,3, 2,2),
        Card("Lightning Bolt", CardType.INSTANT, Color.RED,1, 0,0)
)
