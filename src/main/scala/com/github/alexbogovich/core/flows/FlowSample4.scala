package com.github.alexbogovich.core.flows

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent._
import scala.util.{Failure, Success}


object FlowSample4 extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  // connect the Source to the Sink, obtaining a RunnableGraph
  val sink = Sink.fold[Int, Int](0)(_ + _)
  val runnable: RunnableGraph[Future[Int]] =
    Source(1 to 10).toMat(sink)(Keep.right)

  // get the materialized value of the FoldSink
  val sum1: Future[Int] = runnable.run()
  val sum2: Future[Int] = runnable.run()

  // sum1 and sum2 are different Futures!

  implicit val ec = system.dispatcher
  sum1.onComplete(_ ⇒ system.terminate())

  val result = for {
    r1 <- sum1
    r2 <- sum2
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
