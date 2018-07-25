package data

import Card
import constants.CardType
import constants.Color
import java.util.concurrent.ThreadLocalRandom

val ALPHA_SET: List<Card> = arrayListOf(
        Card("Dragon Whelp", CardType.CREATURE, Color.RED, 4, 2, 3),
        Card("Dwarven Demolition Team", CardType.CREATURE, Color.RED, 3, 1, 1),
        Card("Dwarven Warriors", CardType.CREATURE, Color.RED, 3, 1, 1),
        Card("Earth Elemental", CardType.CREATURE, Color.RED, 5, 4, 5),
        Card("Fire Elemental", CardType.CREATURE, Color.RED, 5, 5, 4),
        Card("Goblin Balloon Brigade", CardType.CREATURE, Color.RED, 1, 1, 1),
        Card("Goblin King", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Granite Gargoyle", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Gray Ogre", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Hill Giant", CardType.CREATURE, Color.RED, 4, 3, 3),
        Card("Hurloon Minotaur", CardType.CREATURE, Color.RED, 3, 2, 3),
        Card("Ironclaw Orcs", CardType.CREATURE, Color.RED, 2, 2, 2),
        Card("Lightning Bolt", CardType.INSTANT, Color.RED, 1, 3),
        Card("Mons's Goblin Raiders", CardType.CREATURE, Color.RED, 1, 1, 1),
        Card("Orcish Artillery", CardType.CREATURE, Color.RED, 3, 1, 3),
        Card("Roc of Kher Ridges", CardType.CREATURE, Color.RED, 4, 3, 3),
        Card("Sedge Troll", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Shivan Dragon", CardType.CREATURE, Color.RED, 6, 5, 5),
        Card("Stone Giant", CardType.CREATURE, Color.RED, 4, 3, 4),
        Card("Two-Headed Giant of Foriys", CardType.CREATURE, Color.RED, 5, 4, 4),
        Card("Uthden Troll", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Mountain", CardType.LAND, Color.LAND)
)

fun getCopy(): MutableList<Card> {
    return ALPHA_SET.toMutableList()
}

fun getRandomLand(): Card {
    return Card("Mountain", CardType.LAND, Color.LAND)
}

fun getRandomCard(): Card {
    return ALPHA_SET[ThreadLocalRandom.current().nextInt(ALPHA_SET.size)]
}
