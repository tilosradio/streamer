package hu.tilos.service3

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import akka.pattern.pipe
import hu.tilos.service3.UserActor.GetUser
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._

import scala.concurrent.ExecutionContext.Implicits.global

object UserActor {

  case class GetUser(userName: String)

}


class UserActor(mongo: MongoProvider) extends Actor with ActorLogging {

  implicit object UserReader extends BSONDocumentReader[UserObj] {
    override def read(bson: BSONDocument): UserObj = {
      UserObj(bson.getAs[BSONObjectID]("_id").get.stringify,
        bson.getAs[String]("username").get)
    }
  }

  override def receive: Receive = LoggingReceive {
    case GetUser(username) => {
      mongo.getDB.collection[BSONCollection]("user").find(BSONDocument("username" -> username)).cursor[UserObj].headOption pipeTo sender()
    }
    case msg => log.error("Unhandled message " + msg)
  }
}
