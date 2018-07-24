package com.github.alexbogovich.typed.pingpong

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors

//https://doc.akka.io/docs/akka/2.5.14/typed/testing.html

sealed trait Cmd
case object CreateAnonymousChild extends Cmd
case class CreateChild(childName: String) extends Cmd
case class SayHelloToChild(childName: String) extends Cmd
case object SayHelloToAnonymousChild extends Cmd
case class SayHello(who: ActorRef[String]) extends Cmd

case class Ping(msg: String, response: ActorRef[Pong])
case class Pong(msg: String)

object PingPong {
  val childActor: Behaviors.Receive[String] = Behaviors.receiveMessage[String] { _ ⇒
    Behaviors.same[String]
  }

  val myBehavior: Behaviors.Receive[Cmd] = Behaviors.receivePartial[Cmd] {
    case (ctx, CreateChild(name)) ⇒
      ctx.spawn(childActor, name)
      Behaviors.same
    case (ctx, CreateAnonymousChild) ⇒
      ctx.spawnAnonymous(childActor)
      Behaviors.same
    case (ctx, SayHelloToChild(childName)) ⇒
      val child: ActorRef[String] = ctx.spawn(childActor, childName)
      child ! "hello"
      Behaviors.same
    case (ctx, SayHelloToAnonymousChild) ⇒
      val child: ActorRef[String] = ctx.spawnAnonymous(childActor)
      child ! "hello stranger"
      Behaviors.same
    case (_, SayHello(who)) ⇒
      who ! "hello"
      Behaviors.same
  }

  val echoActor: Behaviors.Receive[Ping] = Behaviors.receive[Ping] { (_, msg) ⇒
    msg match {
      case Ping(m, replyTo) ⇒
        replyTo ! Pong(m)
        Behaviors.same
    }
  }
}
