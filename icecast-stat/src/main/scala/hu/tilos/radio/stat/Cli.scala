package hu.tilos.radio.stat

import java.net.HttpURLConnection
import java.util.Base64

import com.typesafe.config.{Config, ConfigFactory}
import org.xml.sax.InputSource
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._

import scala.concurrent.ExecutionContext.Implicits.global;

object Cli {
  def main(args: Array[String]) {
    val config: Config = ConfigFactory.load()
    val uconn: HttpURLConnection = new java.net.URL("http://stream.tilos.hu/admin/stats.xml").openConnection().asInstanceOf[HttpURLConnection];
    val authorization: String = config.getString("stream.user") + ":" + config.getString("stream.password")
    uconn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder.encodeToString(authorization.getBytes()))
    uconn.setRequestMethod("GET");
    uconn.setAllowUserInteraction(false);
    uconn.setDefaultUseCaches(false);
    uconn.setDoInput(true);
    uconn.setDoOutput(false);
    uconn.setInstanceFollowRedirects(true);
    uconn.setUseCaches(false);
    val xml = scala.xml.XML.load(new InputSource(uconn.getInputStream))
    var statRecord: Map[String, BSONValue] = xml \\ "source" map { source =>
      (source \@ "mount" substring 1, new BSONInteger((source \ "listeners" text).toInt))

    } toMap

    statRecord += "time" -> new BSONDateTime(new java.util.Date().getTime())
    val driver = new MongoDriver
    val connection = driver.connection(List(config.getString("mongo.server")))

    // Gets a reference to the database "plugin"
    val db = connection(config.getString("mongo.db"))


    val collection: BSONCollection = db.collection[BSONCollection](config.getString("mongo.collection"))

    val document = BSONDocument(statRecord)

    collection.insert(document).onComplete {
      case scala.util.Failure(e) => throw e
      case scala.util.Success(lastError) => {
        System.exit(0)
      }
    }
  }

}
