package com.br.entrelinhas.data.remote

import android.content.Context
import android.net.Uri
import com.br.entrelinhas.data.config.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import java.io.IOException
import java.util.UUID

/**
 * Serviço responsável pelo upload de imagens para o Supabase Storage.
 *
 * Fluxo:
 *   URI local (galeria)
 *     → bytes
 *     → upload para bucket "covers"
 *     → URL pública
 *
 * Nenhuma lógica de validação de formulário reside aqui — apenas acesso ao Storage.
 */
class StorageService(private val client: SupabaseClient) {

    /**
     * Faz upload da imagem apontada por [uri] para o bucket "covers" do Supabase Storage
     * e retorna a URL pública resultante.
     *
     * @throws IOException se não for possível ler os bytes da imagem.
     * @throws Exception em caso de falha de rede ou erro no Storage.
     */
    suspend fun uploadBookCover(uri: Uri, context: Context): String {
        val contentResolver = context.contentResolver

        // Detecta o MIME type para determinar a extensão correta
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val extension = when (mimeType) {
            "image/png"  -> "png"
            "image/webp" -> "webp"
            "image/gif"  -> "gif"
            else         -> "jpg"
        }

        // Lê a imagem como bytes
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IOException("Não foi possível ler a imagem selecionada.")

        // Gera um path único para evitar colisões: android/{timestamp}-{uuid}.ext
        val filePath = generateFilePath(extension)

        // Upload para o Supabase Storage
        client.storage.from(SupabaseConfig.BUCKET_COVERS).upload(filePath, bytes) {
            upsert = false
        }

        return filePath
    }

    private fun generateFilePath(extension: String): String {
        val timestamp = System.currentTimeMillis()
        val uniqueId = UUID.randomUUID().toString().replace("-", "").take(12)
        val userId = client.auth.currentUserOrNull()?.id
        return "$userId/$timestamp-$uniqueId.$extension"
    }
}