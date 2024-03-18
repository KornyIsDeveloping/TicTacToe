package com.kornyisdeveloping.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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
                gameStatus = GameStatus.JOINED,
                isMultiplayer = false
            )
        )
        startGame()
    }

    fun createOnlineGame() {
        GameData.myId = "X"
        val newGameId = Random.nextInt(1000, 9999).toString()  // Generate a new game ID as a string
        GameData.saveGameModel(
            GameModel(
                gameId = newGameId,  // Set the new game ID here
                gameStatus = GameStatus.CREATED,
                isMultiplayer = true,
                filledPos = mutableListOf("", "", "", "", "", "", "", "", ""),
                winner = "",
                currentPlayer = "X"
            )
        )
        startGame()
    }

    fun joinOnlineGame() {
        var gameId = binding.gameIdInput.text.toString()
        if(gameId.isEmpty()) {
            binding.gameIdInput.setError("Please enter the game Id")
            return
        }
        GameData.myId = "O"
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it?.toObject(GameModel::class.java)
                if(model == null) {
                    binding.gameIdInput.setError("Please enter a valid game Id")
                }else{
                    model.gameStatus = GameStatus.JOINED
                    model.isMultiplayer = true
                    GameData.saveGameModel(model)
                    startGame()
                }
            }
    }

    fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}