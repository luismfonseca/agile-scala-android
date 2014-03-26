package pt.teste.ok.ui

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

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.text.format.DateFormat
import java.util.Date
import java.util.Calendar

import pt.teste.ok.models.Post
import pt.teste.ok.R

object EditPostFragment {
  val BUNDLE_MODEL_JSON: String = "model_json"
  val BUNDLE_CREATE_NEW: String = "create_new"

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
  var mTitle: TextView = _
  var mNumberOfLikes: TextView = _
  var mDateButton: Button = _

  private val mActionBarListener = (view: View) => {
    view.getId() match {
      case R.id.action_cancel => {
        getActivity().setResult(EditPostActivity.RESULT_NOTHING_CHANGED)
        getActivity().finish()
      }
      case R.id.action_done => {
        val finalPost = new Post(
          mTitle.getText().toString(),
          if (mNumberOfLikes.getText().toString().isEmpty) 0 else Integer.parseInt(mNumberOfLikes.getText().toString()),
          mModel.date
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
          "Lorem ipsum dolor sit amet.",
          5,
          new Date()
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
    val view = inflater.inflate(R.layout.fragment_edit_post, container, false)

    val actionBarButtons = inflater.inflate(R.layout.actionbar_edit_cancel_done, new LinearLayout(getActivity()), false)


    val cancelActionView = actionBarButtons.findViewById(R.id.action_cancel)
    cancelActionView.setOnClickListener(mActionBarListener)

    val doneActionView = actionBarButtons.findViewById(R.id.action_done)
    doneActionView.setOnClickListener(mActionBarListener)

    getActivity().getActionBar().setCustomView(actionBarButtons)
    getActivity().getActionBar().setDisplayOptions(
        ActionBar.DISPLAY_SHOW_CUSTOM,
        ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM)

    mTitle = view.findViewById(R.id.create_post_title).asInstanceOf[TextView]
    mNumberOfLikes = view.findViewById(R.id.create_post_number_of_likes).asInstanceOf[TextView]
    mDateButton = view.findViewById(R.id.create_post_date).asInstanceOf[Button]
    mDateButton.onClick({
      
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
            mDateButton.setText(DateFormat.format("dd-MM-yyyy", mModel.date))
          }
        }, year, month, day).show()

    })

    if (mModel != null)
    {
      mTitle.setText(mModel.title)
      mNumberOfLikes.setText("" + mModel.numberOfLikes)
      mDateButton.setText(DateFormat.format("dd-MM-yyyy", mModel.date))

    }

    return view
  }

  override def onDestroyView(): Unit = {
    super.onDestroyView()

    getActivity().getActionBar().setCustomView(null)
  }

}
