package com.bignerdranch.android.michael_mei_simple_boggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.abs

class BoggleGameFragment : Fragment() {

    private var listener: OnBoggleInteractionListener? = null
    private var scoreUpdateListener: OnScoreUpdateListener? = null
    private val submittedWords = mutableSetOf<String>()
    private val validWordsSet = HashSet<String>()
    private var totalScore = 0

    private val clickedButtonIds = mutableListOf<Int>()
    // Map to store the coordinates of each button
    private val buttonCoordinates = mapOf(
        R.id.button00 to Pair(0, 0),
        R.id.button01 to Pair(0, 1),
        R.id.button02 to Pair(0, 2),
        R.id.button03 to Pair(0, 3),
        R.id.button10 to Pair(1, 0),
        R.id.button11 to Pair(1, 1),
        R.id.button12 to Pair(1, 2),
        R.id.button13 to Pair(1, 3),
        R.id.button20 to Pair(2, 0),
        R.id.button21 to Pair(2, 1),
        R.id.button22 to Pair(2, 2),
        R.id.button23 to Pair(2, 3),
        R.id.button30 to Pair(3, 0),
        R.id.button31 to Pair(3, 1),
        R.id.button32 to Pair(3, 2),
        R.id.button33 to Pair(3, 3)
    )

    interface OnBoggleInteractionListener {
        fun onWordSubmitted(word: String)
    }

    interface OnScoreUpdateListener {
        fun onScoreUpdated(score: Int)
        fun onResetGame()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_boggle_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up word dictionary
        loadValidWordsFromFile()

        // Set up button click listeners
        val buttonClickListener = View.OnClickListener { v ->
            if (v is Button) {
                val buttonId = v.id
                if (!clickedButtonIds.contains(buttonId) && isAdjacentButton(buttonId)) {
                    clickedButtonIds.add(buttonId)
                    v.isEnabled = false // Disable the clicked button
                    val letter = v.text.toString()
                    appendLetterToWord(letter)
                } else {
                    // Show a message to the user indicating invalid selection
                    Toast.makeText(requireContext(), "Invalid selection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Find and set click listeners for letter buttons
        val buttonIds = arrayOf(
            R.id.button00, R.id.button01, R.id.button02, R.id.button03,
            R.id.button10, R.id.button11, R.id.button12, R.id.button13,
            R.id.button20, R.id.button21, R.id.button22, R.id.button23,
            R.id.button30, R.id.button31, R.id.button32, R.id.button33
        )

        for (buttonId in buttonIds) {
            view.findViewById<Button>(buttonId).setOnClickListener(buttonClickListener)
        }

        // Set up click listener for the clear button
        view.findViewById<Button>(R.id.clearButton).setOnClickListener {
            clearBoard()
        }

        // Set up click listener for the submit button
        view.findViewById<Button>(R.id.submitButton).setOnClickListener {
            submitWord()
        }

        view.findViewById<Button>(R.id.newGameButton)?.setOnClickListener {
            onResetGame()
        }
    }

    private fun submitWord() {
        val wordEditText = requireView().findViewById<EditText>(R.id.wordEditText)
        val word = wordEditText.text.toString().lowercase()
        var score = 0

        if (word.length >= 4 && hasTwoVowels(word)) {
            if (isValidWord(word)) {
                if (submittedWords.contains(word)) {
                    Toast.makeText(requireContext(), "Word already submitted", Toast.LENGTH_SHORT).show()
                    clearBoard()
                } else {
                    score = calculateScore(word)
                    Toast.makeText(requireContext(), "Correct word! Score: $score", Toast.LENGTH_SHORT).show()

                    // Update total score
                    totalScore += score

                    // Add the word to the set of submitted words
                    submittedWords.add(word)

                    // Notify the activity of the updated score
                    scoreUpdateListener?.onScoreUpdated(score)

                    // Reset the board
                    clearBoard()
                }
            } else {
                Toast.makeText(requireContext(), "Incorrect word! -10 points", Toast.LENGTH_SHORT).show()
                // Reduce score by 10 for incorrect word
                score -= 10

                // Update total score
                totalScore -= 10

                // Notify the activity of the updated score
                scoreUpdateListener?.onScoreUpdated(score)
            }
        } else {
            Toast.makeText(requireContext(), "Word must be at least 4 characters long and contain at least 2 letters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasTwoVowels(word: String): Boolean {
        // Check if the word has at least 2 vowels
        var count = 0
        for (letter in word) {
            if (letter in listOf('a', 'e', 'i', 'o', 'u')) {
                count += 1
            }
        }
        return (count >= 2)
    }

    private fun isValidWord(word: String): Boolean {
        // Check if the word is valid
        return validWordsSet.contains(word)
    }

    private fun calculateScore(word: String): Int {
        // Calculate points based on the rules
        var score = 0
        var double = false
        for (letter in word) {
            if (letter in listOf('a', 'e', 'i', 'o', 'u')) {
                score += 5
            } else if (letter in listOf('s', 'z', 'p', 'x', 'q')) {
                score += 1
                double = true //double the score after adding everything else up
            } else {
                score += 1
            }
        }
        if (double) {
            score *= 2
        }
        return score
    }

    private fun clearBoard() {
        // Reset the EditText
        val wordEditText = requireView().findViewById<EditText>(R.id.wordEditText)
        wordEditText.setText("")

        // Reenable all disabled buttons
        for (buttonId in clickedButtonIds) {
            val button = requireView().findViewById<Button>(buttonId)
            button.isEnabled = true
        }

        // Clear the list of clicked button IDs
        clickedButtonIds.clear()
    }

    fun onResetGame() {
        // Display the default score
        scoreUpdateListener?.onScoreUpdated(totalScore * -1)

        // Reset the total score
        totalScore = 0

        // Clear the board
        clearBoard()

        // Clear the set of valid words
        validWordsSet.clear()

        // Randomize the letters in the grid
        val alphabet = ('A'..'Z').toList()
        for (buttonId in buttonCoordinates.keys) {
            val button = requireView().findViewById<Button>(buttonId)
            val randomLetter = alphabet.random()
            button.text = randomLetter.toString()
        }
    }

    private fun appendLetterToWord(letter: String) {
        val wordEditText = requireView().findViewById<EditText>(R.id.wordEditText)
        val currentWord = wordEditText.text.toString()
        val newWord = currentWord + letter
        wordEditText.setText(newWord)
    }

    private fun isAdjacentButton(buttonId: Int): Boolean {
        // Check if the clicked button is adjacent to the last clicked button
        // If no button has been clicked yet, any button can be clicked
        if (clickedButtonIds.isEmpty()) {
            return true
        }

        // Get the coordinates of the clicked button
        val clickedCoordinate = buttonCoordinates[buttonId]

        // Get the coordinates of the last clicked button
        val lastClickedButtonId = clickedButtonIds.last()
        val lastClickedCoordinate = buttonCoordinates[lastClickedButtonId]

        // Check if the clicked button is adjacent to the last clicked button
        return clickedCoordinate != null && lastClickedCoordinate != null &&
                abs(clickedCoordinate.first - lastClickedCoordinate.first) <= 1 &&
                abs(clickedCoordinate.second - lastClickedCoordinate.second) <= 1 &&
                clickedCoordinate != lastClickedCoordinate // Exclude the same button
    }

    private fun loadValidWordsFromFile() {
        val inputStream: InputStream = requireContext().assets.open("words.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            validWordsSet.add(line.trim().lowercase()) // Convert to lowercase and trim whitespace
            line = reader.readLine()
        }
        reader.close()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBoggleInteractionListener && context is OnScoreUpdateListener) {
            listener = context
            scoreUpdateListener = context
        } else {
            throw RuntimeException("$context must implement OnBoggleInteractionListener and OnScoreUpdateListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = BoggleGameFragment()
    }
}