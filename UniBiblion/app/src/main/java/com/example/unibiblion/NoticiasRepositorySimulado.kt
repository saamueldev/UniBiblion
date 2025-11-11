package com.example.unibiblion

// Objeto Singleton para simular o banco de dados/API
object NoticiasRepositorySimulado {

    // Lista mutável que armazena todas as notícias
    private val noticias = mutableListOf<Noticia>()

    init {
        // Inicializa com dados de exemplo (MOCK DATA)
        val corpoLongoExemplo = "Este é o corpo completo do artigo. Aqui você encontra todos os detalhes, parágrafos e informações."

        // Exemplo 1: Imagem Grande
        noticias.add(
            Noticia(
                titulo = "Grande Evento: Palestra sobre IA na Biblioteca.",
                preview = "A universidade receberá o Dr. Silva para discutir o impacto da IA na pesquisa e educação. Inscrições abertas no portal do aluno.",
                corpo = corpoLongoExemplo,
                urlImagem = "url_inicial_grande", // ID simulado
                tipoLayout = Noticia.TIPO_IMAGEM_GRANDE
            )
        )
        // Exemplo 2: Imagem Lateral
        noticias.add(
            Noticia(
                titulo = "Novos Livros Chegando ao Acervo.",
                preview = "Confira as novidades na área de tecnologia e ciências humanas.",
                corpo = corpoLongoExemplo,
                urlImagem = "url_inicial_lateral", // ID simulado
                tipoLayout = Noticia.TIPO_IMAGEM_LATERAL
            )
        )
    }

    /**
     * Obtém todos os anúncios, usado pela Adm_Tela_Mural_Noticias_Eventos no onResume().
     */
    fun getAllNoticias(): List<Noticia> {
        return noticias.toList()
    }

    /**
     * Adiciona um novo anúncio, usado pela Adm_Tela_Criacao_Anuncio_Eventos.
     */
    fun addNoticia(noticia: Noticia) {
        noticias.add(0, noticia) // Adiciona no início para aparecer no topo do mural
    }

    // --- NOVO: Suporte para EDIÇÃO ---
    /**
     * Atualiza uma notícia existente, identificada pelo seu ID (urlImagem).
     * Usado pela Adm_Tela_Criacao_Anuncio_Eventos em modo de edição.
     */
    fun updateNoticia(noticiaAtualizada: Noticia, idItem: String) {
        // Encontra o índice do item a ser substituído
        val index = noticias.indexOfFirst { it.urlImagem == idItem }
        if (index != -1) {
            noticias[index] = noticiaAtualizada // Substitui o objeto antigo pelo novo
        }
    }

    // --- NOVO: Suporte para EXCLUSÃO ---
    /**
     * Remove uma notícia, identificada pelo seu ID (urlImagem).
     * Usado pela Adm_Tela_Criacao_Anuncio_Eventos em modo de exclusão.
     */
    fun deleteNoticia(idItem: String) {
        // Remove todos os itens que correspondem ao ID (deve haver apenas um)
        noticias.removeAll { it.urlImagem == idItem }
    }
}