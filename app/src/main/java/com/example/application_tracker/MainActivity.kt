package com.example.application_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.application_tracker.application.Application
import com.example.application_tracker.application.Contact
import com.example.application_tracker.service.ApplicationService
import com.example.application_tracker.ui.theme.Application_TrackerTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = ApplicationService(this)
        enableEdgeToEdge()

        setContent {
            val company = remember { mutableStateOf("") }
            val address = remember { mutableStateOf("") }
            val position = remember { mutableStateOf("") }
            val dateText = remember { mutableStateOf("") }
            val state = remember { mutableStateOf(Application.posStates.PENDING) }
            val contactName = remember { mutableStateOf("") }
            val contactMail = remember { mutableStateOf("") }
            val contactPhone = remember { mutableStateOf("") }
            val errorMessage = remember { mutableStateOf("") }
            val missingFields = remember { mutableListOf<String>() }
            val expandedApp = remember { mutableStateOf<Application?>(null) }
            val showAddForm = remember { mutableStateOf(false) }
            val expandedMenu = remember { mutableStateOf<Application?>(null) }
            val applications = remember {
                mutableStateListOf<Application>().apply {
                    addAll(service.applications)
                }
            }
            val showDeleteDialog = remember { mutableStateOf(false) }
            val editingApp = remember { mutableStateOf<Application?>(null) }


            Application_TrackerTheme {

                Scaffold(

                    bottomBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            if (errorMessage.value.isNotBlank() && showAddForm.value) {
                                Text(
                                    text = errorMessage.value,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (!showAddForm.value) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = { showAddForm.value = true }
                                    ) {
                                        Text("New Application")
                                    }
                                }

                                if (showAddForm.value) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            missingFields.clear()
                                            if (company.value.isBlank()) missingFields.add("company")
                                            if (address.value.isBlank()) missingFields.add("address")
                                            if (position.value.isBlank()) missingFields.add("position")
                                            if (dateText.value.isBlank()) missingFields.add("date")

                                            if (!missingFields.isEmpty()) {
                                                errorMessage.value = "required fields empty:\n${
                                                    missingFields.joinToString(", ")
                                                }"
                                                return@Button
                                            }

                                            val date = try {
                                                LocalDate.parse(dateText.value)
                                            } catch (e: Exception) {
                                                LocalDate.now()
                                            }
                                            val contact =
                                                if (contactName.value.isNotBlank() || contactMail.value.isNotBlank() || contactPhone.value.isNotBlank()) {
                                                    Contact(
                                                        contactName.value,
                                                        contactMail.value,
                                                        contactPhone.value
                                                    )
                                                } else {
                                                    null
                                                }

                                            val app = Application(
                                                company.value,
                                                address.value,
                                                position.value,
                                                date,
                                                Application.posStates.PENDING,
                                                null,
                                                contact
                                            )

                                            service.addApplication(app)
                                            service.saveAll()

                                            applications.clear()
                                            applications.addAll(service.applications)

                                            company.value = ""
                                            address.value = ""
                                            position.value = ""
                                            dateText.value = ""
                                            showAddForm.value = false
                                        },

                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Green,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Add")
                                    }

                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = { showAddForm.value = false },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        )

                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    ) {

                        if (showAddForm.value) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "*required field",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "*company:",
                                    fontWeight = FontWeight.Bold
                                )

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = company.value,
                                    onValueChange = { company.value = it },
                                    label = { Text("company") })

                                Text(
                                    text = "*address:",
                                    fontWeight = FontWeight.Bold
                                )

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = address.value,
                                    onValueChange = { address.value = it },
                                    label = { Text("address") })

                                Text(
                                    text = "*position:",
                                    fontWeight = FontWeight.Bold
                                )

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = position.value,
                                    onValueChange = { position.value = it },
                                    label = { Text("position") })

                                Text(
                                    text = "*date:",
                                    fontWeight = FontWeight.Bold
                                )

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = dateText.value,
                                    onValueChange = { dateText.value = it },
                                    label = { Text("date(YYYY-MM-DD)") })

                                Text(
                                    text = "contact name:",
                                    fontWeight = FontWeight.Bold
                                )

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = contactName.value,
                                    onValueChange = { contactName.value = it },
                                    label = { Text("contact name") })

                                Text(
                                    text = "contact mail:",
                                    fontWeight = FontWeight.Bold
                                )

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = contactMail.value,
                                    onValueChange = { contactMail.value = it },
                                    label = { Text("contact mail") })

                                Text(
                                    text = "contact phone:",
                                    fontWeight = FontWeight.Bold
                                )

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = contactPhone.value,
                                    onValueChange = { contactPhone.value = it },
                                    label = { Text("contact phone") })
                            }

                        }
                        if (editingApp.value != null) {
                            editingApp.value?.let { currentApp ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "Edit:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.DarkGray
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = currentApp.company,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = currentApp.position,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                                    Text(
                                        text = "*required field",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(text = "*company:", fontWeight = FontWeight.Bold)
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = company.value,
                                        onValueChange = { company.value = it },
                                        label = { Text("company") })

                                    Text(text = "*address:", fontWeight = FontWeight.Bold)
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = address.value,
                                        onValueChange = { address.value = it },
                                        label = { Text("address") })

                                    Text(text = "*position:", fontWeight = FontWeight.Bold)
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = position.value,
                                        onValueChange = { position.value = it },
                                        label = { Text("position") })

                                    Text(text = "*date:", fontWeight = FontWeight.Bold)
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = dateText.value,
                                        onValueChange = { dateText.value = it },
                                        label = { Text("date(YYYY-MM-DD)") })

                                    Text(
                                        text = "contact name:",
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = contactName.value,
                                        onValueChange = { contactName.value = it },
                                        label = { Text("contact name") })

                                    Text(
                                        text = "contact mail:",
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = contactMail.value,
                                        onValueChange = { contactMail.value = it },
                                        label = { Text("contact mail") })

                                    Text(
                                        text = "contact phone:",
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = contactPhone.value,
                                        onValueChange = { contactPhone.value = it },
                                        label = { Text("contact phone") })

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = {

                                                editingApp.value?.let { selectedApp ->

                                                    selectedApp.company = company.value
                                                    selectedApp.address = address.value
                                                    selectedApp.position = position.value

                                                    selectedApp.date = try {
                                                        LocalDate.parse(dateText.value)
                                                    } catch (e: Exception) {
                                                        selectedApp.date
                                                    }

                                                    selectedApp.contact?.let { contact ->
                                                        contact.name = contactName.value
                                                        contact.mail = contactMail.value
                                                        contact.phone = contactPhone.value
                                                    }
                                                }


                                                service.saveAll()

                                                applications.clear()
                                                applications.addAll(service.applications)

                                                editingApp.value = null
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Green,
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Text("Save")
                                        }

                                        Button(
                                            onClick = { editingApp.value = null },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red,
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Text("Cancel")
                                        }

                                    }
                                }
                            }
                        }else if (!showAddForm.value) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)

                            ) {

                                items(applications) { app ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 1.dp)
                                            .combinedClickable(
                                                onClick = {
                                                    expandedApp.value =
                                                        if (expandedApp.value == app) null else app
                                                },

                                                onLongClick = {
                                                    expandedMenu.value = app
                                                }
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = Color.Black,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (expandedApp.value == app) {
                                                    Color.LightGray
                                                } else {
                                                    Color.Transparent
                                                }
                                            )
                                            .padding(12.dp)

                                    ) {

                                        Row {
                                            Text(
                                                text = app.company,
                                                modifier = Modifier.weight(1f),
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                app.date.toString(),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                app.state.toString(),
                                                modifier = Modifier.weight(1f),
                                                when (app.state) {
                                                    Application.posStates.PENDING -> Color.Magenta
                                                    Application.posStates.CANCELLED -> Color.Red
                                                    Application.posStates.INTERVIEW -> Color.Green
                                                    Application.posStates.GHOSTED -> Color.Blue
                                                    else -> Color.Black
                                                },
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = expandedMenu.value == app,
                                            onDismissRequest = {
                                                expandedMenu.value = null
                                            }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Edit") },
                                                onClick = {
                                                    expandedMenu.value = null

                                                    company.value = app.company
                                                    address.value = app.address
                                                    position.value = app.position
                                                    dateText.value = app.date.toString()
                                                    contactName.value = app.contact?.name ?: ""
                                                    contactMail.value = app.contact?.mail ?: ""
                                                    contactPhone.value = app.contact?.phone ?: ""

                                                    editingApp.value = app
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "Delete",
                                                        color = Color.Red
                                                    )
                                                },
                                                onClick = {
                                                    expandedMenu.value = null
                                                    showDeleteDialog.value = true
                                                }
                                            )
                                        }
                                        if (showDeleteDialog.value) {
                                            AlertDialog(
                                                onDismissRequest = {

                                                    showDeleteDialog.value = false
                                                },
                                                title = {
                                                    Text(text = "delete application")
                                                },
                                                text = {
                                                    Text(text = "are you sure you want to delete '${app.company}'?")
                                                },
                                                confirmButton = {
                                                    Button(
                                                        onClick = {
                                                            showDeleteDialog.value = false


                                                            service.deleteApplication(app)
                                                            service.saveAll()

                                                            applications.clear()
                                                            applications.addAll(service.applications)
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color.Red
                                                        )
                                                    ) {
                                                        Text("Delete")
                                                    }
                                                },
                                                dismissButton = {
                                                    Button(
                                                        onClick = {
                                                            showDeleteDialog.value = false
                                                        }
                                                    ) {
                                                        Text("Cancel")
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    if (expandedApp.value == app) {

                                        Column(
                                            modifier = Modifier.padding(top = 8.dp)
                                        ) {
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            fontWeight = FontWeight.Bold,
                                                            textDecoration = TextDecoration.Underline
                                                        )
                                                    ) {
                                                        append("address:")
                                                    }
                                                    append(" " + app.address)
                                                }
                                            )

                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            fontWeight = FontWeight.Bold,
                                                            textDecoration = TextDecoration.Underline
                                                        )
                                                    ) {
                                                        append("position:")
                                                    }
                                                    append(" " + app.position)
                                                }
                                            )

                                            app.contact?.let { contact ->
                                                Text(
                                                    text = buildAnnotatedString {
                                                        withStyle(
                                                            style = SpanStyle(
                                                                fontWeight = FontWeight.Bold,
                                                                textDecoration = TextDecoration.Underline
                                                            )
                                                        ) {
                                                            append("contact name:")
                                                        }
                                                        append(" " + contact.name)
                                                    }
                                                )
                                                Text(
                                                    text = buildAnnotatedString {
                                                        withStyle(
                                                            style = SpanStyle(
                                                                fontWeight = FontWeight.Bold,
                                                                textDecoration = TextDecoration.Underline
                                                            )
                                                        ) {
                                                            append("contact mail:")
                                                        }
                                                        append(" " + contact.mail)
                                                    }
                                                )
                                                Text(
                                                    text = buildAnnotatedString {
                                                        withStyle(
                                                            style = SpanStyle(
                                                                fontWeight = FontWeight.Bold,
                                                                textDecoration = TextDecoration.Underline
                                                            )
                                                        ) {
                                                            append("contact phone: ")
                                                        }
                                                        append(" " + contact.phone)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
