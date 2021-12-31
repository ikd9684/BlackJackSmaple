package app.ikd9684.android.study.blackjack_sample.models

import app.ikd9684.android.study.blackjack_sample.logic.BlackJack
import app.ikd9684.android.study.commons.models.Card

class BJHand {

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

    val status: BlackJack.Status
        get() = when {
            isBust -> BlackJack.Status.Bust
            isNaturalBlackJack -> BlackJack.Status.NaturalBlackJack
            isBlackJack -> BlackJack.Status.BlackJack
            else -> BlackJack.Status.None
        }

    val hasDown: Boolean
        get() = cardsImpl.any { it.isDown }

    val hasNotDown: Boolean
        get() = hasDown.not()

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
}