package hu.tilos.service3

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import hu.tilos.service3.Bookmark.Save
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter}

import scala.concurrent.ExecutionContext.Implicits.global

object Bookmark {

  case class Save(request: BookmarkRequest)

}


class Bookmark(mongo: MongoProvider) extends Actor with ActorLogging {

  implicit object BookmarkWriter extends BSONDocumentWriter[BookmarkRequest] {
    def write(bookmark: BookmarkRequest): BSONDocument = BSONDocument(
      "description" -> bookmark.description)
  }

  override def receive: Receive = LoggingReceive {
    case Save(bookmark) => {
      val requestor = sender
      mongo.getDB.collection[BSONCollection]("bookmark").insert(bookmark).map(
        lastError => requestor ! Status.Ok("Bookmark is saved")
      ).recover {
        case _ => sender ! Status.BadRequest("Can't save bookmark")
      }
      //        case _ => sender ! Status.BadRequest("Can't save bookmark")
      //      }
      //        last: LastError => {
      //          println("kast")
      //          println("sender" + sender)
      //          sender ! Status.Ok("Bookmark is saved: " + last.get("_id"))
      //        }
      //      }.recover({
      //        case _ => sender ! Status.BadRequest("Can't save bookmark")
      //      }
      //      )

    }
  }
}
