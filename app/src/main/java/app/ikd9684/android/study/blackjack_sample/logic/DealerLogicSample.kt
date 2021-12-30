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

            players.filter {
                it.count <= 21
            }.maxOfOrNull {
                it.count
            }?.let { playersMaxCount ->
                var firstTime = true
                while (dealer.count < 17 || dealer.count < playersMaxCount) {
                    if (firstTime.not()) {
                        delay(1000) // それっぽい待ち時間
                    }
                    firstTime = false

                    doHit()
                }
            }
            doStand()
        }
    }
}
