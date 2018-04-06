package com.github.alexbogovich

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.github.alexbogovich.common._

object Sample3 extends App {
  val akkaTag = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = Source(Data.tweetList)

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  val result = tweets
    .map(_.hashtags) // Get all sets of hashtags ...
    .reduce(_ ++ _) // ... and reduce them to a single set, removing duplicates across all tweets
    .mapConcat(identity) // Flatten the stream of tweets to a stream of hashtags
    .map(_.name.toUpperCase) // Convert all hashtags to upper case
    .runWith(Sink.foreach(println)) // Attach the Flow to a Sink that will finally print the hashtags

  implicit val ec = system.dispatcher
  result.onComplete(_ ⇒ system.terminate())
}