package com.github.alexbogovich

import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent._

object Sample4 extends App {
  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s ⇒ ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)

  val factorials = source.scan(BigInt(1))((acc, next) ⇒ acc * next)

  val result: Future[IOResult] =
    factorials.map(_.toString).runWith(lineSink("factorial_sample4.txt"))

  implicit val ec = system.dispatcher
  result.onComplete(_ ⇒ system.terminate())
}