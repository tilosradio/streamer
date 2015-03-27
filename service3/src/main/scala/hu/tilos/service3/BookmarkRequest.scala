package hu.tilos.service3

import java.util.Date

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object PersonJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val bookmarkFormat = jsonFormat5(BookmarkRequest)
}

case class BookmarkRequest(episodeId: String, start: Long, end: Long, description: String, fullEpisde: Boolean) {

}

