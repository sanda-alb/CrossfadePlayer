package com.example.player

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.player.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var playButton: ImageButton
    lateinit var addFirstTrack: Button
    lateinit var addSecondTrack: Button

    private var firstTrack: Uri = Uri.EMPTY
    private var secondTrack: Uri = Uri.EMPTY
    val mediaPlayers = mutableListOf<MediaPlayer>(MediaPlayer(), MediaPlayer())

    lateinit var crossfadeBar: SeekBar
    lateinit var crossfadeText: TextView
    private var crossfadeTime = 2

    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        addFirstTrack = binding.addFirstTrack
        addSecondTrack = binding.addSecondTrack
        crossfadeBar = binding.seekBar
        crossfadeText = binding.crossfadeTime
        crossfadeText.text = crossfadeTime.toString()
        playButton = binding.playButton
        Toast.makeText(this, "Add two tracks and set crossfade time", Toast.LENGTH_LONG).show()

        selectTrack()
        changeCrossfadeBar()
    }


    fun selectTrack() {
        addFirstTrack.setOnClickListener {
            addTrack(resultLauncher1)
        }
        addSecondTrack.setOnClickListener {
            addTrack(resultLauncher2)
        }
    }

    fun addTrack(resultLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.type = "audio/*"
        resultLauncher.launch(intent)
    }

    val resultLauncher1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            loadTrack(mediaPlayers.first(), result)
            firstTrack = result.data?.data!!
        }

    val resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            loadTrack(mediaPlayers.last(), result)
            secondTrack = result.data?.data!!
        }

    fun loadTrack(mediaPlayer: MediaPlayer, result: ActivityResult) {
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                if (data != null) {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(this, data.data!!)
                    mediaPlayer.prepare()

                    if (mediaPlayer.duration < (crossfadeTime * 2000)) {
                        throw Exception("Song is too short.")
                    }

                }
            }
        } catch (error: Exception) {
            Toast.makeText(
                this,
                "Please, choose track longer than ${crossfadeTime * 2} sec",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun changeCrossfadeBar(): Int {
        crossfadeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onProgressChanged(crossfadeBar: SeekBar, progress: Int, changed: Boolean) {
                crossfadeBar.max = 10
                crossfadeBar.min = 2

                if (progress >= 2) {
                    crossfadeTime = progress
                }
                if (changed) {
                    crossfadeText.text = crossfadeTime.toString()
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        return crossfadeTime
    }

    private fun volumeUp(mediaPlayer: MediaPlayer) = coroutineScope.launch {
        var volume = 0.0f
        while (mediaPlayer != Uri.EMPTY && volume < 0.1f) {
            mediaPlayer.setVolume(volume, volume)
            delay(100 * crossfadeTime.toLong())
            volume += 0.1f
        }
    }

    private fun volumeDown(mediaPlayer: MediaPlayer) = coroutineScope.launch {
        var volume = 1.0f
        while (mediaPlayer != Uri.EMPTY && volume > 0.0f) {
            mediaPlayer.setVolume(volume, volume)
            delay(100 * crossfadeTime.toLong())
            volume -= 0.1f
        }
    }


}