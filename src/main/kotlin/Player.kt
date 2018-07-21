class Player {

    var life: Int = 20
    var manaPool: Int = 0
    var hand: MutableList<Card> = mutableListOf()
    val library: Library = Library()
    private val graveyard: MutableList<Card> = mutableListOf()

    init {
        library.create()
    }

    fun drawFromLibrary(){
        hand.add(library.draw())
    }

    fun discard(card: Card){
        hand.remove(card)
        graveyard.add(card)
    }

}