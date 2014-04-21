package pt.testing.whaa.models

import java.util.Date

case class Post(author: Author, date: Date, title: String, content: String, coments: Array[Comment])
{
  
}
