package app.ikd9684.android.study.blackjack_sample.models

import app.ikd9684.android.study.blackjack_sample.logic.BlackJack.Status
import app.ikd9684.android.study.commons.models.Card
import app.ikd9684.android.study.commons.models.Player

open class BJPlayer(name: String) : Player(name) {

    companion object {
        var debug = false
    }

    private val cardsImpl = mutableListOf<Card>()
    val cards: List<Card>
        get() = cardsImpl

    val count: Int
        get() {
            var count = 0

            cardsImpl.filter { 1 < it.number }.onEach { card ->
                count += if (card.number in 11..13) {
                    10
                } else {
                    card.number
                }
            }
            val numberOfAces = cardsImpl.count { it.number == 1 }
            if (0 < numberOfAces) {
                if (numberOfAces == 1) {
                    count += if (count + 11 <= 21) 11 else 1
                } else {
                    count += ((numberOfAces - 1) * 1)
                    count += if (count + 11 <= 21) 11 else 1
                }
            }

            return count
        }

    val isBust: Boolean
        get() = 21 < count

    val isBlackJack: Boolean
        get() = count == 21

    val isNaturalBlackJack: Boolean
        get() = isBlackJack && cardsImpl.size == 2

    val status: Status
        get() = when {
            isBust -> Status.Bust
            isNaturalBlackJack -> Status.NaturalBlackJack
            isBlackJack -> Status.BlackJack
            else -> Status.None
        }

    var numberOfWins = 0
        private set
    var numberOfLosses = 0
        private set
    var numberOfDraws = 0
        private set

    fun plusNumberOfWins() {
        numberOfWins++
    }

    fun plusNumberOfLosses() {
        numberOfLosses++
    }

    fun plusNumberOfDraws() {
        numberOfDraws++
    }

    fun reset() {
        cardsImpl.clear()
    }

    fun deal(card: Card) {
        cardsImpl.add(card)
    }

    fun allOpen() {
        cardsImpl.forEach {
            it.isDown = false
        }
    }

    override fun toString(): String {
        val isDown = debug.not() && cardsImpl.any { it.isDown }
        val statusStr = if (isDown) "?" else if (status == Status.None) "" else status.name
        val countStr = if (isDown) "?" else "$count"
        return "$name($statusStr count=$countStr, cards=$cardsImpl)"
    }
}
