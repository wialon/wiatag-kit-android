package com.gurtam.wiatag_kit.example

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gurtam.wiatagkit.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.util.*


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private var messageListener: MessageListener = object : MessageListener() {

        override fun onChatMessageReceived(messageId: String?, chatMessage: ChatMessage?) {
            val builder = StringBuilder()
            chatMessage?.title?.let { builder.append("$it ") }
            builder.append(chatMessage?.message)
            chatMessage?.latitude?.let { builder.append(" $it ") }
            chatMessage?.longitude?.let { builder.append(it) }
            chatListAdapter?.addItem(ChatItem(MessageType.INPUT, builder.toString()))
        }

        override fun onStartServiceCommand(commandId: String?, time: Int?) {
            chatListAdapter?.addItem(ChatItem(MessageType.COMMAND, "StartServiceCommand"))
        }

        override fun onStopServiceCommand(commandId: String?) {
            chatListAdapter?.addItem(ChatItem(MessageType.COMMAND, "StopServiceCommand"))
        }

        override fun getPosition(commandId: String?) {
            chatListAdapter?.addItem(ChatItem(MessageType.COMMAND, "GetPositionCommand"))
        }

        override fun remoteConfigReceive(commandId: String?, messageData: String?) {
            chatListAdapter?.addItem(ChatItem(MessageType.COMMAND, "RemoteConfigReceive $messageData"))
        }

        override fun remoteConfigRequest(commandId: String?) {
            chatListAdapter?.addItem(ChatItem(MessageType.COMMAND, "RemoteConfigRequest"))
        }


        override fun torch(commandId: String?, isFlashOn: Boolean) {
            if (isFlashOn) {
                chatListAdapter?.addItem(ChatItem(MessageType.COMMAND, "FlashOn"))
            } else {
                chatListAdapter?.addItem(ChatItem(MessageType.COMMAND, "FlashOff"))
            }
        }
    }
    var timer: CountDownTimer? = null;
    var chatListAdapter: ChatListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        chatListAdapter = ChatListAdapter(recyclerViewChat)
        recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true).apply { stackFromEnd = true }
            adapter = chatListAdapter
        }


        buttonInitialize.setOnClickListener {
            initMessageManager()
        }

        buttonSendChatMessage.setOnClickListener {
            val text = editTextChatMessage.text.toString()
            chatListAdapter?.addItem(ChatItem(MessageType.OUTPUT, text))
            val message = Message().time(Date().time).text(text)
            MessageManager.sendMessage(message, null)
            editTextChatMessage.text.clear()
            hideKeyboardFrom(this, view)
        }

        switchUseChat.setOnClickListener {
            MessageManager.useChat(switchUseChat.isChecked)
            if (switchUseChat.isChecked || switchUseRemoteControl.isChecked) {
                startTimer()
            }
        }
        switchUseRemoteControl.setOnClickListener {
            MessageManager.useCommands(switchUseRemoteControl.isChecked)
            if (switchUseChat.isChecked || switchUseRemoteControl.isChecked) {
                startTimer()
            }
        }

        // Send message
        buttonSendMessage.setOnClickListener {
            val message = Message().time(Date().time)
            if ((findViewById<View>(R.id.checkBoxSos) as CheckBox).isChecked) message.sos()
            if ((findViewById<View>(R.id.checkBoxImage) as CheckBox).isChecked) message.image(
                    "wiatag-kit",
                    getBytesFromDrawableBitmap(R.drawable.wiatag)
            )
            if ((findViewById<View>(R.id.checkBoxLocation) as CheckBox).isChecked)
            /**adding Location with
             * latitude     53.9058289
             * longitude    27.4569797
             * altitude     290 meter
             * speed        2 m/s
             * bearing      15
             * satellites   8
             */
                message.location(Location(53.9058289, 27.4569797, 290.0, 2f, 15.toShort(), 8.toByte()))
            MessageManager.sendMessage(message, SenderListener(this@BaseActivity))
        }

        /** Send array of Messages */
        buttonSendMessages.setOnClickListener { MessageManager.sendMessages(generateMessages(), SenderListener(this@BaseActivity)) }

    }

    open fun initMessageManager() {
        val serverUrl = server_url.text.toString()
        val serverPort = port.text.toString().toInt()
        val unit = unitId.text.toString()
        val unitPassword = password.text.toString()
        /** Initialize MessageManager with context, host, port, unitId, password*/
        MessageManager.initWithHost(this, serverUrl, serverPort, unit, unitPassword, SenderListener(this@BaseActivity   ))
        hideKeyboardFrom(this, view)
        MessageManager.registerMessageListener(messageListener)
    }

    /**helper method for periodically checking new commands or messages.
    If there is one, you will get it in the corresponding method of the messageListener class.*/
    open fun startTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        timer = object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                startTimer()
                MessageManager.checkUpdates(null)
            }
        }.start()
    }

    /** helper method to generate Array of messages*/
    private fun generateMessages(): List<Message> {
        val messages: MutableList<Message> = ArrayList()
        for (i in 9 downTo 0) {
            messages.add(
                    Message()
                            .time((Date().time + i * 1000))
                            .batteryLevel((i * 10).toByte())
                            .addParam("CustomParam", i)
                            .location(
                                    Location(
                                            53.9058289,
                                            27.4569797,
                                            290.0,
                                            0.toFloat(),
                                            0.toShort(),
                                            8.toByte()
                                    )
                            )
            )
        }
        return messages
    }

    /** helper method to get byte array of Drawable resource for sending*/
    private fun getBytesFromDrawableBitmap(@DrawableRes drawableId: Int): ByteArray {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
    }

    /** helper method to hide keyboard */
    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /** Callback for receiving sending status */
    class SenderListener(private val context: Context) : MessageSenderListener() {
        override fun onSuccess() {
            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
        }

        override fun onFailure(errorCode: Byte) {
            val errorMessage: String = when (errorCode) {
                FAILED_TO_CONNECT -> "Could not connect to server. Check connection and connection settings (host, port)"
                FAILED_TO_SEND -> "Packet parsing error"
                INVALID_UNIQUE_ID -> "Unit does not exist on server"
                INCORRECT_PASSWORD -> "Wrong password"
                else -> "Failure"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}