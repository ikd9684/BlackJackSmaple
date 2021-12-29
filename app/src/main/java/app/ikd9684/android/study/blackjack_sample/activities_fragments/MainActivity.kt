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
            bj.startNewGame(listOf("プレイヤー"))

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
            Log.d("■", "dealer=${dealer}")
        }
        bj.players.observe(this) { players ->
            Log.d("■", "cards=${bj.cards}")
            Log.d("■", "players=$players")
            Log.d("■", "turn=${bj.turn}")

            bj.judgeTheWinner()?.let { winner ->
                Log.d("■", "- Finish -------------------------")
                Log.d("■", "players=$players")
                Log.d("■", "winner=$winner")

                binding.btnNew.isEnabled = true
                binding.btnNext.isEnabled = true
                binding.btnHit.isEnabled = false
                binding.btnStand.isEnabled = false
            }
        }
    }
}
