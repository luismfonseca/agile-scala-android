package pt.teste.ok.ui.post

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

import android.text.format.DateFormat
import java.util.Date

import pt.teste.ok.TypedResource._
import pt.teste.ok.TR
import pt.teste.ok.R
import pt.teste.ok.models.Post



object PostFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: Post): PostFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new PostFragment()
    fragment.setArguments(arguments)
    fragment
  }

  trait PostDeleteHandler {
    def onPostDeleteHandler()
  }
}

class PostFragment extends Fragment {

  var mModel: Post = _
  var mPostTitle: TextView = _
  var mPostNumberOfLikes: TextView = _
  var mPostDate: TextView = _


  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    if (getArguments() != null) {
        val json = getArguments().getString(PostFragment.BUNDLE_MODEL_JSON)

        mModel = new Gson().fromJson(json, classOf[Post])
    }
    else {
      throw new RuntimeException("Arguments bundle not were not included in the fragment!")
      
      // If you want, you can implement a default view.
      //mModel = new Post(/* use model constructor here */)
    }

    setHasOptionsMenu(true)
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putString(PostFragment.BUNDLE_MODEL_JSON, new Gson().toJson(mModel))
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(TR.layout.fragment_post, container, false)
    
    val postView = inflater.inflate(TR.layout.fragment_view_post, container, false)
    
    val postFrameLayout = view.findView(TR.post_container)
    postFrameLayout.addView(postView)

    mPostTitle = postFrameLayout.findView(TR.post_title)
    mPostNumberOfLikes = postFrameLayout.findView(TR.post_number_of_likes)
    mPostDate = postFrameLayout.findView(TR.post_date)


    display()
    return view
  }

  private def display(): Unit = {
    mPostTitle.setText(mModel.title)
    mPostNumberOfLikes.setText("" + mModel.numberOfLikes)
    mPostDate.setText(DateFormat.format("dd-MM-yyyy", mModel.date))


  }

  override def onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater): Unit = {
    val editMenu = menu.add(Menu.NONE, PostFragment.MENU_ITEM_EDIT, Menu.NONE, "Edit")
    editMenu.setIcon(android.R.drawable.ic_menu_edit)
    editMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

    val deleteMenu = menu.add(Menu.NONE, PostFragment.MENU_ITEM_DELETE, Menu.NONE, "Delete")
    deleteMenu.setIcon(android.R.drawable.ic_menu_delete)
    deleteMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {

    item.getItemId() match {
      case PostFragment.MENU_ITEM_EDIT => {

        val intent = new Intent(getActivity(), classOf[EditPostActivity])

        val json = new Gson().toJson(mModel)
        intent.putExtra(EditPostFragment.BUNDLE_MODEL_JSON, json)
        startActivityForResult(intent, PostFragment.REQUEST_EDIT)

        true
      }
      case PostFragment.MENU_ITEM_DELETE => {

        new AlertDialogBuilder("Delete Post", "Do you really want to delete this Post?")(getActivity()) {
          positiveButton(android.R.string.yes, (_, _) => {

            // TODO: Actually remove the object from database
            toast("The Post was deleted.")

            getActivity() match {
              case deleteHandler: PostFragment.PostDeleteHandler => deleteHandler.onPostDeleteHandler
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

      case PostFragment.REQUEST_EDIT => {
        if (resultCode == EditPostActivity.RESULT_EDIT_OCCURRED)
        {
            val json = data.getExtras().getString(EditPostFragment.BUNDLE_MODEL_JSON)
            mModel = new Gson().fromJson(json, classOf[Post])

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
