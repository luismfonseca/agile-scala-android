package pt.teste.ok.models

import scala.slick.lifted.MappedTo
import java.util.Date

class PostId(val value: Int) extends MappedTo[Int]

case class Post(title: String, likes: Int, date: Date, postId: PostId = new PostId(-1))
{
  
}
