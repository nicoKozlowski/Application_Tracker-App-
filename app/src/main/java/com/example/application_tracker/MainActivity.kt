package com.example.application_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.Font
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
            val showForm = remember { mutableStateOf(false) }

            val applications = remember {
                mutableStateListOf<Application>().apply {
                    addAll(service.getApplications())
                }
            }



            Application_TrackerTheme {

                Scaffold(

                    bottomBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            if (errorMessage.value.isNotBlank() && showForm.value) {
                                Text(
                                    text = errorMessage.value,
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (!showForm.value) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = { showForm.value = true }
                                    ) {
                                        Text("New Application")
                                    }
                                }

                                if (showForm.value) {
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
                                            applications.addAll(service.getApplications())

                                            company.value = ""
                                            address.value = ""
                                            position.value = ""
                                            dateText.value = ""
                                            showForm.value = false
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
                                        onClick = { showForm.value = false },
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

                        if (showForm.value) {
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

                        if (!showForm.value) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)

                            ) {

                                items(applications) { app ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 1.dp)
                                            .clickable() {
                                                expandedApp.value =
                                                    if (expandedApp.value == app) null else app
                                            }
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
