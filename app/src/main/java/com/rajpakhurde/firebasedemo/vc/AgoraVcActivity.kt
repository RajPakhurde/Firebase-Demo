package com.rajpakhurde.firebasedemo.vc

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rajpakhurde.firebasedemo.databinding.ActivityAgoraVcBinding
import com.rajpakhurde.firebasedemo.media.RtcTokenBuilder2
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class AgoraVcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgoraVcBinding

    private val appId = "7e5f7b2dcaf74ef9872b308f39afda33"
    private val appCertificate = "ece273cfc1144ae2a4db3176b5bae95b"
//    private val channelName = "EMentoring"
//    private var token = "007eJxTYAj2Vypwzf8zP3HP9nvGLEHTflzkKkpl0Zi5N2fpyzp9gRQFBvNU0zTzJKOU5MQ0c5PUNEsLc6MkYwOLNGPLxLSURGPjAwvEUhoCGRl0PtxnZGSAQBCfi8HVNzWvJL8oMy+dgQEA7EQhvQ=="
    private var token : String? = null
    private val uid = 0
    private var isJoined = false
    private var agoraEngine : RtcEngine? = null
    private var localSufaceView : SurfaceView? = null
    private var remoteSufaceView : SurfaceView? = null

    private val PERMISSION_ID = 22
    private val REQUESTED_PERMISSION = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    private fun checkSelfPermission() : Boolean{
        return !(ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSION[1]) != PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpvideoSdkEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            e.message?.let { showMessage(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgoraVcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var channelName = intent.getStringExtra("roomId")

        val tokenBuilder = RtcTokenBuilder2()
        val timeStamp = (System.currentTimeMillis() / 1000 + 60).toInt()

        token = tokenBuilder.buildTokenWithUid(
            appId,
            appCertificate,
            channelName,
            uid,
            RtcTokenBuilder2.Role.ROLE_PUBLISHER,
            timeStamp,
            timeStamp
        )

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this,REQUESTED_PERMISSION, PERMISSION_ID)
        }

        setUpvideoSdkEngine()

        binding.btnJoin.setOnClickListener {
            joinCall(channelName!!)
        }

        binding.btnLeave.setOnClickListener {
            leaveCall()
        }
    }

    private fun leaveCall() {

        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            if (remoteSufaceView != null) {
                remoteSufaceView!!.visibility = View.GONE
            }
            if (localSufaceView != null) {
                localSufaceView!!.visibility = View.GONE
            }
        }
    }

    private fun joinCall(channelName: String) {
        if (checkSelfPermission()) {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setUpLocalVideo()
            localSufaceView!!.visibility = View.VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token,channelName,uid,option)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private val mRtcEventHandler : IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote User joined $uid")

            runOnUiThread { setUpRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("User Offline")

            runOnUiThread {
                remoteSufaceView!!.visibility = View.GONE
            }
        }
    }

    private fun setUpRemoteVideo(uid: Int) {
        remoteSufaceView = SurfaceView(baseContext)
        remoteSufaceView!!.setZOrderMediaOverlay(true)
        binding.remoteUser.addView(remoteSufaceView)

        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSufaceView,
                VideoCanvas.RENDER_MODE_ADAPTIVE,
                uid
            )
        )
    }

    private fun setUpLocalVideo() {
        localSufaceView = SurfaceView(baseContext)
        binding.localUser.addView(localSufaceView)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSufaceView,
                VideoCanvas.RENDER_MODE_ADAPTIVE,
                0
            )
        )
    }
}