package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class Tela_Chat_Bot : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var generativeModel: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_chat_bot)

        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        messageInput = findViewById(R.id.edit_text_message)
        sendButton = findViewById(R.id.button_send)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        try {
            val systemInstructionText = """
                você é um assistente virtual para um app de biblioteca
                quero que seja um especialista em livros 
                caso demonstrem estresse quero que fique gago 
                
                """.trimIndent()

            val systemInstruction = com.google.ai.client.generativeai.type.content {
                role = "system"
                text(systemInstructionText)
            }

            generativeModel = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY,
                systemInstruction = systemInstruction
            )
        } catch (e: Exception) {
            Log.e("GeminiChat", "Erro ao inicializar o modelo. Verifique a API Key e a configuração do build.", e)
        }


        setupRecyclerView()
        setupBottomNavigation()
        setupSendButton()

        addMessageToChat("Biblion: Como posso te ajudar?", "model")
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(mutableListOf())
        chatRecyclerView.adapter = chatAdapter

        val layoutManager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = layoutManager
    }

    private fun getGeminiResponse(prompt: String) {

        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)

                response.text?.let { botResponse ->
                    addMessageToChat(botResponse, "model")
                }
            } catch (e: Exception) {
                Log.e("GeminiChat", "Erro ao chamar a API do Gemini", e)
                addMessageToChat("Desculpe, ocorreu um erro. Verifique sua conexão e tente novamente.", "model")
            }
        }
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()

        if (messageText.isNotEmpty()) {
            addMessageToChat(messageText, "user")
            messageInput.text.clear()

            getGeminiResponse(messageText)
        }
    }

    private fun addMessageToChat(text: String, role: String) {
        val message = Message(
            role = role,
            content = text
        )
        chatAdapter.addMessage(message)
        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }


    private fun setupSendButton() {
        sendButton.setOnClickListener {
            sendMessage()
        }

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun setupBottomNavigation() {
        NavigationHelper.setupBottomNavigation(this, bottomNavigation, R.id.nav_chatbot)
    }

}
