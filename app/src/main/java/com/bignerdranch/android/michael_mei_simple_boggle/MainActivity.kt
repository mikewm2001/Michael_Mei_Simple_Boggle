package com.bignerdranch.android.michael_mei_simple_boggle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), BoggleGameFragment.OnBoggleInteractionListener, ScoreFragment.OnScoreInteractionListener, BoggleGameFragment.OnScoreUpdateListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.boggleGameFragment, BoggleGameFragment.newInstance())
                .add(R.id.scoreFragment, ScoreFragment.newInstance())
                .commit()
        }
    }

    override fun onWordSubmitted(word: String) {

    }

    override fun onNewGameClicked() {
        onResetGame()
    }

    override fun onScoreUpdated(score: Int) {
        // Update the score in the ScoreFragment
        val scoreFragment = supportFragmentManager.findFragmentById(R.id.scoreFragment) as? ScoreFragment
        scoreFragment?.updateScore(score)
    }

    override fun onResetGame() {
        val boggleFragment = supportFragmentManager.findFragmentById(R.id.boggleGameFragment) as? BoggleGameFragment
        boggleFragment?.onResetGame()

        val scoreFragment = supportFragmentManager.findFragmentById(R.id.scoreFragment) as? ScoreFragment
        scoreFragment?.updateScore(0)
    }
}