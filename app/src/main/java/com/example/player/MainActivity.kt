package com.example.player

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.example.player.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var playButton: ImageButton
    lateinit var addFirstTrack: Button
    lateinit var addSecondTrack: Button

    private val firstTrack: Uri = Uri.EMPTY
    private val secondTrack: Uri = Uri.EMPTY
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
    }
}