package localakka

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

object Local extends App {
  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor") // the local actor
  localActor ! "START"
}

class LocalActor extends Actor {
  val remote = context.actorSelection("akka://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")
  var counter = 0

  def receive = {
    case "START" =>
      remote ! "Hello from the LocalActor"
    case msg: String =>
      println(s"LocalActor received message: '$msg'")
      if (counter < 5) {
        sender ! "Hello back to you"
        counter += 1
      }
  }

}