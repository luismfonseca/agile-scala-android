package pt.testing.whaa.ui.post

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
import pt.testing.whaa.models.Post
import pt.testing.whaa.models.Author
import pt.testing.whaa.models.Comment
import pt.testing.whaa.ui.ChangeToFragmentHandler
import pt.testing.whaa.ui.comment._

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
  var mPostDate: TextView = _
  var mPostTitle: TextView = _
  var mPostContent: TextView = _

  var mAuthorName: TextView = _
  var mAuthorAge: TextView = _

  var mComentsListAdapter: CommentListAdapter = _
  var mComentsView: LinearLayout = _
  var mComentsAddButton: Button = _

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
    val view = inflater.inflate(R.layout.fragment_post, container, false)
    
    val postView = inflater.inflate(R.layout.fragment_view_post, container, false)
    
    val postFrameLayout = view.findViewById(R.id.post_container).asInstanceOf[FrameLayout]
    postFrameLayout.addView(postView)

    mPostDate = postFrameLayout.findViewById(R.id.post_date).asInstanceOf[TextView]
    mPostTitle = postFrameLayout.findViewById(R.id.post_title).asInstanceOf[TextView]
    mPostContent = postFrameLayout.findViewById(R.id.post_content).asInstanceOf[TextView]

    val postAuthorView = inflater.inflate(R.layout.fragment_view_author, container, false)

    val authorFrameLayout = postFrameLayout.findViewById(R.id.post_author_container).asInstanceOf[FrameLayout]
    authorFrameLayout.addView(postAuthorView)

    mAuthorName = authorFrameLayout.findViewById(R.id.author_name).asInstanceOf[TextView]
    mAuthorAge = authorFrameLayout.findViewById(R.id.author_age).asInstanceOf[TextView]

    mComentsView = new LinearLayout(getActivity())
    mComentsView.setOrientation(LinearLayout.VERTICAL)
	
	// TODO: Load actual values from database
	mComentsListAdapter = new CommentListAdapter(getActivity(), Array[Comment]())

    val comentsFrameLayout = postFrameLayout.findViewById(R.id.post_coments_container).asInstanceOf[FrameLayout]
    comentsFrameLayout.addView(mComentsView)

    mComentsAddButton = postFrameLayout.findViewById(R.id.post_add_to_coments).asInstanceOf[Button]
	mComentsAddButton.setOnClickListener(new View.OnClickListener() {
    
	  override def onClick(view: View): Unit = {
        val intent = new Intent(getActivity(), classOf[EditCommentActivity])
        intent.putExtra(EditCommentFragment.BUNDLE_CREATE_NEW, true)

        startActivity(intent)
	  }
	})


    display()
    return view
  }

  private def display(): Unit = {
    mPostDate.setText(DateFormat.format("dd-MM-yyyy", mModel.date))
    mPostTitle.setText(mModel.title)
    mPostContent.setText(mModel.content)

    if (mModel.author != null) {
    mAuthorName.setText(mModel.author.name)
    mAuthorAge.setText("" + mModel.author.age)

    }
    mComentsView.removeAllViews
    for (i <- 0 until mComentsListAdapter.getCount) {
      val view = mComentsListAdapter.getView(i, null, null)
      
      view.setOnClickListener(new View.OnClickListener() {
        override def onClick(view: View): Unit = {
          
          val fragment = CommentFragment.newInstance(mComentsListAdapter.getItem(i))
          
          (getActivity().asInstanceOf[ChangeToFragmentHandler]).onChangeToFragment(fragment)
        }
      })
      
      mComentsView.addView(view)
    }

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
