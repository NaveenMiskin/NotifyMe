package com.example.notifyme

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import okhttp3.Call
import okhttp3.Callback
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var messageDisplay: TextView
    private lateinit var statusText: TextView
    private lateinit var webSocket: WebSocket

    private val client = OkHttpClient.Builder()
        .pingInterval(10, TimeUnit.SECONDS) // Keep connection alive
        .build()
   //private val serverUrl = "ws://192.168.149.180:8000/ws/android"
   private val ipFetchUrl = "http://192.168.149.180:8000/ip"

    private var hasShownError = false
    private var hasMessageAppend = false

    private lateinit var scrollView: ScrollView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageDisplay = findViewById(R.id.messageDisplay)
        statusText = findViewById(R.id.statusText)
        scrollView = findViewById(R.id.scrollView)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)


        //connectWebSocket(serverUrl)
        swipeRefreshLayout.setOnRefreshListener {
            // custom toast.... below.........

            val inflater = layoutInflater
            val layout: View = inflater.inflate(R.layout.custom_toast, null)

            val textView = layout.findViewById<TextView>(R.id.toast_text)
            textView.text = "Refreshing..."

            val toast = Toast(this)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
            toast.setGravity(Gravity.CENTER,0,0)

            //Default Toast................
            //Toast.makeText(this,"Refreshing...",Toast.LENGTH_SHORT).show()
            fetchServerIpAndConnect()
            swipeRefreshLayout.isRefreshing = false
        }

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                appendMessage("\nMe: $messageText")
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

    private fun fetchServerIpAndConnect() {
        val request = Request.Builder().url(ipFetchUrl).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    statusText.text = "Failed to fetch IP ❌"
                    statusText.setTextColor(Color.RED)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val ip = JSONObject(body).getString("ip")
                val serverUrl = "ws://$ip:8000/ws/android"
                runOnUiThread { connectWebSocket(serverUrl) }
            }
        })
    }
    private fun connectWebSocket(serverUrl: String) {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                runOnUiThread {
                    statusText.text = "Connected ✅"
                    statusText.setTextColor(Color.GREEN)
                    if(!hasMessageAppend){
                        appendMessage("\n                          Connected to server\n                       waiting for the messages...\n")
                        hasMessageAppend = true
                    }
                    hasShownError = false
                    // Fade out error message if it exists
                    val text = messageDisplay.text.toString()
                    if (text.contains("Error:")) {
                        messageDisplay.postDelayed({
                            val lines = messageDisplay.text.split("\n")
                            val filteredLines = lines.filterNot { it.startsWith("Error:") }
                            messageDisplay.text = filteredLines.joinToString("\n")
                        }, 2000)
                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    appendMessage("\nWindows: $text")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                runOnUiThread {
                    statusText.text = "Disconnected ❌ Reconnecting..."
                    statusText.setTextColor(Color.RED)
                    if (!hasShownError) {
                        messageDisplay.append("\nError: ${t.message}")
                        hasShownError = true
                    }
                }
                reconnectWebSocket()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                runOnUiThread {
                    statusText.text = "Disconnected ❌"
                    statusText.setTextColor(Color.RED)
                }
                reconnectWebSocket()
            }
        })
    }

    private fun reconnectWebSocket() {
        Thread.sleep(3000)
        fetchServerIpAndConnect()
    }
    private fun appendMessage(message: String) {
        messageDisplay.append("\n$message")
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}