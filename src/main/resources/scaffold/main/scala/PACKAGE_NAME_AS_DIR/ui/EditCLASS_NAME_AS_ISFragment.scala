package PACKAGE_UI

import android.app.ActionBar
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import org.scaloid.common._

import com.google.gson.Gson

IMPORT_EDIT_FRAGMENT_FIELDS_DEPENDENCIES
import PACKAGE_MODELS.CLASS_NAME_AS_IS
import PACKAGE_R

object EditCLASS_NAME_AS_ISFragment {
  val BUNDLE_MODEL_JSON: String = "model_json"
  val BUNDLE_CREATE_NEW: String = "create_new"

  def newInstance(model: Post): EditCLASS_NAME_AS_ISFragment = {
    val arguments = new Bundle()
    arguments.putString(BUNDLE_MODEL_JSON, new Gson().toJson(model))

    val fragment = new EditCLASS_NAME_AS_ISFragment()
    fragment.setArguments(arguments)
    fragment
  }
}

class EditCLASS_NAME_AS_ISFragment extends Fragment {

  var mModel: CLASS_NAME_AS_IS = _
FRAGMENT_EDIT_FIELDS
  private val mActionBarListener = (view: View) => {
    view.getId() match {
      case R.id.action_cancel => {
        getActivity().setResult(EditPostActivity.RESULT_NOTHING_CHANGED)
        getActivity().finish()
      }
      case R.id.action_done => {
        val finalCLASS_NAME_AS_IS = new CLASS_NAME_AS_IS(
FRAGMENT_EDIT_VIEW_GET_FIELDS
		)

        val data = new Intent()
        data.putExtra(EditCLASS_NAME_AS_ISFragment.BUNDLE_MODEL_JSON, new Gson().toJson(finalCLASS_NAME_AS_IS))

        getActivity().setResult(EditCLASS_NAME_AS_ISActivity.RESULT_EDIT_OCCURRED, data)
        getActivity().finish()
      }
    }
  }

  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    if (getArguments() != null) {
      if (getArguments().getBoolean(EditCLASS_NAME_AS_ISFragment.BUNDLE_CREATE_NEW))
      {
        mModel = new CLASS_NAME_AS_IS("dummynew", 0, new Date())
      }
      else
      {
        val json = getArguments().getString(EditCLASS_NAME_AS_ISFragment.BUNDLE_MODEL_JSON)
        mModel = new Gson().fromJson(json, classOf[CLASS_NAME_AS_IS])
      }
    }
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.fragment_edit_CLASS_NAME_UNDERSCORED, container, false)

    val actionBarButtons = inflater.inflate(R.layout.actionbar_edit_cancel_done, new LinearLayout(getActivity()), false)


    val cancelActionView = actionBarButtons.findViewById(R.id.action_cancel)
    cancelActionView.setOnClickListener(mActionBarListener)

    val doneActionView = actionBarButtons.findViewById(R.id.action_done)
    doneActionView.setOnClickListener(mActionBarListener)

    getActivity().getActionBar().setCustomView(actionBarButtons)
    getActivity().getActionBar().setDisplayOptions(
        ActionBar.DISPLAY_SHOW_CUSTOM,
        ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM)

FRAGMENT_EDIT_VIEW_ASSIGN_FIELDS
    if (mModel != null)
    {
FRAGMENT_EDIT_VIEW_SET_FIELDS
    }

    return view
  }

  override def onDestroyView(): Unit = {
    super.onDestroyView()

    getActivity().getActionBar().setCustomView(null)
  }

}
