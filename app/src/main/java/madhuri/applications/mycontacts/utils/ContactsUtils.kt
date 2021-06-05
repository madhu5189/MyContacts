package madhuri.applications.mycontacts.utils

import android.Manifest
import android.content.Context
import android.provider.ContactsContract
import androidx.annotation.RequiresPermission
import madhuri.applications.mycontacts.model.Contact

@RequiresPermission(Manifest.permission.READ_CONTACTS)
@JvmOverloads
fun Context.retrieveAllContacts(
    searchPattern: String = "",
    limit: Int = -1,
    offset: Int = -1
): List<Contact> {
    val result: MutableList<Contact> = mutableListOf()
    contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        CONTACT_PROJECTION,
        if (searchPattern.isNotBlank()) "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE '%?%'" else null,
        if (searchPattern.isNotBlank()) arrayOf(searchPattern) else null,
        if (limit > 0 && offset > -1) "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC LIMIT $limit OFFSET $offset"
        else ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
    )?.use {
        if (it.moveToFirst()) {
            do {
                val contactId = it.getLong(it.getColumnIndex(CONTACT_PROJECTION[0]))
                val name = it.getString(it.getColumnIndex(CONTACT_PROJECTION[1])) ?: ""
                val hasPhoneNumber =
                    it.getString(it.getColumnIndex(CONTACT_PROJECTION[2])).toInt()
                val phoneNumber: List<String> = if (hasPhoneNumber > 0) {
                    retrievePhoneNumber(contactId)
                } else mutableListOf()

                result.add(Contact(name, phoneNumber))
            } while (it.moveToNext())
        }
    }
    return result
}

private fun Context.retrievePhoneNumber(contactId: Long): List<String> {
    val result: MutableList<String> = mutableListOf()
    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} =?",
        arrayOf(contactId.toString()),
        null
    )?.use {
        if (it.moveToFirst()) {
            do {
                result.add(it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
            } while (it.moveToNext())
        }
    }
    return result
}

private val CONTACT_PROJECTION = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    ContactsContract.Contacts.HAS_PHONE_NUMBER
)