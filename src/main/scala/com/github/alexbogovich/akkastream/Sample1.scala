package com.github.alexbogovich.akkastream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.{ExecutionContextExecutor, Future}

object Sample1 extends App {
  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)

  val done: Future[Done] = source.runForeach(i ⇒ println(i))(materializer)

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  done.onComplete(_ ⇒ system.terminate())
}
