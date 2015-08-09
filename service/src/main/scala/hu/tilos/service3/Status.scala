package hu.tilos.service3

import hu.tilos.service3.Status.Ok
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object StatusJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val okFormat = jsonFormat1(Ok)
}

object Status {

  case class Ok(message: String)

  case class BadRequest(message: String)

}
