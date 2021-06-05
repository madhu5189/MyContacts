package madhuri.applications.mycontacts.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import madhuri.applications.mycontacts.R
import madhuri.applications.mycontacts.databinding.RecyclerContactItemBinding
import madhuri.applications.mycontacts.model.Contact

class ContactsAdapter(private var contactsList: MutableList<Contact>) :
    RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    // updating the recyclerview when there is a change in the list
    fun updateContacts(newDiaryList: List<Contact>) {
        contactsList.clear()
        contactsList.addAll(newDiaryList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContactsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_contact_item, parent, false)
        )

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(contactsList[position])
    }

    override fun getItemCount(): Int = contactsList.size

    class ContactsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val recyclerViewItemBinding = RecyclerContactItemBinding.bind(view)
        private val contactNameTextView = recyclerViewItemBinding.nameRecyclerItem
        private val contactNumberTextView = recyclerViewItemBinding.numberRecyclerItem

        fun bind(contact: Contact) {
            contactNameTextView.text = contact.name
            var contactNumbers = ""

            for (number in contact.contactNumber) {
                contactNumbers = if (contactNumbers == "") {
                    number
                } else "$contactNumbers\n $number"
            }
            contactNumberTextView.text = contactNumbers
        }
    }

}