package PACKAGE_UI.CLASS_NAME_UNDERSCORED

import android.app.ListFragment
import android.os.Bundle
import android.view.View
import android.widget.ListView

import org.scaloid.common._

import com.google.gson.Gson

IMPORT_MODEL_FIELDS_DEPENDENCIES
import PACKAGE_UI.ChangeToFragmentHandler
import PACKAGE_MODELS.CLASS_NAME_AS_IS

object CLASS_NAME_AS_ISListFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: CLASS_NAME_AS_IS): CLASS_NAME_AS_ISListFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new CLASS_NAME_AS_ISListFragment()
    fragment.setArguments(arguments)
    fragment
  }
}

class CLASS_NAME_AS_ISListFragment extends ListFragment {

  var mListAdapter: CLASS_NAME_AS_ISListAdapter = _

  lazy val mItems: Array[CLASS_NAME_AS_IS] = {
  
    // TODO: Load real object from database
    (1 to 4).foldLeft(Array[CLASS_NAME_AS_IS]()) {
      (acc, index) => {
        acc :+ CLASS_NAME_AS_IS(
RANDOM_DATA_COMMA_SEPARATED
        )
      }
    }
  }
  
  override def onActivityCreated(bundle: Bundle): Unit = {
    super.onActivityCreated(bundle)

    getListView().setDividerHeight(0)

    mListAdapter = new CLASS_NAME_AS_ISListAdapter(getActivity(), mItems)
    setListAdapter(mListAdapter)
  }

  override def onListItemClick(listView: ListView, view: View, position: Int, id: Long) {

    val CLASS_NAME_UNCAPITALIZEDFragment = CLASS_NAME_AS_ISFragment.newInstance(mItems(position))

    (getActivity().asInstanceOf[ChangeToFragmentHandler]).onChangeToFragment(CLASS_NAME_UNCAPITALIZEDFragment)
  }
}
