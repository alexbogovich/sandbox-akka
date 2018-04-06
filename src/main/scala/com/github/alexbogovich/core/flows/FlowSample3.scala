package com.github.alexbogovich.core.flows

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent._
import scala.util.{Failure, Success}


object FlowSample3 extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val source = Source(1 to 10)
  source.map(_ ⇒ 0) // has no effect on source, since it's immutable
  val sum = source.runWith(Sink.fold(0)(_ + _)) // 55

  val zeroes = source.map(_ ⇒ 0) // returns new Source[Int], with `map()` appended
  val sumZero = zeroes.runWith(Sink.fold(0)(_ + _)) // 0
  // materialize the flow, getting the Sinks materialized value

  implicit val ec = system.dispatcher

  val result = for {
    r1 <- sum
    r2 <- sumZero
  } yield (r1, r2)

  result.onComplete({
    case Success(x) =>
      println(s"\nresult = $x")
      system.terminate()
    case Failure(e) =>
      e.printStackTrace
      system.terminate()
  })
}
