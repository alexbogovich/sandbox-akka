package com.github.alexbogovich.akkastream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.{Done, NotUsed}
import com.github.alexbogovich.akkastream.common.{Author, Data, Hashtag, Tweet}

import scala.concurrent.Future

object Sample7 extends App {
  val akkaTag = Hashtag("#akka")

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()
  val tweets: Source[Tweet, NotUsed] = Source(Data.tweetList)

  val writeAuthors: Sink[Author, Future[Done]] = Sink.foreach[Author](println(_))
  val writeHashtags: Sink[Hashtag, Future[Done]] = Sink.foreach[Hashtag](println(_))
  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val bcast = b.add(Broadcast[Tweet](2))
    tweets ~> bcast.in
    bcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
    bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashtags
    ClosedShape
  })
  g.run()
  system.terminate()
}
