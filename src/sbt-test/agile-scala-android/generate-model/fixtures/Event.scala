package pt.teste.ok.models

import scala.slick.lifted.MappedTo
import java.net.URL
import java.util.Calendar

class EventId(val value: Int) extends MappedTo[Int]

case class Event(studentBranch: String, points: Int, link: URL, calendar: Calendar, eventId: EventId = new EventId(-1))
{
  
}
