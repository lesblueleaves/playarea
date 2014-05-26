package localakka

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

object HelloRemote extends App{
 val system = ActorSystem("HelloRemoteSystem")
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! "The RemoteActor is alive"
}

class RemoteActor extends Actor{
  def receive ={
    case msg: String =>
      println(s"RemoteActor received message '$msg'")
      sender !"Hello from remoteActor!"
  }
}