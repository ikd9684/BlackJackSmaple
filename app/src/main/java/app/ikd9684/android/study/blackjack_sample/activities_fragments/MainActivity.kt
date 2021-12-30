package app.ikd9684.android.study.blackjack_sample.activities_fragments

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.ikd9684.android.study.blackjack_sample.R
import app.ikd9684.android.study.blackjack_sample.databinding.ActivityMainBinding
import app.ikd9684.android.study.blackjack_sample.view_model.BlackJackViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val bj: BlackJackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.btnNew.isEnabled = true
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = false
        binding.btnStand.isEnabled = false

        binding.btnNew.setOnClickListener {
            Log.d("■", "= New ===============================")
            bj.startNewGame(listOf("プレイヤー1", "プレイヤー2"))

            binding.btnNew.isEnabled = false
            binding.btnNext.isEnabled = false
            binding.btnHit.isEnabled = true
            binding.btnStand.isEnabled = true
        }

        binding.btnNext.setOnClickListener {
            Log.d("■", "- Next ------------------------------")
            bj.startNextPlay()

            binding.btnNew.isEnabled = true
            binding.btnNext.isEnabled = false
            binding.btnHit.isEnabled = true
            binding.btnStand.isEnabled = true
        }
        binding.btnHit.setOnClickListener {
            bj.hit()
        }
        binding.btnStand.setOnClickListener {
            bj.stand()
        }

        bj.dealer.observe(this) { dealer ->
            Log.d("■", "- Dealer -------------------------")
            Log.d("■", "cards=${bj.cards}")
            Log.d("■", "dealer=${dealer}")
        }

        bj.player.observe(this) { player ->
            Log.d("■", "- Player -------------------------")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "player=$player")
        }

        bj.players.observe(this) { players ->
            Log.d("■", "- Players -------------------------")
            Log.d("■", "cards=${bj.cards}")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "players=$players")
        }

        bj.turn.observe(this) { turn ->
            Log.d("■", "- Turn -------------------------")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "players=${bj.players.value}")
            Log.d("■", "turn=$turn")
        }

        bj.result.observe(this) { result ->
            Log.d("■", "- Finish -------------------------")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "winner=${result.winners}")
            Log.d("■", "loser=${result.losers}")
            Log.d("■", "draws=${result.draws}")

            binding.btnNew.isEnabled = true
            binding.btnNext.isEnabled = true
            binding.btnHit.isEnabled = false
            binding.btnStand.isEnabled = false
        }
    }
}
