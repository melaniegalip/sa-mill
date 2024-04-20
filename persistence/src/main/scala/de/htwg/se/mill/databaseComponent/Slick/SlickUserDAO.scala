package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}


class SlickUserDAO {
    
    private val databaseDB: String = sys.env.getOrElse("POSTGRES_DATABASE", "tbl")
    private val databaseUser: String = sys.env.getOrElse("POSTGRES_USER", "postgres")
    private val databasePassword: String = sys.env.getOrElse("POSTGRES_PASSWORD", "postgres")
    private val databasePort: String = sys.env.getOrElse("POSTGRES_PORT", "5432")
    private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", "localhost")
    //private val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "database")
    private val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"

    val database = Database.forURL(
        url = databaseUrl,
        driver = "org.postgresql.Driver",
        user = databaseUser,
        password = databasePassword
    )

    
    val player = TableQuery[PlayerTable]
    val board = TableQuery[BoardTable]
    val game = TableQuery[GameTable]
    val field = TableQuery[FieldTable]
    val gameState = TableQuery[GameStateTable]
    

    def createTables(): Future[Unit] = {

        val createPlayerTableAction = player.schema.createIfNotExists
        val createBoardTableAction = board.schema.createIfNotExists
        val createGameTableAction = game.schema.createIfNotExists
        val createFieldTableAction = field.schema.createIfNotExists
        val createGameStateTableAction = gameState.schema.createIfNotExists

        val combinedAction = for {
            _ <- createPlayerTableAction
            _ <- createBoardTableAction
            _ <- createGameTableAction
            _ <- createFieldTableAction
            _ <- createGameStateTableAction
        } yield ()

        database.run(combinedAction)
    }

    def closeDatabase(): Unit = {
        database.close()
    }

}