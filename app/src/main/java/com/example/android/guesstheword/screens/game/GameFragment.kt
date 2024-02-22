/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {


    private lateinit var binding: GameFragmentBinding
    private  lateinit var gameViewModel: GameViewModel

/*----------------------------------------------------------------------------------------------------------*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

    binding.lifecycleOwner = this

    //gameViewModel=ViewModelProviders.of(this).get(GameViewModel::class.java)
    gameViewModel= ViewModelProvider(this).get(GameViewModel::class.java)

//bind gameViewModel variable with the view model class
    binding.gameViewModel=gameViewModel

//    gameViewModel.score.observe(viewLifecycleOwner, Observer { newScore->
//        updateScoreText(newScore)
//    })
//    gameViewModel.word.observe(viewLifecycleOwner, Observer {
//        updateWordText()
//    })

    gameViewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { eventGameFinish->
    if(eventGameFinish.equals(true)) gameFinished()
})

  gameViewModel.eventBuzz.observe(viewLifecycleOwner, Observer { buzzType->
      if(buzzType!=GameViewModel.BuzzType.NO_BUZZ)
      {
          buzz(buzzType.pattern)
          gameViewModel.onBuzzComplete()
      }
  })

//    gameViewModel.currentTime.observe(viewLifecycleOwner, Observer {
//       updateTimerText()
//    })

//        binding.correctButton.setOnClickListener {
//            gameViewModel. onCorrect()
//
//        }
//
//        binding.skipButton.setOnClickListener {
//            gameViewModel.onSkip()
//        }

        return binding.root

    }

/*------------------------------------------------------------------------------------*/

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        //set eventGameFinish to false again
        gameViewModel.onGameFinishComplete()
        val action = GameFragmentDirections.actionGameToScore(gameViewModel.score.value?:0)
        findNavController(this).navigate(action)
    }



    /** Methods for buttons presses **/



    /** Methods for updating the UI **/

     private fun updateWordText() {
        binding.wordText.text = gameViewModel.word.value

    }
    private fun updateTimerText() {
        binding.timerText.text = DateUtils.formatElapsedTime(gameViewModel.currentTime.value ?: 0)

    }
    private fun updateScoreText(newScore:Int) {
        binding.scoreText.text =newScore.toString()
    }


    private fun buzz(pattern:LongArray){

        Log.i("TAG", "buzz: buzzer is indicated")
        val buzzer=activity?.getSystemService<Vibrator>()

      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        buzzer?.vibrate(VibrationEffect.createWaveform(pattern,-1))

        else buzzer?.vibrate(pattern,-1)
    }
}
