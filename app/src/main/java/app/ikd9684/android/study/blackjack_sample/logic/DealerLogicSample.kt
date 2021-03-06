package app.ikd9684.android.study.blackjack_sample.logic

import app.ikd9684.android.study.blackjack_sample.models.BJPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DealerLogicSample : DealerLogic {

    override fun compute(
        dealer: BJPlayer,
        players: List<BJPlayer>,
        doHit: () -> Unit,
        doStand: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000) // それっぽい待ち時間

            players.mapNotNull {
                it.validMaxCount
            }.maxOrNull()?.let { playersValidMaxCount ->
                dealer.validMaxCount?.let {
                    var dealerValidMaxCount = it

                    var firstTime = true
                    while (dealerValidMaxCount < 17 || dealerValidMaxCount < playersValidMaxCount) {
                        if (firstTime.not()) {
                            delay(1000) // それっぽい待ち時間
                        }
                        firstTime = false

                        doHit()

                        dealerValidMaxCount = dealer.validMaxCount ?: break
                    }
                }
            }
            doStand()
        }
    }
}
