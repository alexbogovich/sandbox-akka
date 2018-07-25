package com.github.alexbogovich.typed.pingpong

import akka.actor.ActorSystem
import akka.actor.testkit.typed.Effect.{Spawned, SpawnedAnonymous}
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.testkit.TestKit
import com.github.alexbogovich.typed.pingpong.PingPong.{childActor, myBehavior}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._


@RunWith(classOf[JUnitRunner])
class PingPongTest extends WordSpec with Matchers with BeforeAndAfterAll {

//  override def afterAll {
//    shutdownTestKit()
//  }

  "Spawning children" in {
    val testKit = BehaviorTestKit(myBehavior)
    testKit.run(CreateChild("child"))
    testKit.expectEffect(Spawned(childActor, "child"))
  }

  "Spawning children anonymously" in {
    val testKit = BehaviorTestKit(myBehavior)
    testKit.run(CreateAnonymousChild)
    testKit.expectEffect(SpawnedAnonymous(childActor))
  }

  "Sending messages" in {
    val testKit = BehaviorTestKit(myBehavior)
    val inbox = TestInbox[String]()
    testKit.run(SayHello(inbox.ref))
    inbox.expectMessage("hello")
  }

  "Sending messages another" in {
    val testKit = BehaviorTestKit(myBehavior)
    testKit.run(SayHelloToChild("child"))
    val childInbox = testKit.childInbox[String]("child")
    childInbox.expectMessage("hello")
  }

  "Sending messages another anonymously" in {
    val testKit = BehaviorTestKit(myBehavior)
    testKit.run(SayHelloToAnonymousChild)
    // Anonymous actors are created as: $a $b etc
    val childInbox = testKit.childInbox[String](s"$$a")
    childInbox.expectMessage("hello stranger")
  }
}
