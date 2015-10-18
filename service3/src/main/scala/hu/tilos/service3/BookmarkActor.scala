package hu.tilos.service3

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import hu.tilos.service3.BookmarkActor.Save
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._

import scala.concurrent.ExecutionContext.Implicits.global

object BookmarkActor {

  case class Save(request: BookmarkRequest, user: UserObj)

}


class BookmarkActor(mongo: MongoProvider) extends Actor with ActorLogging {

  implicit object BookmarkWriter extends BSONDocumentWriter[BookmarkRequest] {
    def write(bookmark: BookmarkRequest): BSONDocument = BSONDocument(
      "description" -> bookmark.description,
      "episodeId" -> BSONObjectID(bookmark.episodeId),
      "start" -> BSONDateTime(bookmark.start.getTime),
      "end" -> BSONDateTime(bookmark.end.getTime),
      "full" -> bookmark.fullEpisde
    )
  }

  override def receive: Receive = LoggingReceive {
    case Save(bookmark, user) => {
      val requestor = sender

      val document = BookmarkWriter.write(bookmark) ++ ("creator" -> BSONObjectID(user.id))
      mongo.getDB.collection[BSONCollection]("bookmark").insert(document).map(
        lastError => requestor ! Status.Ok("Bookmark is saved")
      ).recover {

        case e: Throwable => {
          println("recover")
          requestor ! Status.BadRequest("Can't save bookmark: " + e.getMessage)
        }
      }
    }
  }
}
