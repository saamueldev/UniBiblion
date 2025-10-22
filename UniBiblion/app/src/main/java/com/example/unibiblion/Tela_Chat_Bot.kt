package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_Chat_Bot : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var bottomNavigation: BottomNavigationView

    private val messagesList = mutableListOf(
        Message("Biblion: Como posso te ajudar?", false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_chat_bot)

        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        messageInput = findViewById(R.id.edit_text_message)
        sendButton = findViewById(R.id.button_send)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        setupChat()
        setupBottomNavigation()
        setupSendButton()
    }

    private fun setupChat() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager

        chatAdapter = ChatAdapter(messagesList)
        chatRecyclerView.adapter = chatAdapter
    }

    private fun setupSendButton() {
        sendButton.setOnClickListener {
            sendMessage()
        }

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
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
                R.id.nav_livraria-> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
                    finish()
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, Tela_Abrir_Noticia_Evento::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil::class.java))
                    finish()
                    true
                }
                R.id.nav_chatbot -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()

        if (messageText.isNotEmpty()) {
            val userMessage = Message(messageText, true)
            chatAdapter.addMessage(userMessage)

            simulateBotResponse(messageText)

            messageInput.text.clear()

            chatRecyclerView.scrollToPosition(messagesList.size - 1)
        }
    }

    private fun simulateBotResponse(userQuery: String) {
        val botReply = when {
            userQuery.contains("livro", ignoreCase = true) -> "Você pode verificar nosso acervo na seção 'Livraria' da barra inferior."
            userQuery.contains("horário", ignoreCase = true) -> "A biblioteca está aberta de segunda a sexta, das 8h às 22h."
            else -> "Obrigado pela sua mensagem. Como posso ser mais útil?"
        }

        val botMessage = Message(botReply, false)
        chatRecyclerView.postDelayed({
            chatAdapter.addMessage(botMessage)
            chatRecyclerView.scrollToPosition(messagesList.size - 1)
        }, 500)
    }
}