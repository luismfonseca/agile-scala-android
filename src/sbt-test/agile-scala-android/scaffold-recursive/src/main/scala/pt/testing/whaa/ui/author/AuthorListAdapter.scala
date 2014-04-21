package pt.testing.whaa.ui.author

import android.app.Activity
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView



import pt.testing.whaa.models.Author
import pt.testing.whaa.R

class AuthorListAdapter(val context: Activity, val items: Array[Author]) extends ArrayAdapter[Author](context, R.layout.item_author, items)  {

  case class ViewHolder(name: TextView, age: TextView, placeholder: ImageView)

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {

    // Reuse views
    val rowView =
      if (convertView == null) {
        val layoutInflater = LayoutInflater.from(context)
        val newRowView = layoutInflater.inflate(R.layout.item_author, null)

        // Configure view holder
        val viewHolder = new ViewHolder(
          newRowView.findViewById(R.id.item_author_name).asInstanceOf[TextView],
          newRowView.findViewById(R.id.item_author_age).asInstanceOf[TextView],

          newRowView.findViewById(R.id.item_author_placeholder).asInstanceOf[ImageView]
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
      viewHolder.name.setText(items(position).name)
      viewHolder.age.setText("" + items(position).age)

    }

    return rowView
  }

}
