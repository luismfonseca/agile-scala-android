package pt.testing.whaa.ui.author

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


import pt.testing.whaa.R
import pt.testing.whaa.models.Author



object AuthorFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: Author): AuthorFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new AuthorFragment()
    fragment.setArguments(arguments)
    fragment
  }

  trait AuthorDeleteHandler {
    def onAuthorDeleteHandler()
  }
}

class AuthorFragment extends Fragment {

  var mModel: Author = _
  var mAuthorName: TextView = _
  var mAuthorAge: TextView = _


  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    if (getArguments() != null) {
        val json = getArguments().getString(AuthorFragment.BUNDLE_MODEL_JSON)

        mModel = new Gson().fromJson(json, classOf[Author])
    }
    else {
      throw new RuntimeException("Arguments bundle not were not included in the fragment!")
      
      // If you want, you can implement a default view.
      //mModel = new Author(/* use model constructor here */)
    }

    setHasOptionsMenu(true)
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putString(AuthorFragment.BUNDLE_MODEL_JSON, new Gson().toJson(mModel))
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.fragment_author, container, false)
    
    val authorView = inflater.inflate(R.layout.fragment_view_author, container, false)
    
    val authorFrameLayout = view.findViewById(R.id.author_container).asInstanceOf[FrameLayout]
    authorFrameLayout.addView(authorView)

    mAuthorName = authorFrameLayout.findViewById(R.id.author_name).asInstanceOf[TextView]
    mAuthorAge = authorFrameLayout.findViewById(R.id.author_age).asInstanceOf[TextView]



    display()
    return view
  }

  private def display(): Unit = {
    mAuthorName.setText(mModel.name)
    mAuthorAge.setText("" + mModel.age)


  }

  override def onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater): Unit = {
    val editMenu = menu.add(Menu.NONE, AuthorFragment.MENU_ITEM_EDIT, Menu.NONE, "Edit")
    editMenu.setIcon(android.R.drawable.ic_menu_edit)
    editMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

    val deleteMenu = menu.add(Menu.NONE, AuthorFragment.MENU_ITEM_DELETE, Menu.NONE, "Delete")
    deleteMenu.setIcon(android.R.drawable.ic_menu_delete)
    deleteMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {

    item.getItemId() match {
      case AuthorFragment.MENU_ITEM_EDIT => {

        val intent = new Intent(getActivity(), classOf[EditAuthorActivity])

        val json = new Gson().toJson(mModel)
        intent.putExtra(EditAuthorFragment.BUNDLE_MODEL_JSON, json)
        startActivityForResult(intent, AuthorFragment.REQUEST_EDIT)

        true
      }
      case AuthorFragment.MENU_ITEM_DELETE => {

        new AlertDialogBuilder("Delete Author", "Do you really want to delete this Author?")(getActivity()) {
          positiveButton(android.R.string.yes, (_, _) => {

            // TODO: Actually remove the object from database
            toast("The Author was deleted.")

            getActivity() match {
              case deleteHandler: AuthorFragment.AuthorDeleteHandler => deleteHandler.onAuthorDeleteHandler
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

      case AuthorFragment.REQUEST_EDIT => {
        if (resultCode == EditAuthorActivity.RESULT_EDIT_OCCURRED)
        {
            val json = data.getExtras().getString(EditAuthorFragment.BUNDLE_MODEL_JSON)
            mModel = new Gson().fromJson(json, classOf[Author])

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
