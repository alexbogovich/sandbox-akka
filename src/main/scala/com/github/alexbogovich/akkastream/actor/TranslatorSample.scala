package com.github.alexbogovich.akkastream.actor

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.{ExecutionContextExecutor, Future}


class Translator extends Actor with ActorLogging{
  def receive = {
    case word: String ⇒
      // ... process message
      log.info(s"Actor $self recive $word")
      val reply = word.toUpperCase
      log.info(s"transform to $word")
      sender() ! reply // reply to the ask
  }
}

object TranslatorSample extends App {
  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val firstRef = system.actorOf(Props[Translator], "first-actor")

  implicit val askTimeout: Timeout = Timeout(5.seconds)
  val words: Source[String, NotUsed] =
    Source(List("hello", "hi", "HELLO", "HI", "HELlo", "HI", "HELlo", "hi"))

  val done: Future[Done] = words
    .ask[String](parallelism = 5)(firstRef)
    .runForeach(i ⇒ println(i))

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  done.onComplete(_ ⇒ system.terminate())
}
