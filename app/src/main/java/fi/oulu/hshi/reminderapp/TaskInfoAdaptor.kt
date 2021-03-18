package fi.oulu.hshi.reminderapp


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import fi.oulu.hshi.reminderapp.databinding.MessageItemBinding
import fi.oulu.hshi.reminderapp.db.ReminderInfo
import fi.oulu.hshi.reminderapp.db.TaskInfo
import fi.oulu.hshi.reminderapp.entity.Reminder

class TaskInfoAdaptor (context: Context, private val list:MutableList<Reminder>): BaseAdapter(){
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        var rowBinding = MessageItemBinding.inflate(inflater, container, false)


        //val row = inflater.inflate(R.layout.payment_history_item, parent, false)

        //rowBinding.txtTaskName.text = list[position].message
        rowBinding.txtTaskDesc.text = list[position].message
        rowBinding.txtCreateTime.text = list[position].creation_time
        rowBinding.txtRemindDate.text = list[position].reminder_time

        return rowBinding.root
    }
    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}