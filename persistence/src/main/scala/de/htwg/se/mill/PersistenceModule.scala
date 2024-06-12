package persistence

import com.google.inject.AbstractModule
import databaseComponent.MongoDB.MongoDBDAO
import databaseComponent.Slick.SlickUserDAO

class PersistenceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[DBDAO]).to(classOf[MongoDBDAO])
  }
}
