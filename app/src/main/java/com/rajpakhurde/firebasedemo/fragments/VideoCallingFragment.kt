package com.rajpakhurde.firebasedemo.fragments

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rajpakhurde.firebasedemo.R
import com.rajpakhurde.firebasedemo.databinding.FragmentVideoCallingBinding
import com.rajpakhurde.firebasedemo.media.RtcTokenBuilder2
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoCallingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideoCallingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentVideoCallingBinding
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
        return !(ContextCompat.checkSelfPermission(requireContext(), REQUESTED_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), REQUESTED_PERMISSION[1]) != PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpvideoSdkEngine() {
        try {
            val config = RtcEngineConfig()
//            config.mContext = baseContext
            config.mContext = requireContext()
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            e.message?.let { showMessage(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoCallingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        var channelName = intent.getStringExtra("roomId")
        var channelName = arguments?.getString("roomId")

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
            ActivityCompat.requestPermissions(context as Activity,REQUESTED_PERMISSION, PERMISSION_ID)
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

            requireActivity().runOnUiThread { setUpRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("User Offline")

            requireActivity().runOnUiThread {
                remoteSufaceView!!.visibility = View.GONE
            }
        }
    }

    private fun setUpRemoteVideo(uid: Int) {
        remoteSufaceView = SurfaceView(requireContext())
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
        localSufaceView = SurfaceView(requireContext())
        binding.localUser.addView(localSufaceView)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSufaceView,
                VideoCanvas.RENDER_MODE_ADAPTIVE,
                0
            )
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VideoCallingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideoCallingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}