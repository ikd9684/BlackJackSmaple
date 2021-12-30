package app.ikd9684.android.study.blackjack_sample.logic

import app.ikd9684.android.study.blackjack_sample.models.BJPlayer

interface BJJudgement {

    data class BJResult(
        val winners: List<BJPlayer>,
        val losers: List<BJPlayer>,
        val draws: List<BJPlayer>,
    )

    fun judge(
        dealer: BJPlayer,
        players: List<BJPlayer>,
        completion: (result: BJResult) -> Unit
    )
}
