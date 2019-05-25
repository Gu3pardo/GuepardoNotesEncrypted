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
internal class NoteListAdapter(private val context: Context, private val list: Array<Note>) : BaseAdapter() {

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

    override fun getItemId(position: Int): Long = list[position].id

    override fun getCount(): Int = list.size

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(index: Int, convertView: View?, parentView: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.list_item, null)

        val note = list[index]

        val holder = Holder()
        holder.title = rowView.findViewById(R.id.itemTitle)
        holder.content = rowView.findViewById(R.id.itemContent)
        holder.dateTime = rowView.findViewById(R.id.itemDateTime)

        holder.edit = rowView.findViewById(R.id.btnEdit)
        holder.delete = rowView.findViewById(R.id.btnDelete)

        holder.title.text = note.title
        holder.content.text = note.content
        holder.dateTime.text = "${note.dateString} / ${note.timeString}"

        holder.edit.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(context.getString(R.string.bundleDataId), note.id)
            navigationController.navigateWithData(ActivityEdit::class.java, bundle, false)
        }

        holder.delete.setOnClickListener {
            MaterialDialog(context).show {
                title(text = context.getString(R.string.delete))
                message(text = String.format(context.getString(R.string.deleteRequest), note.title))
                positiveButton(text = context.getString(R.string.yes)) { DatabaseController.instance.delete(note.id.toInt()) }
                negativeButton(text = context.getString(R.string.no))
            }
        }

        return rowView
    }
}