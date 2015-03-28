package hu.tilos.service3


import java.io.{ByteArrayInputStream, InputStream, OutputStream}

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import hu.tilos.service3.Status.{BadRequest, Ok}
import hu.tilos.service3.common.Role
import hu.tilos.service3.common.Role._
import scaldi.Injector
import scaldi.akka.AkkaInjectable
import spray.http.MediaTypes._
import spray.http.{BodyPart, MultipartFormData, StatusCodes}
import spray.httpx.SprayJsonSupport
import spray.json._
import spray.routing._

import scala.concurrent.duration._


class MyServiceActor(implicit inj: Injector) extends Actor with HttpService with SprayJsonSupport with AkkaInjectable {

  def actorRefFactory = context

  def receive = runRoute(myRoute)

  implicit val timeout = Timeout(5 second)

  val bookmarkWorker = injectActorRef[BookmarkActor]("bookmark")

  val userWorker = injectActorRef[UserActor]

  val authenticator = new JwtAuthenticator(userWorker)

  implicit def executionContext = actorRefFactory.dispatcher

  import PersonJsonSupport._

  def checkPermission(user: UserObj): Boolean = {
    return true
  }

  def isInRole(user: UserObj, role: Role): Boolean = {
    user.role.id >= role.id
  }

  val myRoute =

    path("api" / "v2" / "bookmark") {
      post {
        respondWithMediaType(`application/json`) {
          authenticate(authenticator) { user =>
            authorize(isInRole(user, Role.Author)) {
              entity(as[BookmarkRequest]) { bookmark => requestContext =>
                import StatusJsonSupport._
                (bookmarkWorker ? BookmarkActor.Save(bookmark, user)).map {
                  case ok: Ok => requestContext.complete(ok.toJson.prettyPrint)
                  case error: BadRequest => {
                    requestContext.complete(StatusCodes.BadRequest, error.message)
                  }
                  case _ => println("Unkown message")
                }
              }
            }
          }
        }
      }

    } ~
      path("api" / "v2" / "upload") {
        post {
          respondWithMediaType(`application/json`) {
            authenticate(authenticator) { user =>
              authorize(isInRole(user, Role.Author)) {
                entity(as[MultipartFormData]) { formData =>
                  detach() {
                    complete {
                      val details = formData.fields.map {
                        case BodyPart(entity, headers) =>
                          //val content = entity.buffer
                          val content = new ByteArrayInputStream(entity.data.toByteArray)
                          //                  val contentType = headers.find(h => h.is("content-type")).get.value
                          val contentType = "text/plain"
                          val fileName = headers.find(h => h.is("content-disposition")).get.value.split("filename=").last
                          val result = saveAttachment(fileName, content)
                          (contentType, fileName, result)
                        case t: Throwable => println("Unhandles error message " + t.getMessage)
                        case _ => println("Unhandles message")
                      }
                      s"""{"status": "Processed POST request, details=$details" }"""
                    }
                  }
                }
              }
            }
          }
        }
      }

  private def saveAttachment(fileName: String, content: InputStream): Boolean = {
    saveAttachment[InputStream](fileName, content, {
      (is, os) =>
        val buffer = new Array[Byte](16384)
        Iterator
          .continually(is.read(buffer))
          .takeWhile(-1 !=)
          .foreach(read => os.write(buffer, 0, read))
    }
    )
  }

  private def saveAttachment[T](fileName: String, content: T, writeFile: (T, OutputStream) => Unit): Boolean = {
    try {
      val fos = new java.io.FileOutputStream(fileName)
      writeFile(content, fos)
      fos.close()
      true
    } catch {
      case _ => false
    }
  }

  private def createResponder(requestContext: RequestContext) =
    context.actorOf(Props(new Responder(requestContext)))
}

class Responder(requestContext: RequestContext) extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case Ok(_) => {
      println("Responding")
      requestContext.complete("asd")
      killYourself
    }

  }

  private def killYourself = self ! PoisonPill
}