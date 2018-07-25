package com.github.alexbogovich.typed.alarm

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

import scala.io.StdIn

//https://www.youtube.com/watch?time_continue=1&v=k9rwU8G1Oi0

sealed trait AlarmMessage
case class EnableAlarm(pinCode: String) extends AlarmMessage
case class DisableAlarm(pinCode: String) extends AlarmMessage
case object ActivityEvent extends AlarmMessage

sealed trait SensorEvent
case object WindowOpened extends SensorEvent


object Alarm {
  def enabled(pinCode: String): Behavior[AlarmMessage] =
    Behaviors.receive[AlarmMessage] { (ctx, msg) =>
      msg match {
        case DisableAlarm(enteredCode) if enteredCode == pinCode =>
          ctx.log.info("Disable alarm")
          disabled(pinCode)
        case ActivityEvent =>
          ctx.log.info("OEOEOEOE alarm, activity detected!")
          Behaviors.same
        case _ =>
          ctx.log.info("i dont give a f about this event")
          //          Behaviors.ignore ignore any intreactions
          Behaviors.same
      }
    }

  def disabled(pinCode: String): Behavior[AlarmMessage] =
    Behaviors.receive[AlarmMessage] { (ctx, msg) =>
      msg match {
        case EnableAlarm(enteredCode) if enteredCode == pinCode =>
          ctx.log.info("Enable alarm")
          enabled(pinCode)
        case _ =>
          ctx.log.info("i dont give a f about this event")
          //          Behaviors.ignore ignore any intreactions
          Behaviors.same
      }
    }


}

object MainAlarm {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem(Alarm.enabled("0000"), "AlarmSystem")
    val alarmRef: ActorRef[AlarmMessage] = system


    alarmRef ! DisableAlarm("1234")
    alarmRef ! ActivityEvent
    alarmRef ! DisableAlarm("0000")
    alarmRef ! ActivityEvent
    alarmRef ! EnableAlarm("0000")

    println(">>> Press ENTER to exit <<<")
    try StdIn.readLine()
    finally system.terminate()
  }
}