package pt.testing.whaa.ui.author

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


import pt.testing.whaa.R
import pt.testing.whaa.models.Author



object EditAuthorFragment {
  val BUNDLE_MODEL_JSON = "model_json"
  val BUNDLE_CREATE_NEW = "create_new"

  def newInstance(model: Author): EditAuthorFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new EditAuthorFragment()
    fragment.setArguments(arguments)
    fragment
  }
}

class EditAuthorFragment extends Fragment {

  var mModel: Author = _
  var mAuthorName: TextView = _
  var mAuthorAge: TextView = _


  private val mActionBarListener = (view: View) => {
    view.getId() match {
      case R.id.action_cancel => {
        getActivity().setResult(EditAuthorActivity.RESULT_NOTHING_CHANGED)
        getActivity().finish()
      }
      case R.id.action_done => {
        val finalAuthor = new Author(
          mAuthorName.getText().toString(),
          if (mAuthorAge.getText().toString().isEmpty) 0 else Integer.parseInt(mAuthorAge.getText().toString())
        )

        val data = new Intent()
        data.putExtra(EditAuthorFragment.BUNDLE_MODEL_JSON, new Gson().toJson(finalAuthor))

        getActivity().setResult(EditAuthorActivity.RESULT_EDIT_OCCURRED, data)
        getActivity().finish()
      }
    }
  }

  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    if (getArguments() != null) {
      if (getArguments().getBoolean(EditAuthorFragment.BUNDLE_CREATE_NEW))
      {
        mModel = new Author(
          "Lorem ipsum dolor sit amet.",
          5
        )
      }
      else
      {
        val json = getArguments().getString(EditAuthorFragment.BUNDLE_MODEL_JSON)
        mModel = new Gson().fromJson(json, classOf[Author])
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

    val view = inflater.inflate(R.layout.fragment_author, container, false)

    val authorView = inflater.inflate(R.layout.fragment_edit_author, container, false)
    
    val authorFrameLayout = view.findViewById(R.id.author_container).asInstanceOf[FrameLayout]
    authorFrameLayout.addView(authorView)

    mAuthorName = authorFrameLayout.findViewById(R.id.create_author_name).asInstanceOf[TextView]
    mAuthorAge = authorFrameLayout.findViewById(R.id.create_author_age).asInstanceOf[TextView]


    if (mModel != null)
    {
      mAuthorName.setText(mModel.name)
      mAuthorAge.setText("" + mModel.age)
    }

    return view
  }
  


  override def onDestroyView(): Unit = {
    super.onDestroyView()

    getActivity().getActionBar().setCustomView(null)
  }

}
