package hu.tilos.service3

import java.util.concurrent.TimeUnit

import org.specs2.mutable.Specification
import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class UserActorTest extends Specification {
  "The 'Hello world' string should" >> {
    "contain 11 characters" >> {
      "Hello world" must haveSize(11)
    }
    "start with 'Hello'" >> {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" >> {
      "Hello world" must endWith("world")
    }
  }
  "User Actor" >> {
    "use mongodb" >> {
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost"))

      // Gets a reference to the database "plugin"
      val db = connection("tilos")

      val result: Future[Option[BSONDocument]] = db.collection[BSONCollection]("user").find(BSONDocument("username" -> "test.admin")).cursor[BSONDocument].headOption
      result.map(s => println(s)).recover{case e:Throwable => println("ajjaj")}
      Await.result(result, Duration(5, TimeUnit.SECONDS))
      println(result.value)
      val s = "asd"
      s must haveSize(11)
    }
    //    "retrieve an example user" >> {
    //      implicit val system = ActorSystem("on-spray-can");
    //      val actorRef = TestActorRef[UserActor]
    //      val actor = actorRef.underlyingActor
    //      actor receive Save(BookmarkRequest("asd", new java.util.Date(), new java.util.Date(), "asd", false), UserObj("id", "test.admin"))
    //    }
  }
}
