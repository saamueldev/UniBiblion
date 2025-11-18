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

    // --- Componentes da UI ---
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var bottomNavigation: BottomNavigationView

    // --- Modelo Gemini ---
    private lateinit var generativeModel: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_chat_bot)

        // --- Inicialização da UI ---
        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        messageInput = findViewById(R.id.edit_text_message)
        sendButton = findViewById(R.id.button_send)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 1. INICIALIZA O MODELO GEMINI com a sua API Key segura
        try {
            generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash-latest",
                apiKey = BuildConfig.GEMINI_API_KEY
            )
        } catch (e: Exception) {
            Log.e("GeminiChat", "Erro ao inicializar o modelo. Verifique a API Key e a configuração do build.", e)
        }


        setupRecyclerView()
        setupBottomNavigation()
        setupSendButton()

        // Adiciona uma mensagem de boas-vindas do bot
        addMessageToChat("Biblion: Como posso te ajudar?", "model")
    }

    private fun setupRecyclerView() {
        // Inicializa o adapter com uma lista vazia. As mensagens serão adicionadas dinamicamente.
        chatAdapter = ChatAdapter(mutableListOf())
        chatRecyclerView.adapter = chatAdapter

        val layoutManager = LinearLayoutManager(this)
        // Opcional: para o teclado não sobrepor a última mensagem
        // layoutManager.stackFromEnd = true
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
        bottomNavigation.selectedItemId = R.id.nav_chatbot

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
                    finish()
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, NoticiasActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil::class.java))
                    finish()
                    true
                }
                R.id.nav_chatbot -> true
                else -> false
            }
        }
    }

}
