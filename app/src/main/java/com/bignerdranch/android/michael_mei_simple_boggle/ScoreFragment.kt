package com.bignerdranch.android.michael_mei_simple_boggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ScoreFragment : Fragment() {

    interface OnScoreInteractionListener {
        fun onNewGameClicked()
    }

    private var listener: OnScoreInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize views and set up button click listeners
        view.findViewById<Button>(R.id.newGameButton)?.setOnClickListener {
            listener?.onNewGameClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScoreInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnScoreInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScoreFragment()
    }

    fun updateScore(score: Int) {
        val scoreTextView = requireView().findViewById<TextView>(R.id.scoreTextView)
        val existingScoreString = scoreTextView.text.toString().removePrefix("Score: ")
        val existingScore = existingScoreString.toIntOrNull() ?: 0
        val combinedScore = existingScore + score
        scoreTextView.text = "Score: $combinedScore"
    }
}