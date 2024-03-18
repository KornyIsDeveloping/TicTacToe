package com.kornyisdeveloping.tictactoe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kornyisdeveloping.tictactoe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener {

    private var isHumanPlaying = true

    lateinit var binding: ActivityGameBinding

    private var gameModel : GameModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        GameData.fetchGameModel()

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        binding.startGameBtn.setOnClickListener {
            startGame()
        }

        GameData.gameModel.observe(this) {
            gameModel = it
            setUI()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setUI() {
        gameModel?.apply {
            binding.btn0.text = filledPos[0]
            binding.btn1.text = filledPos[1]
            binding.btn2.text = filledPos[2]
            binding.btn3.text = filledPos[3]
            binding.btn4.text = filledPos[4]
            binding.btn5.text = filledPos[5]
            binding.btn6.text = filledPos[6]
            binding.btn7.text = filledPos[7]
            binding.btn8.text = filledPos[8]

            binding.startGameBtn.visibility = View.VISIBLE

            binding.gameStatusText.text =
                when(gameStatus) {
                    GameStatus.CREATED -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        "Game ID: " + gameId
                    }
                    GameStatus.JOINED -> {
                        "Click the start game!"
                    }
                    GameStatus.INPROGRESS -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        when(GameData.myId) {
                            currentPlayer -> "Your turn"
                            else -> currentPlayer + " turn"
                        }
                    }
                    GameStatus.FINISHED -> {
                        if(winner.isNotEmpty()) {
                            when(GameData.myId) {
                                winner -> "You won!"
                                else -> winner + " won!"
                            }
                        }
                        else "Draw!"
                    }
                }
        }
    }

//    fun startGame(){
//        gameModel?.apply {
//            updateGameData(
//                GameModel(
//                    gameId = gameId,
//                    gameStatus = GameStatus.INPROGRESS
//                )
//            )
//        }
//    }
fun startGame() {
    gameModel?.apply {
        // Reset the game board
        filledPos = MutableList(9) { "" }
        winner = ""
        gameStatus = GameStatus.INPROGRESS
        currentPlayer = "X"  // Let's say "X" represents the human player

        updateGameData(this)

        // If the bot should start first, trigger the bot's move
        // If you decide to have a button or a setting to let the user choose who starts,
        // you can conditionally call botMove() based on that setting.
        if (shouldBotStartFirst()) {
            isHumanPlaying = false
            currentPlayer = "O"  // "O" represents the bot
            botMove()  // Trigger the bot's move
        } else {
            // The human is starting, so wait for user input
            isHumanPlaying = true
        }
    }
}

    private fun botMove() {
        gameModel?.let { model ->
            val availablePositions = model.filledPos.mapIndexedNotNull { index, value ->
                if (value.isEmpty()) index else null
            }
            if (availablePositions.isNotEmpty()) {
                val movePosition = availablePositions.random()
                model.filledPos[movePosition] = "O"  // Assuming the bot is "O"
                model.currentPlayer = "X"  // Switch turn back to the human player
                checkForWinner()
                updateUI(movePosition)
            }
        }
    }

    private fun updateUI(movePosition: Int) {
        // Update the specific button based on the move position
        // For example:
        val button = when (movePosition) {
            0 -> binding.btn0
            1 -> binding.btn1
            2 -> binding.btn2
            3 -> binding.btn3
            4 -> binding.btn4
            5 -> binding.btn5
            6 -> binding.btn6
            7 -> binding.btn7
            8 -> binding.btn8
            else -> null
        }
        button?.text = "O"  // Set the bot's move on the button

        // Check if you should update any status text or enable/disable buttons
    }

    private fun shouldBotStartFirst(): Boolean {
        // You can introduce logic or a setting that decides if the bot should start
        // For now, we can return false to let the human start, or true for the bot
        return false  // Replace with actual logic or setting
    }

    fun updateGameData(model : GameModel) {
        GameData.saveGameModel(model)
    }

    fun checkForWinner() {
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6),
        )

        gameModel?.apply {
            for ( i in winningPos){
                //012
                if(
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]]== filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ){
                    gameStatus = GameStatus.FINISHED
                    winner = filledPos[i[0]]
                }
            }
            //if is draw
            if( filledPos.none(){ it.isEmpty() }){
                gameStatus = GameStatus.FINISHED
            }


            updateGameData(this)

        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if(gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return;
            }
            //game in progress
            if(gameId != "-1" && currentPlayer != GameData.myId) {
                Toast.makeText(applicationContext, "Not your turn", Toast.LENGTH_SHORT).show()
                return;
            }
            val clickedPos = (v?.tag as String).toInt()
            if(filledPos[clickedPos].isEmpty()) {
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if(currentPlayer == "X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }
            if (!isMultiplayer && !isWinnerFound() && !isBoardFull()) {
                botMoveWithDelay()
            }
        }
    }

    private fun isWinnerFound(): Boolean {
        val winningPositions = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8), // rows
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8), // columns
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)  // diagonals
        )
        gameModel?.filledPos?.let { filledPos ->
            for (pos in winningPositions) {
                if (filledPos[pos[0]] != "" &&
                    filledPos[pos[0]] == filledPos[pos[1]] &&
                    filledPos[pos[1]] == filledPos[pos[2]]) {
                    return true
                }
            }
        }
        return false
    }

    private fun isBoardFull(): Boolean {
        // Check if all positions are filled
        return gameModel?.filledPos?.all { it.isNotEmpty() } ?: false
    }

    private fun botMoveWithDelay() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            botMove()
        }, 1000) // Delay the bot move by 1 second
    }

}