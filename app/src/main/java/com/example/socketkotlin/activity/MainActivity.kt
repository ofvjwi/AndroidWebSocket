package com.example.socketkotlin.activity

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.socketkotlin.R
import okhttp3.*
import okio.ByteString

class MainActivity : AppCompatActivity() {
    var webSocket: WebSocket? = null
    private lateinit var textViewSocket: TextView
    private lateinit var counterTextView: TextView
    private var counter: Int = 988

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        );

        initViews()
    }

    private fun initViews() {
        textViewSocket = findViewById(R.id.tv_socket)
        counterTextView = findViewById(R.id.counter_text_view)
        connectToSocket()
    }


    private fun connectToSocket() {
        val client = OkHttpClient()

        val request: Request = Request.Builder().url("wss://ws.bitstamp.net").build()

        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@MainActivity.webSocket = webSocket
                webSocket.send(
                    "{\n" +
                            "    \"event\": \"bts:subscribe\",\n" +
                            "    \"data\": {\n" +
                            "        \"channel\": \"live_trades_btcusd\"\n" +
                            "    }\n" +
                            "}"
                )
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("@@@", "Receiving : $text")
                runOnUiThread {
                    textViewSocket.text = text
                    counterTextView.text = counter.toString()
                    counter++
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("@@@", "Receiving bytes : $bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("@@@", "Closing : $code / $reason")
                //webSocket.close(1000, null)
                //webSocket.cancel()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("@@@", "Error : " + t.message)
            }
        })
        client.dispatcher.executorService.shutdown()
    }
}

