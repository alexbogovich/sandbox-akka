package com.github.alexbogovich.akkastream.typed

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.typed.scaladsl.ActorSource

import scala.concurrent.ExecutionContextExecutor

trait Protocol
case class Message(msg: String) extends Protocol
case object Complete extends Protocol
case class Fail(ex: Exception) extends Protocol


object MessageProtocol extends App {
  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val source: Source[Protocol, ActorRef[Protocol]] = ActorSource.actorRef[Protocol](
    completionMatcher = {
      case Complete ⇒
    },
    failureMatcher = {
      case Fail(ex) ⇒ ex
    },
    bufferSize = 8,
    overflowStrategy = OverflowStrategy.fail
  )

  val ref = source.collect {
    case Message(msg) ⇒ msg
  }.to(Sink.foreach(println)).run()

  ref ! Message("msg1")
  ref ! Message("msg2")
  ref ! Message("msg3")

  implicit val ec: ExecutionContextExecutor = system.dispatcher
}
