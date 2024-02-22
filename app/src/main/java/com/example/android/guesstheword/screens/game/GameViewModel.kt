package com.example.android.guesstheword.screens.game


import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData as LiveData


private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel: ViewModel() {

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }
    companion object {
        // These represent different important times
        // This is the number of milliseconds in a second
      private  const val ONE_SECOND = 1000L
        // This is the total time of the game
      private  const val COUNTDOWN_TIME =60000L

        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L

    }

    private val TAG=GameViewModel::class.simpleName

    // The current word
     private val _word = MutableLiveData<String>()
    //encapsulation of parameter
     val word : LiveData<String>
               get() = _word

   private val _eventGameFinish=MutableLiveData<Boolean>()
    val eventGameFinish:LiveData<Boolean>
    get() = _eventGameFinish

    // The current score
    private val _score =MutableLiveData<Int>()
     val score :LiveData<Int>
                get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

private  val timer:CountDownTimer

private val _currentTime=MutableLiveData<Long>()
        val currentTime:LiveData<Long>
              get() = _currentTime

    val currentTimeString:LiveData<String>

    private val _eventBuzz=MutableLiveData<BuzzType>()
   val eventBuzz:MutableLiveData<BuzzType>
   get() = _eventBuzz


    init {
        Log.i(TAG, "${TAG}: ViewModel is created")
        resetList()
        nextWord()
       _score.value = 0
        _eventGameFinish.value=false

        timer=object:CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
            override fun onTick(millisUntilFinished: Long) {
                //current time in seconds
              _currentTime.value=millisUntilFinished/ ONE_SECOND
                if(_currentTime.value!! <= 10L){_eventBuzz.value=BuzzType.COUNTDOWN_PANIC}
            }

            override fun onFinish() {
               _eventGameFinish.value=true
                _eventBuzz.value=BuzzType.GAME_OVER
            }

        }.start()

    currentTimeString=Transformations.map(_currentTime) { timeLong ->
       DateUtils.formatElapsedTime(timeLong)
   }


    }


    /**-----------------------------------------------------------------------------------------------
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
           // gameFinished
            resetList()
        }
            _word.value = wordList.removeAt(0)
        }



     fun onSkip() {
       _score.value=_score.value?.minus(1)
        nextWord()

    }


     fun onCorrect() {
         _score.value=_score.value?.plus(1)
        nextWord()
         _eventBuzz.value=BuzzType.CORRECT
    }

    fun onGameFinishComplete(){_eventGameFinish.value=false}

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }


    override fun onCleared() {
        Log.i(TAG, "onCleared: GameViewModel is destroyed")
        timer.cancel()
        super.onCleared()
    }


}