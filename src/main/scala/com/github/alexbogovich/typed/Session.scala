package com.github.alexbogovich.typed

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, Terminated}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

sealed trait RoomCommand
final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand

sealed trait SessionEvent
final case class SessionGranted(handle: ActorRef[PostMessage]) extends SessionEvent
final case class SessionDenied(reason: String) extends SessionEvent
final case class MessagePosted(screenName: String, message: String) extends SessionEvent

trait SessionCommand
final case class PostMessage(message: String) extends SessionCommand
private final case class NotifyClient(message: MessagePosted) extends SessionCommand


object ChatRoom {
  private final case class PublishSessionMessage(screenName: String, message: String)
    extends RoomCommand

  val behavior: Behavior[RoomCommand] =
    chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
    Behaviors.receive { (ctx, msg) ⇒
      ctx.log.info(s"chatRoom $ctx receive $msg")
      msg match {
        case GetSession(screenName, client) ⇒
          // create a child actor for further interaction with the client
          ctx.log.info(s"spawn session for $screenName $client")
          val ses = ctx.spawn(
            session(ctx.self, screenName, client),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name))
          ctx.log.info(s"tell SessionGranted to $client")
          client ! SessionGranted(ses)
          ctx.log.info(s"add session to pool")
          chatRoom(ses :: sessions)
        case PublishSessionMessage(screenName, message) ⇒
          ctx.log.info(s"create notify clients with $message from $screenName")
          val notification = NotifyClient(MessagePosted(screenName, message))
          ctx.log.info(s"send to all sessions")
          sessions foreach (_ ! notification)
          Behaviors.same
      }
    }

  private def session(
                       room:       ActorRef[PublishSessionMessage],
                       screenName: String,
                       client:     ActorRef[SessionEvent]): Behavior[SessionCommand] =
    Behaviors.receive { (ctx, msg) ⇒
      ctx.log.info(s"session $ctx receive $msg")
      msg match {
        case PostMessage(message) ⇒
          ctx.log.info(s"publish message $message from $screenName to $room")
          // from client, publish to others via the room
          room ! PublishSessionMessage(screenName, message)
          Behaviors.same
        case NotifyClient(message) ⇒
          ctx.log.info(s"send $message to $client")
          // published from the room
          client ! message
          Behaviors.same
      }
    }
}


object Gabbler {
  val gabbler: Behavior[SessionEvent] =
    Behaviors.receive { (ctx, msg) ⇒
      ctx.log.info(s"gabbler $ctx receive $msg")
      msg match {
        case SessionGranted(handle) ⇒
          ctx.log.info(s"post message 'Hello World!'")
          handle ! PostMessage("Hello World!")
          Behaviors.same
        case MessagePosted(screenName, message) ⇒
          ctx.log.info(s"message has been posted by '$screenName': $message")
          Behaviors.same
        case SessionDenied(reason) =>
          ctx.log.info(s"SessionDenied '$reason'")
          Behaviors.stopped
      }
    }

  def main(args: Array[String]): Unit = {
    val main: Behavior[NotUsed] =
      Behaviors.setup { ctx ⇒
        ctx.log.info("start init chatRoom")
        val chatRoom = ctx.spawn(ChatRoom.behavior, "chatroom")
        ctx.log.info("start init gabbler")
        val gabblerRef = ctx.spawn(gabbler, "gabbler")
        val gabbler2Ref = ctx.spawn(gabbler, "gabbler2")
        ctx.watch(gabblerRef)
        ctx.watch(gabbler2Ref)
        ctx.log.info(s"fire get sessin for $gabblerRef from $chatRoom")
        chatRoom ! GetSession("ol’ Gabbler", gabblerRef)
        chatRoom ! GetSession("ol’ Gabbler2", gabbler2Ref)

        Behaviors.receiveSignal {
          case (_, Terminated(ref)) ⇒
            Behaviors.stopped
        }
      }

    val system = ActorSystem(main, "ChatRoomDemo")
    Await.result(system.whenTerminated, 3.seconds)
  }
}