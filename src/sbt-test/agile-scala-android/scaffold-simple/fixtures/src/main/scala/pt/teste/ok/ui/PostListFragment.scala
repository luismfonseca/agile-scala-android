package pt.teste.ok.ui

import android.app.ListFragment
import android.os.Bundle
import android.view.View
import android.widget.ListView

import org.scaloid.common._

import com.google.gson.Gson

import android.text.format.DateFormat
import java.util.Date

import pt.teste.ok.models.Post

object PostListFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: Post): PostListFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new PostListFragment()
    fragment.setArguments(arguments)
    fragment
  }
}

class PostListFragment extends ListFragment {

  var mListAdapter: PostListAdapter = _

  lazy val mItems: Array[Post] = {
  
    // TODO: Load real object from database
    (1 to 4).foldLeft(Array[Post]()) {
      (acc, index) => {
        acc :+ Post(
          "Lorem ipsum dolor sit amet.",
          5,
          new Date()
        )
      }
    }
  }
  
  override def onActivityCreated(bundle: Bundle): Unit = {
    super.onActivityCreated(bundle)

    getListView().setDividerHeight(0)

    mListAdapter = new PostListAdapter(getActivity(), mItems)
    setListAdapter(mListAdapter)
  }

  override def onListItemClick(listView: ListView, view: View, position: Int, id: Long) {

    val postFragment = PostFragment.newInstance(mItems(position))

    (getActivity().asInstanceOf[ChangeToFragmentHandler]).onChangeToFragment(postFragment)
  }
}
