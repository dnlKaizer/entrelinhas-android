package com.br.entrelinhas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Banco de dados Room do app Entrelinhas.
 *
 * exportSchema = false desabilita a exportação do histórico de schema para fins
 * acadêmicos (em produção, manter true e versionar as migrações).
 *
 * Instanciado como singleton via [getInstance] para evitar múltiplas conexões.
 */
@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "entrelinhas_db"
                )
                    .fallbackToDestructiveMigration() // Recria o DB em caso de mudança de versão (desenvolvimento)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}