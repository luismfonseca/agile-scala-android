package PACKAGE_UI.CLASS_NAME_UNDERSCORED

import android.app.Activity
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

IMPORT_MODEL_FIELDS_DEPENDENCIES

import PACKAGE_MODELS.CLASS_NAME_AS_IS
import PACKAGE_R

class CLASS_NAME_AS_ISListAdapter(val context: Activity, val items: Array[CLASS_NAME_AS_IS]) extends ArrayAdapter[CLASS_NAME_AS_IS](context, R.layout.item_CLASS_NAME_UNDERSCORED, items)  {

  case class ViewHolder(LIST_ADAPTER_VIEWHOLDER_ELEMENTS, placeholder: ImageView)

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {

    // Reuse views
    val rowView =
      if (convertView == null) {
        val layoutInflater = LayoutInflater.from(context)
        val newRowView = layoutInflater.inflate(R.layout.item_CLASS_NAME_UNDERSCORED, null)

        // Configure view holder
        val viewHolder = new ViewHolder(
LIST_ADAPTER_VIEWHOLDER_PARAMETERS
          newRowView.findViewById(R.id.item_CLASS_NAME_UNDERSCORED_placeholder).asInstanceOf[ImageView]
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
VIEWHOLDER_DISPLAY_FIELDS
    }

    return rowView
  }

}
