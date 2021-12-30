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
            if (player.isBust) {
                losers.add(player)
            } else if (dealer.isBust) {
                winners.add(player)
            } else if ((dealer.isNaturalBlackJack && player.isNaturalBlackJack) || (dealer.isBlackJack && player.isBlackJack)) {
                draws.add(player)
            } else if (dealer.isNaturalBlackJack) {
                losers.add(player)
            } else if (player.isNaturalBlackJack) {
                winners.add(player)
            } else if (dealer.isBlackJack) {
                losers.add(player)
            } else if (player.isBlackJack) {
                winners.add(player)
            } else if (player.count < dealer.count) {
                losers.add(player)
            } else if (dealer.count < player.count) {
                winners.add(player)
            } else {
                draws.add(player)
            }
        }

        completion(BJJudgement.BJResult(winners, losers, draws))
    }
}
