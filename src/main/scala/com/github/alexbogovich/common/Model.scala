package com.github.alexbogovich.common

final case class Author(handle: String)

final case class Hashtag(name: String)

final case class Tweet(author: Author, timestamp: Long, body: String) {
  def hashtags: Set[Hashtag] = body.split(" ").collect {
    case t if t.startsWith("#") ⇒ Hashtag(t.replaceAll("[^#\\w]", ""))
  }.toSet
}