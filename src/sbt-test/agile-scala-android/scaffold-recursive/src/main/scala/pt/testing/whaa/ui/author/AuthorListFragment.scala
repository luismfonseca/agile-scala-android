package pt.testing.whaa.ui.author

import android.app.ListFragment
import android.os.Bundle
import android.view.View
import android.widget.ListView

import org.scaloid.common._

import com.google.gson.Gson


import pt.testing.whaa.ui.ChangeToFragmentHandler
import pt.testing.whaa.models.Author

object AuthorListFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: Author): AuthorListFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new AuthorListFragment()
    fragment.setArguments(arguments)
    fragment
  }
}

class AuthorListFragment extends ListFragment {

  var mListAdapter: AuthorListAdapter = _

  lazy val mItems: Array[Author] = {
  
    // TODO: Load real object from database
    (1 to 4).foldLeft(Array[Author]()) {
      (acc, index) => {
        acc :+ Author(
          "Lorem ipsum dolor sit amet.",
          5
        )
      }
    }
  }
  
  override def onActivityCreated(bundle: Bundle): Unit = {
    super.onActivityCreated(bundle)

    getListView().setDividerHeight(0)

    mListAdapter = new AuthorListAdapter(getActivity(), mItems)
    setListAdapter(mListAdapter)
  }

  override def onListItemClick(listView: ListView, view: View, position: Int, id: Long) {

    val authorFragment = AuthorFragment.newInstance(mItems(position))

    (getActivity().asInstanceOf[ChangeToFragmentHandler]).onChangeToFragment(authorFragment)
  }
}
