package com.github.alexbogovich.akkastream.core.flows

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.github.alexbogovich.akkastream.core.flows.FlowSample3.{result, system}

import scala.concurrent._
import scala.util.{Failure, Success}


object FlowSample2 extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val source = Source(1 to 10)
  val sink = Sink.fold[Int, Int](0)(_ + _)

  // materialize the flow, getting the Sinks materialized value
  val sum: Future[Int] = source.runWith(sink)

  implicit val ec = system.dispatcher

  sum.onComplete({
    case Success(x) =>
      println(s"\nresult = $x")
      system.terminate()
    case Failure(e) =>
      e.printStackTrace
      system.terminate()
  })
}
