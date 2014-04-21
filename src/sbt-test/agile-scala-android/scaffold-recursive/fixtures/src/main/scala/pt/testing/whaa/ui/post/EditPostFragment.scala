package pt.testing.whaa.ui.post

import android.app.ActionBar
import android.app.Fragment
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.FrameLayout

import org.scaloid.common._

import com.google.gson.Gson

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.text.format.DateFormat
import java.util.Date
import java.util.Calendar

import pt.testing.whaa.R
import pt.testing.whaa.models.Post
import pt.testing.whaa.models.Author
import pt.testing.whaa.models.Comment
import pt.testing.whaa.ui.author._

object EditPostFragment {
  val BUNDLE_MODEL_JSON = "model_json"
  val BUNDLE_CREATE_NEW = "create_new"
  val REQUEST_NEW_AUTHOR = 3

  def newInstance(model: Post): EditPostFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new EditPostFragment()
    fragment.setArguments(arguments)
    fragment
  }
}

class EditPostFragment extends Fragment {

  var mModel: Post = _
  var mPostDateButton: Button = _
  var mPostTitle: TextView = _
  var mPostContent: TextView = _

  var mAuthorName: TextView = _
  var mAuthorAge: TextView = _
  var mChangeAuthorButton: Button = _

  private val mActionBarListener = (view: View) => {
    view.getId() match {
      case R.id.action_cancel => {
        getActivity().setResult(EditPostActivity.RESULT_NOTHING_CHANGED)
        getActivity().finish()
      }
      case R.id.action_done => {
        val finalPost = new Post(
          mModel.author,
          mModel.date,
          mPostTitle.getText().toString(),
          mPostContent.getText().toString(),
          mModel.coments
        )

        val data = new Intent()
        data.putExtra(EditPostFragment.BUNDLE_MODEL_JSON, new Gson().toJson(finalPost))

        getActivity().setResult(EditPostActivity.RESULT_EDIT_OCCURRED, data)
        getActivity().finish()
      }
    }
  }

  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    if (getArguments() != null) {
      if (getArguments().getBoolean(EditPostFragment.BUNDLE_CREATE_NEW))
      {
        mModel = new Post(
null,
          new Date(),
          "Lorem ipsum dolor sit amet.",
          "Lorem ipsum dolor sit amet.", null
        )
      }
      else
      {
        val json = getArguments().getString(EditPostFragment.BUNDLE_MODEL_JSON)
        mModel = new Gson().fromJson(json, classOf[Post])
      }
    }
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {

    val actionBarButtons = inflater.inflate(R.layout.actionbar_edit_cancel_done, new LinearLayout(getActivity()), false)

    val cancelActionView = actionBarButtons.findViewById(R.id.action_cancel)
    cancelActionView.setOnClickListener(mActionBarListener)

    val doneActionView = actionBarButtons.findViewById(R.id.action_done)
    doneActionView.setOnClickListener(mActionBarListener)

    getActivity().getActionBar().setCustomView(actionBarButtons)
    getActivity().getActionBar().setDisplayOptions(
        ActionBar.DISPLAY_SHOW_CUSTOM,
        ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM)

    val view = inflater.inflate(R.layout.fragment_post, container, false)

    val postView = inflater.inflate(R.layout.fragment_edit_post, container, false)
    
    val postFrameLayout = view.findViewById(R.id.post_container).asInstanceOf[FrameLayout]
    postFrameLayout.addView(postView)

    mPostDateButton = postFrameLayout.findViewById(R.id.create_post_date).asInstanceOf[Button]
    mPostDateButton.onClick({
      
        val calendar = Calendar.getInstance()
		if (mModel.date != null) {
          calendar.setTime(mModel.date)
		}
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

          override def onDateSet(datePickerView: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int): Unit = {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            mModel = mModel.copy(date = calendar.getTime())
            mPostDateButton.setText(DateFormat.format("dd-MM-yyyy", mModel.date))
          }
        }, year, month, day).show()

    })
    mPostTitle = postFrameLayout.findViewById(R.id.create_post_title).asInstanceOf[TextView]
    mPostContent = postFrameLayout.findViewById(R.id.create_post_content).asInstanceOf[TextView]

    val postAuthorView = inflater.inflate(R.layout.fragment_view_author, container, false)

    val authorFrameLayout = postFrameLayout.findViewById(R.id.post_author_container).asInstanceOf[FrameLayout]
    authorFrameLayout.addView(postAuthorView)

    mAuthorName = authorFrameLayout.findViewById(R.id.author_name).asInstanceOf[TextView]
    mAuthorAge = authorFrameLayout.findViewById(R.id.author_age).asInstanceOf[TextView]

    mChangeAuthorButton = postFrameLayout.findViewById(R.id.post_change_author).asInstanceOf[Button]
    mChangeAuthorButton.onClick({

    val dialog = new AlertDialogBuilder("Choose the Author...")(getActivity()) {
      positiveButton("New", {
      val intent = new Intent(getActivity(), classOf[EditAuthorActivity])
      intent.putExtra(EditAuthorFragment.BUNDLE_CREATE_NEW, true)

      startActivityForResult(intent, EditPostFragment.REQUEST_NEW_AUTHOR);
    })
    negativeButton(android.R.string.cancel)
    }
    
    // TODO: Load real objects from a database
    val authors = Array[Author]()
    dialog.setAdapter(new AuthorListAdapter(getActivity(), authors), new DialogInterface.OnClickListener() {
      override def onClick(dialog: DialogInterface, which: Int): Unit = {
        mModel = mModel.copy(author = authors(which))
        displayAuthor()
      }
    })
    
    dialog.show()
    ()
  })

    if (mModel != null)
    {
      mPostDateButton.setText(DateFormat.format("dd-MM-yyyy", mModel.date))
      mPostTitle.setText(mModel.title)
      mPostContent.setText(mModel.content)
    }

    return view
  }
  
  def displayAuthor(): Unit = {
    mAuthorName.setText(mModel.author.name)
    mAuthorAge.setText("" + mModel.author.age)

  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    
	requestCode match {
	  case EditPostFragment.REQUEST_NEW_AUTHOR => {
	    resultCode match {
		  case EditAuthorActivity.RESULT_EDIT_OCCURRED => {
		    val authorJson = data.getStringExtra(EditPostFragment.BUNDLE_MODEL_JSON)	
			val newAuthor = new Gson().fromJson(authorJson, classOf[Author])
			
			mModel = mModel.copy(author = newAuthor)
			displayAuthor()
		  }
		}
	  }
	  case _ => super.onActivityResult(requestCode, resultCode, data)
	}
  }

  override def onDestroyView(): Unit = {
    super.onDestroyView()

    getActivity().getActionBar().setCustomView(null)
  }

}
