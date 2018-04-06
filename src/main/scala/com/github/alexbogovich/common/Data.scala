package com.github.alexbogovich.common

import java.lang.System.currentTimeMillis

object Data {
  val tweetList: List[Tweet] = Tweet(Author("rolandkuhn"), currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("patriknw"), currentTimeMillis, "#akka !") ::
    Tweet(Author("bantonsson"), currentTimeMillis, "#akka !") ::
    Tweet(Author("drewhk"), currentTimeMillis, "#akka !") ::
    Tweet(Author("ktosopl"), currentTimeMillis, "#akka on the rocks!") ::
    Tweet(Author("mmartynas"), currentTimeMillis, "wow #akka !") ::
    Tweet(Author("akkateam"), currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("bananaman"), currentTimeMillis, "#bananas rock!") ::
    Tweet(Author("appleman"), currentTimeMillis, "#apples rock!") ::
    Tweet(Author("drama"), currentTimeMillis, "we compared #apples to #oranges!") ::
    Nil
}