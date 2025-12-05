package com.example.unibiblion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID // Import necessário para gerar nomes de arquivo únicos

class Adm_Editar_Foto : AppCompatActivity() {

    private lateinit var imageViewPerfil: ImageView
    private lateinit var buttonEscolherFoto: Button
    private lateinit var buttonSalvar: Button

    private var imagemSelecionadaUri: Uri? = null

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val selecionarImagemLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imagemSelecionadaUri = it
            imageViewPerfil.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil_foto)

        imageViewPerfil = findViewById(R.id.profile_image)
        buttonEscolherFoto = findViewById(R.id.button_escolher_foto)
        buttonSalvar = findViewById(R.id.button_salvar)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        buttonEscolherFoto.setOnClickListener {
            abrirGaleria()
        }

        buttonSalvar.setOnClickListener {
            uploadImagemParaFirebase()
        }

        setupBottomNavigation(bottomNavigation)

        carregarFotoDePerfilAtual()
    }

    private fun carregarFotoDePerfilAtual() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            imageViewPerfil.setImageResource(R.drawable.ic_profile)
            return
        }

        firestore.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val imageUrl = document.getString("profileImageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(imageViewPerfil)
                    } else {
                        imageViewPerfil.setImageResource(R.drawable.ic_profile)
                    }
                } else {
                    imageViewPerfil.setImageResource(R.drawable.ic_profile)
                }
            }
            .addOnFailureListener {
                imageViewPerfil.setImageResource(R.drawable.ic_profile)
                Log.e("PerfilFoto", "Erro ao buscar foto de perfil", it)
            }
    }

    private fun abrirGaleria() {
        selecionarImagemLauncher.launch("image/*")
    }

    private fun uploadImagemParaFirebase() {
        val uri = imagemSelecionadaUri
        if (uri == null) {
            Toast.makeText(this, "Por favor, escolha uma foto primeiro.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // 1. Obter a URL da imagem antiga para poder excluí-la depois
        firestore.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                val oldImageUrl = document.getString("profileImageUrl")

                // 2. Criar um nome de arquivo único para a nova imagem
                val fileName = "${UUID.randomUUID()}.jpg"
                val newImageRef = storage.reference.child("profile_images/$fileName")

                // 3. Fazer o upload da nova imagem
                newImageRef.putFile(uri)
                    .addOnSuccessListener {
                        // 4. Se o upload for bem-sucedido, obter a nova URL de download
                        newImageRef.downloadUrl.addOnSuccessListener { newDownloadUri ->
                            // 5. Salvar a nova URL no Firestore
                            salvarUrlNoFirestore(newDownloadUri.toString(), oldImageUrl)
                        }
                    }
                    .addOnFailureListener { exception ->
                        setLoading(false)
                        Toast.makeText(this, "Erro no upload: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Erro ao obter dados do usuário para a troca.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarUrlNoFirestore(newImageUrl: String, oldImageUrl: String?) {
        val userId = auth.currentUser?.uid ?: return
        val userDocRef = firestore.collection("usuarios").document(userId)
        val data = hashMapOf<String, Any>("profileImageUrl" to newImageUrl)

        userDocRef.update(data)
            .addOnSuccessListener {
                // 6. Se a URL foi atualizada, excluir a imagem antiga (se existir)
                if (!oldImageUrl.isNullOrEmpty()) {
                    val oldImageRef = storage.getReferenceFromUrl(oldImageUrl)
                    oldImageRef.delete().addOnSuccessListener {
                        Log.d("PerfilFoto", "Imagem antiga excluída com sucesso.")
                    }.addOnFailureListener {
                        Log.w("PerfilFoto", "Falha ao excluir a imagem antiga.")
                    }
                }
                setLoading(false)
                Toast.makeText(this, "Foto atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Erro ao salvar referência da foto: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            buttonEscolherFoto.isEnabled = false
            buttonSalvar.isEnabled = false
        } else {
            buttonEscolherFoto.isEnabled = true
            buttonSalvar.isEnabled = true
        }
    }

    private fun setupBottomNavigation(bottomNavigation: BottomNavigationView) {
        bottomNavigation.selectedItemId = R.id.nav_perfil
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Adm_Tela_Central_Livraria::class.java))
                    finish(); true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, Adm_Tela_Mural_Noticias_Eventos::class.java))
                    finish(); true
                }
                R.id.nav_perfil ->
                    true
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Adm_Chat_Bot::class.java))
                    finish(); true
                }
                else -> false
            }
        }
    }
}
