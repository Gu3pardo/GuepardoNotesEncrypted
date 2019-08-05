package guepardoapps.mynoteencrypted.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.rey.material.widget.FloatingActionButton
import guepardoapps.mynoteencrypted.R
import guepardoapps.mynoteencrypted.activities.ActivityEdit
import guepardoapps.mynoteencrypted.controller.DatabaseController
import guepardoapps.mynoteencrypted.controller.NavigationController
import guepardoapps.mynoteencrypted.model.Note

@ExperimentalUnsignedTypes
internal class NoteListAdapter(private val context: Context, private val list: Array<Note>, private val reload: () -> Unit) : BaseAdapter() {

    private val navigationController: NavigationController = NavigationController(context)

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class Holder {
        lateinit var title: TextView
        lateinit var content: TextView
        lateinit var dateTime: TextView
        lateinit var edit: FloatingActionButton
        lateinit var delete: FloatingActionButton
    }

    override fun getItem(position: Int): Note = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(index: Int, convertView: View?, parentView: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.list_item, null)

        Holder().apply {
            val note = list[index]

            title = rowView.findViewById(R.id.itemTitle)
            content = rowView.findViewById(R.id.itemContent)
            dateTime = rowView.findViewById(R.id.itemDateTime)

            edit = rowView.findViewById(R.id.btnEdit)
            delete = rowView.findViewById(R.id.btnDelete)

            title.text = note.title
            content.text = note.content
            dateTime.text = "${note.dateString} / ${note.timeString}"

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(context.getString(R.string.bundleDataId), note.id)
                navigationController.navigateWithData(ActivityEdit::class.java, bundle, false)
            }

            delete.setOnClickListener {
                MaterialDialog(context).show {
                    title(text = context.getString(R.string.delete))
                    message(text = String.format(context.getString(R.string.deleteRequest), note.title))
                    positiveButton(text = context.getString(R.string.yes)) {
                        DatabaseController.instance.delete(note.id)
                        reload()
                    }
                    negativeButton(text = context.getString(R.string.no))
                }
            }
        }

        return rowView
    }
}