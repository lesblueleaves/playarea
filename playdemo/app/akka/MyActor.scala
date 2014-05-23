//package akka
//
//import akka.actor.Actor
//import akka.actor.Props
//import akka.event.Logging
//import akka.actor.ActorSystem
//
//class MyActor(myName:String) extends Actor{
//	val log = Logging(context.system, this)
//	
//	def receive = {
//	case "hello" => log.info("hello from %s".format(myName))
//	case _ => log.info("huh?")
//	}
//}
//
//object Main extends App {
//  val system = ActorSystem("HelloSystem")
//  // default Actor constructor
////  val helloActor = system.actorOf(Props[MyActor], name = "helloactor")
//  val helloActor = system.actorOf(Props(new MyActor("Fred")), name = "helloactor")
//  
//  helloActor ! "hello"
//  
//  helloActor ! "buenos dias"
//}