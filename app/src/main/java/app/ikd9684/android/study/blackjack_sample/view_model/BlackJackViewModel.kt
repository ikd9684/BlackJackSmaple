package app.ikd9684.android.study.blackjack_sample.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.ikd9684.android.study.blackjack_sample.logic.BlackJack
import app.ikd9684.android.study.blackjack_sample.model.Card

class BlackJackViewModel : ViewModel() {

    private val blackJack = BlackJack(
        onHitPlayer = { player, players ->
            playerImpl.postValue(player)
            playersImpl.postValue(players)
        },
        onStandPlayer = { player, players ->
            playerImpl.postValue(player)
            playersImpl.postValue(players)
        },
        onHitDealer = { dealer ->
            dealerImpl.postValue(dealer)
        },
        onStandDealer = { dealer ->
            dealerImpl.postValue(dealer)
        },
        onChangeTurn = { turn ->
            turnImpl.postValue(turn)
        },
        onPlayCompletion = { result ->
            val player1Name = players.value?.firstOrNull()?.name ?: ""
            when {
                result.winners.any { it.name == player1Name } -> numberOfWins++
                result.losers.any { it.name == player1Name } -> numberOfLosses++
                else -> numberOfDraws++
            }

            resultImpl.postValue(result)
        },
        onResetCards = { cards ->
            resetCardsImpl.postValue(cards)
        },
    )

    private val dealerImpl = MutableLiveData<BlackJack.BJPlayer>()
    val dealer: LiveData<BlackJack.BJPlayer>
        get() = dealerImpl

    private val playerImpl = MutableLiveData<BlackJack.BJPlayer>()
    val player: LiveData<BlackJack.BJPlayer>
        get() = playerImpl

    private val playersImpl = MutableLiveData<List<BlackJack.BJPlayer>>()
    val players: LiveData<List<BlackJack.BJPlayer>>
        get() = playersImpl

    private val turnImpl = MutableLiveData<BlackJack.BJPlayer>()
    val turn: LiveData<BlackJack.BJPlayer>
        get() = turnImpl

    private val resultImpl = MutableLiveData<BlackJack.BJJudgement.BJResult>()
    val result: LiveData<BlackJack.BJJudgement.BJResult>
        get() = resultImpl

    val cards: List<Card>
        get() = blackJack.cards

    private val resetCardsImpl = MutableLiveData<List<Card>>()
    val resetCards: LiveData<List<Card>>
        get() = resetCardsImpl

    var dealerName: String
        set(value) {
            blackJack.dealerName = value
        }
        get() = blackJack.dealerName

    var numberOfWins = 0
        private set
    var numberOfLosses = 0
        private set
    var numberOfDraws = 0
        private set

    fun startNewGame(playersNameList: List<String>) {
        numberOfWins = 0
        numberOfLosses = 0
        numberOfDraws = 0

        blackJack.startNewGame(playersNameList)
    }

    fun startNextPlay() {
        blackJack.startNextPlay()
    }

    fun hit() {
        blackJack.hit()
    }

    fun stand() {
        blackJack.stand()
    }
}
