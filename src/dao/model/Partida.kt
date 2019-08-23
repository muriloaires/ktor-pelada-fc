package dao.model
import org.jetbrains.exposed.sql.Table


object Partidas : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val dataRealizacao = long("data_realizacao")
}

data class Partida(
    val id: Int,
    val dataRealizacao: Long
)

data class NewPartida(
    val id: Int?,
    val dataRealizacao: Long
)