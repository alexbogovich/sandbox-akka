package com.github.alexbogovich.core.flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl._
import com.github.alexbogovich.core.flows.FlowSample2.{sum, system}

import scala.concurrent._
import scala.util.{Failure, Success}


object FlowSample1 extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val source = Source(1 to 10)
  val sink = Sink.fold[Int, Int](0)(_ + _)

  // connect the Source to the Sink, obtaining a RunnableGraph
  val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

  // materialize the flow and get the value of the FoldSink
  val sum: Future[Int] = runnable.run()

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
