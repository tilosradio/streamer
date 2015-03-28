package hu.tilos.service3


import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import scaldi.akka.AkkaInjectable
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App with AkkaInjectable {

  implicit val system = ActorSystem("on-spray-can");

  implicit val injector = new scaldi.Module {
    bind[MyServiceActor] to new MyServiceActor
    bind[BookmarkActor] to injected[BookmarkActor]
    bind[UserActor] to injected[UserActor]
    bind[MongoProvider] to new MongoProvider
    bind[ActorSystem] to system
  }


  //val service = system.actorOf(Props[MyServiceActor], "demo-service")

  val service = injectActorRef[MyServiceActor]

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 6060)
}

