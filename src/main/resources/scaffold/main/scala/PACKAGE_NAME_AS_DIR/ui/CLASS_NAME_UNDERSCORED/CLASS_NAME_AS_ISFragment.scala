package PACKAGE_UI.CLASS_NAME_UNDERSCORED

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import android.widget.FrameLayout
import android.widget.LinearLayout

import org.scaloid.common._

import com.google.gson.Gson

IMPORT_MODEL_FIELDS_DEPENDENCIES
import PACKAGE_TYPEDRESOURCE_IMPLICITS
import PACKAGE_TR
import PACKAGE_R
import PACKAGE_MODELS.CLASS_NAME_AS_IS
IMPORT_OTHER_MODELS
IMPORT_OTHER_MODELS_UI

object CLASS_NAME_AS_ISFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: CLASS_NAME_AS_IS): CLASS_NAME_AS_ISFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new CLASS_NAME_AS_ISFragment()
    fragment.setArguments(arguments)
    fragment
  }

  trait CLASS_NAME_AS_ISDeleteHandler {
    def onCLASS_NAME_AS_ISDeleteHandler()
  }
}

class CLASS_NAME_AS_ISFragment extends Fragment {

  var mModel: CLASS_NAME_AS_IS = _
FRAGMENT_VIEW_FIELDS
FRAGMENT_VIEW_OTHER_MODELS_FIELDS
  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    if (getArguments() != null) {
        val json = getArguments().getString(CLASS_NAME_AS_ISFragment.BUNDLE_MODEL_JSON)

        mModel = new Gson().fromJson(json, classOf[CLASS_NAME_AS_IS])
    }
    else {
      throw new RuntimeException("Arguments bundle not were not included in the fragment!")
      
      // If you want, you can implement a default view.
      //mModel = new CLASS_NAME_AS_IS(/* use model constructor here */)
    }

    setHasOptionsMenu(true)
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putString(CLASS_NAME_AS_ISFragment.BUNDLE_MODEL_JSON, new Gson().toJson(mModel))
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(TR.layout.fragment_CLASS_NAME_UNDERSCORED, container, false)
    
    val CLASS_NAME_UNCAPITALIZEDView = inflater.inflate(TR.layout.fragment_view_CLASS_NAME_UNDERSCORED, container, false)
    
    val CLASS_NAME_UNCAPITALIZEDFrameLayout = view.findView(TR.CLASS_NAME_UNDERSCORED_container)
    CLASS_NAME_UNCAPITALIZEDFrameLayout.addView(CLASS_NAME_UNCAPITALIZEDView)

FRAGMENT_VIEW_ASSIGN_FIELDS
FRAGMENT_VIEW_ASSIGN_FIELDS_OTHER_MODELS
    display()
    return view
  }

  private def display(): Unit = {
FRAGMENT_VIEW_DISPLAY_FIELDS
FRAGMENT_VIEW_DISPLAY_OTHER_MODELS_FIELDS
  }

  override def onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater): Unit = {
    val editMenu = menu.add(Menu.NONE, CLASS_NAME_AS_ISFragment.MENU_ITEM_EDIT, Menu.NONE, "Edit")
    editMenu.setIcon(android.R.drawable.ic_menu_edit)
    editMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

    val deleteMenu = menu.add(Menu.NONE, CLASS_NAME_AS_ISFragment.MENU_ITEM_DELETE, Menu.NONE, "Delete")
    deleteMenu.setIcon(android.R.drawable.ic_menu_delete)
    deleteMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {

    item.getItemId() match {
      case CLASS_NAME_AS_ISFragment.MENU_ITEM_EDIT => {

        val intent = new Intent(getActivity(), classOf[EditCLASS_NAME_AS_ISActivity])

        val json = new Gson().toJson(mModel)
        intent.putExtra(EditCLASS_NAME_AS_ISFragment.BUNDLE_MODEL_JSON, json)
        startActivityForResult(intent, CLASS_NAME_AS_ISFragment.REQUEST_EDIT)

        true
      }
      case CLASS_NAME_AS_ISFragment.MENU_ITEM_DELETE => {

        new AlertDialogBuilder("Delete MODEL_NAME_PRETTY", "Do you really want to delete this MODEL_NAME_PRETTY?")(getActivity()) {
          positiveButton(android.R.string.yes, (_, _) => {

            // TODO: Actually remove the object from database
            toast("The MODEL_NAME_PRETTY was deleted.")

            getActivity() match {
              case deleteHandler: CLASS_NAME_AS_ISFragment.CLASS_NAME_AS_ISDeleteHandler => deleteHandler.onCLASS_NAME_AS_ISDeleteHandler
              case activity => activity.getFragmentManager().popBackStack()
            }
          })
          negativeButton(android.R.string.cancel)
        }.show()
        true
      }
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {

    requestCode match {

      case CLASS_NAME_AS_ISFragment.REQUEST_EDIT => {
        if (resultCode == EditCLASS_NAME_AS_ISActivity.RESULT_EDIT_OCCURRED)
        {
            val json = data.getExtras().getString(EditCLASS_NAME_AS_ISFragment.BUNDLE_MODEL_JSON)
            mModel = new Gson().fromJson(json, classOf[CLASS_NAME_AS_IS])

            // TODO: Save the edited object to the database
            display()
        }
        else
        {
            Toast.makeText(getActivity(), "Edit was canceled.", Toast.LENGTH_LONG).show()
        }
       }
    }
  }

}
