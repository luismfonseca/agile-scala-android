package pt.testing.whaa.ui.comment

import android.app.ListFragment
import android.os.Bundle
import android.view.View
import android.widget.ListView

import org.scaloid.common._

import com.google.gson.Gson

import android.text.format.DateFormat
import java.util.Date

import pt.testing.whaa.ui.ChangeToFragmentHandler
import pt.testing.whaa.models.Comment

object CommentListFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: Comment): CommentListFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new CommentListFragment()
    fragment.setArguments(arguments)
    fragment
  }
}

class CommentListFragment extends ListFragment {

  var mListAdapter: CommentListAdapter = _

  lazy val mItems: Array[Comment] = {
  
    // TODO: Load real object from database
    (1 to 4).foldLeft(Array[Comment]()) {
      (acc, index) => {
        acc :+ Comment(
null,
          new Date(),
          "Lorem ipsum dolor sit amet."
        )
      }
    }
  }
  
  override def onActivityCreated(bundle: Bundle): Unit = {
    super.onActivityCreated(bundle)

    getListView().setDividerHeight(0)

    mListAdapter = new CommentListAdapter(getActivity(), mItems)
    setListAdapter(mListAdapter)
  }

  override def onListItemClick(listView: ListView, view: View, position: Int, id: Long) {

    val commentFragment = CommentFragment.newInstance(mItems(position))

    (getActivity().asInstanceOf[ChangeToFragmentHandler]).onChangeToFragment(commentFragment)
  }
}
