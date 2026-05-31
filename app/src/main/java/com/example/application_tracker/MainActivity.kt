package com.example.application_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

        println(service.getApplications().size)

        enableEdgeToEdge()

        setContent {
            var company = remember { mutableStateOf("") }
            var address = remember {mutableStateOf("")}
            var position = remember {mutableStateOf("")}
            var dateText = remember {mutableStateOf("")}
            var state = remember {mutableStateOf(Application.posStates.PENDING)}
            var contactName = remember {mutableStateOf("")}
            var contactMail = remember {mutableStateOf("")}
            var contactPhone = remember {mutableStateOf("")}
            var errorMessage = remember {mutableStateOf("")}
            val missingFields = mutableListOf<String>()

            val applications = remember {
                mutableStateListOf<Application>().apply {
                    addAll(service.getApplications())
                }
            }

            var showForm = remember {
                mutableStateOf(false)
            }



            Application_TrackerTheme {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            showForm.value = true
                        }
                    ) {
                        Text("New")
                    }
                    if (showForm.value) {

                        Button(
                            onClick = {

                                missingFields.clear()

                                if (company.value.isBlank()) missingFields.add("company")
                                if (address.value.isBlank()) missingFields.add("address")
                                if (position.value.isBlank()) missingFields.add("position")
                                if (dateText.value.isBlank()) missingFields.add("date")

                                if (!missingFields.isEmpty()) {
                                    errorMessage.value =
                                        "required fields empty: ${missingFields.joinToString(", ")}"
                                    return@Button
                                }

                                val date = try {
                                    LocalDate.parse(dateText.value)
                                } catch (e: Exception) {
                                    LocalDate.now()
                                }

                                val app = Application(
                                    company.value,
                                    address.value,
                                    position.value,
                                    date,
                                    Application.posStates.PENDING,
                                    null,
                                    null,
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
                                missingFields.clear()
                                errorMessage.value = ""
                            }
                        ) {
                            Text("Add")
                        }
                        Text("* required field")
                        TextField(
                            value = company.value,
                            onValueChange = {company.value = it},
                            label = {Text("Company*")}
                        )

                        TextField(
                            value = address.value,
                            onValueChange = {address.value = it},
                            label = {Text("address*")}
                        )

                        TextField(
                            value = position.value,
                            onValueChange = {position.value = it},
                            label = {Text("position*")}
                        )
                        TextField(
                            value = dateText.value,
                            onValueChange = {dateText.value = it},
                            label = {Text("date(YYYY-MM-DD)*")}
                        )
                        TextField(
                            value = contactName.value,
                            onValueChange = {contactName.value = it},
                            label = {Text("contactName")}
                        )
                        TextField(
                            value = contactMail.value,
                            onValueChange = {contactMail.value = it},
                            label = {Text("contactMail")}
                        )
                        TextField(
                            value = contactPhone.value,
                            onValueChange = {contactPhone.value = it},
                            label = {Text("contactPhone")}
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)

                    ) {

                        items(applications) { app ->

                            Text(app.getCompany())
                        }
                    }
                    if (errorMessage.value.isNotEmpty()) {
                        Text(
                            text = errorMessage.value,
                            color = androidx.compose.ui.graphics.Color.Red
                        )
                    }
                }
            }
        }
    }
}