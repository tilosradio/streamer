package hu.tilos.service3

import akka.actor.ActorRef
import akka.pattern.ask
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import com.typesafe.config.ConfigFactory
import hu.tilos.service3.UserActor.GetUser
import spray.http.{HttpCredentials, HttpHeader, HttpRequest, OAuth2BearerToken}
import spray.routing.RequestContext

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class JwtAuthenticator(userActor: ActorRef) extends spray.routing.authentication.HttpAuthenticator[UserObj] {

  implicit val timeout = akka.util.Timeout(5 second)

  val config = ConfigFactory.load()

  override implicit def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global

  override def getChallengeHeaders(httpRequest: HttpRequest): List[HttpHeader] = {
    httpRequest.headers.filter(_.name equals "Authorization")
  }

  override def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext): Future[Option[UserObj]] = {
    credentials match {
      case Some(OAuth2BearerToken(token)) => {
        val parsedJWT: SignedJWT = SignedJWT.parse(token)
        val jwsObject = JWSObject.parse(token);
        val verifier = new MACVerifier(config.getString("tilos.jwt.secret").getBytes);
        val verifiedSignature = jwsObject.verify(verifier);
        if (verifiedSignature) {
          (userActor ? GetUser(parsedJWT.getJWTClaimsSet.getStringClaim("username"))).asInstanceOf[Future[Option[UserObj]]]
        } else {
          Future(None)
        }
      }
      case _ => Future(None)
    }
  }
}