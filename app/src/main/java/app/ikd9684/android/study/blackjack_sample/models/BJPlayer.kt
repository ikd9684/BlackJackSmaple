package app.ikd9684.android.study.blackjack_sample.models

import app.ikd9684.android.study.blackjack_sample.logic.BlackJack.Status
import app.ikd9684.android.study.commons.models.Card
import app.ikd9684.android.study.commons.models.Player

open class BJPlayer(name: String) : Player(name) {

    companion object {
        var debug = false
    }

    private var handsIndex = 0

    private val handsImpl = mutableListOf<BJHand>()
    val hands: List<BJHand>
        get() = handsImpl

    val selectedHand: BJHand
        get() = handsImpl[handsIndex]

    val validHands: List<BJHand>
        get() = handsImpl.filter { it.isBust.not() }

    val validMaxCount: Int?
        get() = validHands.maxOfOrNull { it.count }

    val isAllBust: Boolean
        get() = handsImpl.all { it.isBust }

    val hasBlackJack: Boolean
        get() = handsImpl.any { it.isBlackJack }

    val hasNaturalBlackJack: Boolean
        get() = handsImpl.any { it.isBlackJack && it.cards.size == 2 }

    var numberOfWins = 0
        private set
    var numberOfLosses = 0
        private set
    var numberOfDraws = 0
        private set

    fun selectNextHand(): Boolean {
        return if (handsIndex < handsImpl.size - 1) {
            handsIndex++
            true
        } else {
            false
        }
    }

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
        handsIndex = 0
        handsImpl.clear()
        handsImpl.add(BJHand())
    }

    fun deal(card: Card) {
        handsImpl[handsIndex].deal(card)
    }

    fun allOpen() {
        handsImpl.forEach {
            it.allOpen()
        }
    }

    override fun toString(): String {
        val result = mutableListOf<String>()
        handsImpl.forEachIndexed { i, bjHand ->
            val isDown = debug.not() && bjHand.hasDown
            val status = bjHand.status
            val statusStr = if (isDown) "?" else if (status == Status.None) "" else status.name
            val countStr = if (isDown) "?" else "$bjHand.count"
            result.add("$name(hand#$i: $statusStr count=$countStr, cards=${bjHand.cards})")
        }
        return result.joinToString(", ")
    }
}
