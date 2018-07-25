package com.github.alexbogovich.typed.pingpong

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import com.github.alexbogovich.typed.pingpong.PingPong.echoActor
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class PingPongActorTest extends WordSpec with ActorTestKit with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdownTestKit()

  "typed actor test" in {
    val probe = TestProbe[Pong]()
    val pinger = spawn(echoActor, "ping")
    pinger ! Ping("hello", probe.ref)
    probe.expectMessage(Pong("hello"))
  }

  "typed actor test anonymously spawn" in {
    val probe = TestProbe[Pong]()
    val pinger = spawn(echoActor)
    pinger ! Ping("hello", probe.ref)
    probe.expectMessage(Pong("hello"))
  }
}
