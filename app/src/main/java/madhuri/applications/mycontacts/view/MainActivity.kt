package madhuri.applications.mycontacts.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import madhuri.applications.mycontacts.databinding.ActivityMainBinding
import madhuri.applications.mycontacts.model.Contact
import madhuri.applications.mycontacts.utils.retrieveAllContacts

const val PERMISSION_REQUEST_READ_CONTACT = 0


class MainActivity : AppCompatActivity() {

    private lateinit var layout: View
    private lateinit var mainBinding: ActivityMainBinding
    private val contactsAdapter = ContactsAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        layout = mainBinding.root
        setContentView(layout)

        // adding new contact on click of the FAB
        mainBinding.fab.setOnClickListener {
            // Creates a new Intent to insert a contact
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                // Sets the MIME type to match the Contacts Provider
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }
            startActivity(intent)
        }

        checkPermission()

        // recyclerview
        mainBinding.recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactsAdapter
            addItemDecoration(DividerItemDecoration(context, 1))
        }

    }


    private fun updateContactsInRecyclerViewAdapter() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        contactsAdapter.updateContacts(retrieveAllContacts() as MutableList<Contact>)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_CONTACT) {
            // Request for READ_CONTACTS permission.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Read Contacts Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
                // fetching the contacts from the device
                updateContactsInRecyclerViewAdapter()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Read Contacts Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Function to check and request permission.
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSION_REQUEST_READ_CONTACT
            )
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT)
                .show()
            // fetching the contacts from the device
            updateContactsInRecyclerViewAdapter()
        }
    }


}