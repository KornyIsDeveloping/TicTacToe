package com.kornyisdeveloping.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kornyisdeveloping.tictactoe.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.playOfflineBtn.setOnClickListener {
            createOfflineGame()
        }

        binding.createOnlineGameBtn.setOnClickListener {
            createOnlineGame()
        }

        binding.joinOnlineGameBtn.setOnClickListener {
            joinOnlineGame()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun createOfflineGame() {
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }

    fun createOnlineGame() {
//        GameData.saveGameModel(
//            GameModel(
//                gameStatus = GameStatus.CREATED
//                gameId = Random.nextInt(1000..9999).toString()
//            )
//        )
//        startGame()
        GameData.myId = "X"
        val newGameId = Random.nextInt().toString()  // Generate a new game ID as a string
        GameData.saveGameModel(
            GameModel(
                gameId = newGameId,  // Set the new game ID here
                gameStatus = GameStatus.CREATED,
                filledPos = mutableListOf("", "", "", "", "", "", "", "", ""),
                winner = "",
                currentPlayer = "X"
                // Other properties can be set here as well
            )
        )
        startGame()
    }

    fun joinOnlineGame() {

    }

    fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}