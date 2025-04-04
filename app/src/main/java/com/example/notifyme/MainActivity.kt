package com.example.notifyme

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var messageDisplay: TextView
    private lateinit var statusText: TextView
    private lateinit var webSocket: WebSocket

    private val client = OkHttpClient.Builder()
        .pingInterval(10, TimeUnit.SECONDS) // Keep connection alive
        .build()
    private val serverUrl = "ws://Your_IP_Address:8000/ws/android"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageDisplay = findViewById(R.id.messageDisplay)
        statusText = findViewById(R.id.statusText)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        val sendButton = findViewById<Button>(R.id.sendButton)

        connectWebSocket()

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                messageDisplay.append("\nMe: $messageText\n")
                messageInput.text.clear()

                webSocket.send(messageText)
            }
        }


//        // WebSocket Connection
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("ws://192.168.43.180:8000/ws/android")
//            .build()
//
//        webSocket = client.newWebSocket(request, object : WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                runOnUiThread {
//                    statusText.text = "Connected ✅"
//                    statusText.setTextColor(Color.GREEN)
//                    messageDisplay.append("\n                        Connected to server\n                     waiting for the messages...\n")
//                }
//            }
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                runOnUiThread {
//                    messageDisplay.append("\nWindows: $text")
//
//                }
//            }
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                runOnUiThread {
//                    statusText.text = "Disconnected ❌"
//                    statusText.setTextColor(Color.RED)
//                    messageDisplay.append("\nError: ${t.message}")
//                }
//            }
//        })


    }

    private fun connectWebSocket() {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                runOnUiThread {
                    statusText.text = "Connected ✅"
                    statusText.setTextColor(Color.GREEN)
                    messageDisplay.append("\n                        Connected to server\n                     waiting for the messages...\n")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    messageDisplay.append("\nWindows: $text")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                runOnUiThread {
                    statusText.text = "Disconnected ❌ Reconnecting..."
                    statusText.setTextColor(Color.RED)
                    messageDisplay.append("\nError: ${t.message}")
                }
                reconnectWebSocket() // Try to reconnect
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                runOnUiThread {
                    statusText.text = "Disconnected ❌"
                    statusText.setTextColor(Color.RED)
                }
                reconnectWebSocket() // Try to reconnect
            }
        })
    }

    private fun reconnectWebSocket() {
        Thread.sleep(3000) // Wait 3 seconds before reconnecting
        connectWebSocket()
    }
}