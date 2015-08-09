package hu.tilos.service3

import java.util.concurrent.TimeUnit._

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import hu.tilos.service3.UserActor.GetUser
import hu.tilos.service3.common.Role
import hu.tilos.service3.entities.AuthorObj
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object UserActor {

  case class GetUser(userName: String)

}


class UserActor(mongo: MongoProvider) extends Actor with ActorLogging {


  def getPermission(userBson: BSONDocument): (Option[AuthorObj], List[String]) = {
    if (userBson.getAs[BSONDocument]("author").isDefined) {
      val authorBson = Await.result(
        mongo.getDB.collection[BSONCollection]("author").find(BSONDocument("_id" -> userBson.getAs[BSONDocument]("author").get.getAs[BSONObjectID]("$id").get)).cursor[BSONDocument].headOption,
        Duration(5, SECONDS)
      )
      val author = AuthorObj(
        userBson.getAs[BSONObjectID]("_id").get.stringify,
        userBson.getAs[String]("name").get,
        userBson.getAs[String]("alias").get
      )
      (Some(author), List("/author/" + author.alias))
    } else {
      (None, List.empty[String])
    }
  }

  override def receive: Receive = LoggingReceive {
    case GetUser(username) => {
      val originalSender = sender()
      val user = mongo.getDB.collection[BSONCollection]("user").find(BSONDocument("username" -> username)).cursor[BSONDocument].headOption
      user.map { user => {
        val userBson = user.get
        var (author, permission) = getPermission(userBson)
        val userObj = UserObj(
          userBson.getAs[BSONObjectID]("_id").get.stringify,
          userBson.getAs[String]("username").get,
          Role(userBson.getAs[Int]("role_id").get),
          author
        )

        userObj
      }
      }.onSuccess { case user: UserObj => originalSender ! Some(user) }
    }
  }
}

