package hu.tilos.service3


import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import scaldi.akka.AkkaInjectable
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App with AkkaInjectable {

  implicit val injector = new scaldi.Module {
    bind[MyServiceActor] to new MyServiceActor
    bind[Bookmark] to injected [Bookmark]
    bind[MongoProvider] to new MongoProvider
  }

  implicit val system = ActorSystem("on-spray-can");
  //val service = system.actorOf(Props[MyServiceActor], "demo-service")

  val service = injectActorRef[MyServiceActor]

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}

