package hu.tilos.service3


import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import hu.tilos.service3.Status.{BadRequest, Ok}
import scaldi.Injector
import scaldi.akka.AkkaInjectable
import spray.http.MediaTypes._
import spray.http.StatusCodes
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

  val myRoute =

    path("api" / "bookmark") {
      post {
        respondWithMediaType(`application/json`) {
          authenticate(authenticator) { user =>
            entity(as[BookmarkRequest]) { bookmark => requestContext =>
              import StatusJsonSupport._
              (bookmarkWorker ? BookmarkActor.Save(bookmark, user)).map {
                case ok: Ok => requestContext.complete(ok.toJson.prettyPrint)
                case error: BadRequest => {
                  println("bad request")
                  requestContext.complete(StatusCodes.BadRequest, error.message)
                }
                case _ => println("Unkown message")
              }
            }
          }
        }
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