import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import persistence.FileIOJson
import play.api.libs.json.Json
import model.GameState

class FileIOJsonSpec extends AnyWordSpec with Matchers {

  "The FileIOJson" should {
    "save and load game state correctly" in {
      // Erstelle einen Testspielzustand
      val gameState = GameState("TestPlayer", 100)

      // Speichere den Spielzustand
      FileIOJson.save(gameState)

      // Lade den Spielzustand
      val loadedJson = FileIOJson.load

      // Überprüfe, ob der geladene JSON-String einen gültigen Spielzustand enthält
      val loadedGameStateResult = loadedJson.validate[GameState]

      loadedGameStateResult match {
        case JsSuccess(loadedGameState, _) =>
          // Überprüfe, ob der geladene Spielzustand dem gespeicherten Spielzustand entspricht
          loadedGameState shouldBe gameState
        case JsError(errors) =>
          fail(s"Failed to parse loaded JSON: $errors")
      }
    }
  }
}
