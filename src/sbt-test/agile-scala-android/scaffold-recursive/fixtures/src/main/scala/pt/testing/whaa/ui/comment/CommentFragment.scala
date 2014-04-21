package pt.testing.whaa.ui.comment

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

import pt.testing.whaa.R
import pt.testing.whaa.models.Comment
import pt.testing.whaa.models.Author


object CommentFragment {
  val BUNDLE_MODEL_JSON = "model_json"

  val MENU_ITEM_EDIT = 1
  val MENU_ITEM_DELETE = 2

  val REQUEST_EDIT = 1

  def newInstance(model: Comment): CommentFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new CommentFragment()
    fragment.setArguments(arguments)
    fragment
  }

  trait CommentDeleteHandler {
    def onCommentDeleteHandler()
  }
}

class CommentFragment extends Fragment {

  var mModel: Comment = _
  var mCommentDate: TextView = _
  var mCommentText: TextView = _

  var mAuthorName: TextView = _
  var mAuthorAge: TextView = _


  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    if (getArguments() != null) {
        val json = getArguments().getString(CommentFragment.BUNDLE_MODEL_JSON)

        mModel = new Gson().fromJson(json, classOf[Comment])
    }
    else {
      throw new RuntimeException("Arguments bundle not were not included in the fragment!")
      
      // If you want, you can implement a default view.
      //mModel = new Comment(/* use model constructor here */)
    }

    setHasOptionsMenu(true)
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putString(CommentFragment.BUNDLE_MODEL_JSON, new Gson().toJson(mModel))
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.fragment_comment, container, false)
    
    val commentView = inflater.inflate(R.layout.fragment_view_comment, container, false)
    
    val commentFrameLayout = view.findViewById(R.id.comment_container).asInstanceOf[FrameLayout]
    commentFrameLayout.addView(commentView)

    mCommentDate = commentFrameLayout.findViewById(R.id.comment_date).asInstanceOf[TextView]
    mCommentText = commentFrameLayout.findViewById(R.id.comment_text).asInstanceOf[TextView]

    val commentAuthorView = inflater.inflate(R.layout.fragment_view_author, container, false)

    val authorFrameLayout = commentFrameLayout.findViewById(R.id.comment_author_container).asInstanceOf[FrameLayout]
    authorFrameLayout.addView(commentAuthorView)

    mAuthorName = authorFrameLayout.findViewById(R.id.author_name).asInstanceOf[TextView]
    mAuthorAge = authorFrameLayout.findViewById(R.id.author_age).asInstanceOf[TextView]



    display()
    return view
  }

  private def display(): Unit = {
    mCommentDate.setText(DateFormat.format("dd-MM-yyyy", mModel.date))
    mCommentText.setText(mModel.text)

    if (mModel.author != null) {
    mAuthorName.setText(mModel.author.name)
    mAuthorAge.setText("" + mModel.author.age)

    }

  }

  override def onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater): Unit = {
    val editMenu = menu.add(Menu.NONE, CommentFragment.MENU_ITEM_EDIT, Menu.NONE, "Edit")
    editMenu.setIcon(android.R.drawable.ic_menu_edit)
    editMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

    val deleteMenu = menu.add(Menu.NONE, CommentFragment.MENU_ITEM_DELETE, Menu.NONE, "Delete")
    deleteMenu.setIcon(android.R.drawable.ic_menu_delete)
    deleteMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {

    item.getItemId() match {
      case CommentFragment.MENU_ITEM_EDIT => {

        val intent = new Intent(getActivity(), classOf[EditCommentActivity])

        val json = new Gson().toJson(mModel)
        intent.putExtra(EditCommentFragment.BUNDLE_MODEL_JSON, json)
        startActivityForResult(intent, CommentFragment.REQUEST_EDIT)

        true
      }
      case CommentFragment.MENU_ITEM_DELETE => {

        new AlertDialogBuilder("Delete Comment", "Do you really want to delete this Comment?")(getActivity()) {
          positiveButton(android.R.string.yes, (_, _) => {

            // TODO: Actually remove the object from database
            toast("The Comment was deleted.")

            getActivity() match {
              case deleteHandler: CommentFragment.CommentDeleteHandler => deleteHandler.onCommentDeleteHandler
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

      case CommentFragment.REQUEST_EDIT => {
        if (resultCode == EditCommentActivity.RESULT_EDIT_OCCURRED)
        {
            val json = data.getExtras().getString(EditCommentFragment.BUNDLE_MODEL_JSON)
            mModel = new Gson().fromJson(json, classOf[Comment])

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
