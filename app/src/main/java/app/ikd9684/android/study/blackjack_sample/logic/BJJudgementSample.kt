package app.ikd9684.android.study.blackjack_sample.logic

import app.ikd9684.android.study.blackjack_sample.models.BJPlayer

open class BJJudgementSample : BJJudgement {
    // ブラックジャックではプレイヤー同士は勝負しない
    // プレイヤーが複数いても、それは単に１対１の勝負が複数あるだけ
    //
    // ・ディーラーとプレイヤーの双方がバストしていたらディーラーの勝ち
    // ・ディーラーとプレイヤーの双方がバストしていない場合は 21 に近いプレイヤーが勝ち
    // ・点数が同じ場合は引き分け
    // ・２枚でブラックジャック（ナチュラルブラックジャック）と３枚以上のブラックジャックがあったらナチュラルブラックジャックの勝ち
    override fun judge(
        dealer: BJPlayer,
        players: List<BJPlayer>,
        completion: (result: BJJudgement.BJResult) -> Unit
    ) {
        val winners = mutableListOf<BJPlayer>()
        val losers = mutableListOf<BJPlayer>()
        val draws = mutableListOf<BJPlayer>()

        players.forEach { player ->
            if (player.isAllBust) {
                losers.add(player)
            } else if (dealer.isAllBust) {
                winners.add(player)
            } else if ((dealer.hasNaturalBlackJack && player.hasNaturalBlackJack) || (dealer.hasBlackJack && player.hasBlackJack)) {
                draws.add(player)
            } else if (dealer.hasNaturalBlackJack) {
                losers.add(player)
            } else if (player.hasNaturalBlackJack) {
                winners.add(player)
            } else if (dealer.hasBlackJack) {
                losers.add(player)
            } else if (player.hasBlackJack) {
                winners.add(player)
            } else if ((player.validMaxCount ?: 0) < (dealer.validMaxCount ?: 0)) {
                losers.add(player)
            } else if ((dealer.validMaxCount ?: 0) < (player.validMaxCount ?: 0)) {
                winners.add(player)
            } else {
                draws.add(player)
            }
        }

        completion(BJJudgement.BJResult(winners, losers, draws))
    }
}
