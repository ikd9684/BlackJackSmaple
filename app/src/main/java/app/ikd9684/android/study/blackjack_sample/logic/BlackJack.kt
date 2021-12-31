package app.ikd9684.android.study.blackjack_sample.logic

import app.ikd9684.android.study.blackjack_sample.models.BJPlayer
import app.ikd9684.android.study.commons.models.Card

class BlackJack(
    private val onHitPlayer: (player: BJPlayer, players: List<BJPlayer>) -> Unit,
    private val onStandPlayer: (player: BJPlayer, players: List<BJPlayer>) -> Unit,
    private val onHitDealer: (dealer: BJPlayer) -> Unit,
    private val onStandDealer: (dealer: BJPlayer) -> Unit,
    private val onChangeTurn: (turn: BJPlayer) -> Unit,
    private val onPlayCompletion: (result: BJJudgement.BJResult) -> Unit,
    private val onResetCards: (cards: List<Card>) -> Unit,
    private val judgement: BJJudgement = BJJudgementSample(),
    private val dealerLogic: DealerLogic = DealerLogicSample(),
) {

    enum class Status {
        Bust,
        BlackJack,
        NaturalBlackJack,
        None,
    }

    class NoNewGameException :
        IllegalStateException("No new game have been started")

    private val playersImpl = mutableListOf<BJPlayer>()
    val players: List<BJPlayer>
        get() = playersImpl

    var dealerName: String = "Dealer"
        set(value) {
            // ディーラーの名前の変更はディーラーの入れ替えに相当するので、ゲームを開始したら名前は変えられない
            turn ?: run {
                field = value
                dealerImpl = BJPlayer(value)
            }
        }
    private var dealerImpl = BJPlayer(dealerName)

    private val cardsImpl = mutableListOf<Card>()
    val cards: List<Card>
        get() = cardsImpl

    private var numberOfDeck: Int = 1

    private var needReset = false

    private var turn: BJPlayer? = null

    fun startNewGame(playersNameList: List<String>, numberOfDeck: Int = 1) {
        playersImpl.clear()
        playersNameList.map { name ->
            playersImpl.add(BJPlayer(name))
        }

        this.numberOfDeck = numberOfDeck

        resetCards()

        startNextPlay()
    }

    private fun resetCards() {
        cardsImpl.clear()
        repeat(numberOfDeck) {
            cardsImpl.addAll(Card.newCardList(numberOfJoker = 0))
        }
        cardsImpl.shuffle()

        onResetCards(cardsImpl)
    }

    fun startNextPlay() {
        if (cardsImpl.isEmpty()) {
            throw NoNewGameException()
        }

        if (needReset) {
            resetCards()
        }
        needReset = false

        dealerImpl.reset()
        playersImpl.forEach { player ->
            player.reset()
        }

        val initialTurn = playersImpl.first()
        turn = initialTurn

        repeat(2) { i ->
            // ディーラーのカードは１枚目だけ開く
            dealOutACardTo(dealerImpl, 0 < i)
            onHitDealer(dealerImpl)
            playersImpl.forEach { player ->
                // プレイヤーのカードは全部開く
                dealOutACardTo(player, false)
                onHitPlayer(player, playersImpl)
            }
        }
        onChangeTurn(initialTurn)
    }

    fun hit() {
        val turn = turn ?: run { throw NoNewGameException() }

        dealOutACardTo(turn, false)
        onHitPlayer(turn, playersImpl)

        if (21 < turn.selectedHand.count) {
            // バストしてたら自動的に次のターンに進む
            turnToNext()
        }
    }

    fun stand() {
        val turn = turn ?: run { throw NoNewGameException() }

        onStandPlayer(turn, playersImpl)
        turnToNext()
    }

    private fun computeDealer() {
        dealerLogic.compute(
            dealerImpl,
            playersImpl,
            doHit = {
                dealOutACardTo(dealerImpl, false)
                onHitDealer(dealerImpl)
            },
            doStand = {
                onStandDealer(dealerImpl)
                judge()
            }
        )
    }

    private fun turnToNext() {
        val index = playersImpl.indexOf(turn)
        turn = when (index) {
            -1 -> {
                playersImpl.first()
            }
            playersImpl.size - 1 -> {
                dealerImpl
            }
            else -> {
                playersImpl[index + 1]
            }
        }

        turn?.let { onChangeTurn(it) }

        if (turn == dealerImpl) {
            computeDealer()
        }
    }

    private fun dealOutACardTo(player: BJPlayer, isDown: Boolean): Card? {
        if (player.selectedHand.count < 21) {
            cardsImpl.removeFirstOrNull()?.let { card ->
                card.isDown = isDown
                player.deal(card)
                return card
            }
        }
        return null
    }

    private fun judge() {
        turn ?: run { throw NoNewGameException() }

        dealerImpl.allOpen()

        judgement.judge(dealerImpl, playersImpl) { result ->
            onPlayCompletion(result)
        }

        // 残り枚数が山札総数の半分を下回っていたら、次に始める前に山札をリセットする
        needReset = (cardsImpl.size < (numberOfDeck * 52) / 2)
    }
}