package pt.testing.whaa.ui.comment

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


import pt.testing.whaa.models.Comment
import pt.testing.whaa.R

class CommentListAdapter(val context: Activity, val items: Array[Comment]) extends ArrayAdapter[Comment](context, R.layout.item_comment, items)  {

  case class ViewHolder(date: TextView, text: TextView, placeholder: ImageView)

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {

    // Reuse views
    val rowView =
      if (convertView == null) {
        val layoutInflater = LayoutInflater.from(context)
        val newRowView = layoutInflater.inflate(R.layout.item_comment, null)

        // Configure view holder
        val viewHolder = new ViewHolder(
          newRowView.findViewById(R.id.item_comment_date).asInstanceOf[TextView],
          newRowView.findViewById(R.id.item_comment_text).asInstanceOf[TextView],

          newRowView.findViewById(R.id.item_comment_placeholder).asInstanceOf[ImageView]
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
      viewHolder.date.setText(DateFormat.format("dd-MM-yyyy", items(position).date))
      viewHolder.text.setText(items(position).text)

    }

    return rowView
  }

}
