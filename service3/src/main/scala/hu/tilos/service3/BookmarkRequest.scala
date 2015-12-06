package hu.tilos.service3

import java.util.Date

import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, RootJsonFormat}


object PersonJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object DateJsonFormat extends RootJsonFormat[Date] {

    override def write(obj: Date) = JsNumber(obj.getTime)

    override def read(json: JsValue): Date = json match {
      case JsNumber(s: BigDecimal) => new Date(s.longValue())
      case _ => throw new RuntimeException("Invalid date format")
    }
  }

  implicit val bookmarkFormat = jsonFormat5(BookmarkRequest)
}

case class BookmarkRequest(
                            episodeId: String,
                            start: Date,
                            end: Date,
                            description: String,
                            fullEpisde: Boolean) {

}

