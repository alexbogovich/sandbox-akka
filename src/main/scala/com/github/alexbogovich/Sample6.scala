package com.github.alexbogovich

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.github.alexbogovich.common._


object Sample6 extends App {
  val akkaTag = Hashtag("#akka")

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()
  val tweets: Source[Tweet, NotUsed] = Source(Data.tweetList)

  val authors: Source[Author, NotUsed] =
    tweets
      .filter(_.hashtags.contains(akkaTag))
      .map(_.author)
  val hashtags: Source[Hashtag, NotUsed] = tweets.mapConcat(_.hashtags.toList)

  val resultAuthors = authors.runForeach(println)
  val resultHashTag = hashtags.runForeach(println)

  implicit val ec = system.dispatcher
  resultAuthors.zip(resultHashTag).onComplete(_ ⇒ system.terminate())
}