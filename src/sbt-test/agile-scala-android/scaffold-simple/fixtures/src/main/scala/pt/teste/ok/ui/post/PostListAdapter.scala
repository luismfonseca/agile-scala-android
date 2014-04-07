package pt.teste.ok.ui.post

import android.app.Activity
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import android.text.format.DateFormat
import java.util.Date


import pt.teste.ok.models.Post
import pt.teste.ok.R

class PostListAdapter(val context: Activity, val items: Array[Post]) extends ArrayAdapter[Post](context, R.layout.item_post, items)  {

  case class ViewHolder(title: TextView, numberOfLikes: TextView, date: TextView, placeholder: ImageView)

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {

    // Reuse views
    val rowView =
      if (convertView == null) {
        val layoutInflater = LayoutInflater.from(context)
        val newRowView = layoutInflater.inflate(R.layout.item_post, null)

        // Configure view holder
        val viewHolder = new ViewHolder(
          newRowView.findViewById(R.id.item_post_title).asInstanceOf[TextView],
          newRowView.findViewById(R.id.item_post_number_of_likes).asInstanceOf[TextView],
          newRowView.findViewById(R.id.item_post_date).asInstanceOf[TextView],

          newRowView.findViewById(R.id.item_post_placeholder).asInstanceOf[ImageView]
        )
        newRowView.setTag(viewHolder)
        newRowView
      }
      else {
        convertView
      }

    // Fill data
    val viewHolder = rowView.getTag().asInstanceOf[ViewHolder]
    viewHolder.placeholder.setImageResource(R.drawable.ic_placeholder)
    if (items(position) != null) {
      viewHolder.title.setText(items(position).title)
      viewHolder.numberOfLikes.setText("" + items(position).numberOfLikes)
      viewHolder.date.setText(DateFormat.format("dd-MM-yyyy", items(position).date))

    }

    return rowView
  }

}
