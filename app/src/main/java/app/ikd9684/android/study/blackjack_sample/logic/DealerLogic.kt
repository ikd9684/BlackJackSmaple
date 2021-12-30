package app.ikd9684.android.study.blackjack_sample.logic

import app.ikd9684.android.study.blackjack_sample.models.BJPlayer

interface DealerLogic {

    fun compute(
        dealer: BJPlayer,
        players: List<BJPlayer>,
        doHit: () -> Unit,
        doStand: () -> Unit
    )
}
