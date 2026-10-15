package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.QuicksandFontFamily
import kotlinx.coroutines.launch

@Composable
fun SettingsBackupDialog(
    isPinEnabled: Boolean,
    currentPin: String,
    onSetPin: (String, Boolean) -> Unit,
    onDisablePin: () -> Unit,
    onExportBackup: suspend () -> String,
    onImportBackup: suspend (String) -> Boolean,
    onDeleteAllData: () -> Unit,
    onShareDaySummary: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showExportResultDialog by remember { mutableStateOf(false) }
    var exportedJsonContent by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Settings & Privacy 🔒",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPrimary
                )
                Text(
                    text = "100% offline & private on your device.",
                    fontFamily = QuicksandFontFamily,
                    fontSize = 12.sp,
                    color = CuteSubtleText
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // PIN Lock Section
                Text(
                    text = "Security",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CuteDarkText
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PastelPinkBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PastelPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "PIN Lock",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CuteDarkText
                                )
                                Text(
                                    text = if (isPinEnabled) "Lock active (4-digit)" else "Disabled",
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 11.sp,
                                    color = CuteSubtleText
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isPinEnabled) {
                                TextButton(onClick = { showPinSetupDialog = true }) {
                                    Text("Change", fontSize = 12.sp, color = PastelPrimary)
                                }
                            }
                            Switch(
                                checked = isPinEnabled,
                                onCheckedChange = { enable ->
                                    if (enable) {
                                        showPinSetupDialog = true
                                    } else {
                                        onDisablePin()
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = PastelPrimary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Backup & Migration
                Text(
                    text = "Data Backup & Migration",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CuteDarkText
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Export Button
                Button(
                    onClick = {
                        scope.launch {
                            val json = onExportBackup()
                            exportedJsonContent = json
                            showExportResultDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F0FF), contentColor = Color(0xFF5F3DC4)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export backup (JSON)", fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Import Button
                Button(
                    onClick = { showImportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBFBEE), contentColor = Color(0xFF2B8A3E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import backup (JSON)", fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Share Today's Summary
                Button(
                    onClick = onShareDaySummary,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF4E6), contentColor = Color(0xFFD9480F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share / Save Today's Journal", fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Danger Zone
                Text(
                    text = "Danger Zone",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFC92A2A)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE3E3), contentColor = Color(0xFFC92A2A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete all data", fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
            ) {
                Text("Close")
            }
        }
    )

    // PIN Setup Dialog
    if (showPinSetupDialog) {
        var pin1 by remember { mutableStateOf("") }
        var pin2 by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPinSetupDialog = false },
            title = { Text("Set 4-Digit PIN 🔑", fontFamily = PatrickHandFontFamily, fontSize = 22.sp, color = PastelPrimary) },
            text = {
                Column {
                    Text("Choose a 4-digit code to protect your journal.", fontFamily = QuicksandFontFamily, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pin1,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin1 = it },
                        label = { Text("Enter PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pin2,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin2 = it },
                        label = { Text("Confirm PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = error!!, color = Color(0xFFD32F2F), fontSize = 12.sp, fontFamily = QuicksandFontFamily)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pin1.length != 4) {
                            error = "PIN must be exactly 4 digits."
                        } else if (pin1 != pin2) {
                            error = "PINs do not match."
                        } else {
                            onSetPin(pin1, true)
                            showPinSetupDialog = false
                            Toast.makeText(context, "PIN set successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
                ) { Text("Save PIN") }
            },
            dismissButton = {
                TextButton(onClick = { showPinSetupDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Export Result Dialog
    if (showExportResultDialog) {
        AlertDialog(
            onDismissRequest = { showExportResultDialog = false },
            title = { Text("Backup Exported! 🎉", fontFamily = PatrickHandFontFamily, fontSize = 22.sp, color = PastelPrimary) },
            text = {
                Column {
                    Text(
                        text = "Your journal backup is ready. You can copy it or share it to save a backup file.",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PastelPinkBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Text(
                            text = exportedJsonContent,
                            fontFamily = QuicksandFontFamily,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            },
            confirmButton = {
                Row {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("DailyTaskJournalBackup", exportedJsonContent))
                            Toast.makeText(context, "Copied backup to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, exportedJsonContent)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Daily Journal Backup"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F3DC4))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportResultDialog = false }) { Text("Done") }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        var importInput by remember { mutableStateOf("") }
        var importError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Backup (JSON) 📥", fontFamily = PatrickHandFontFamily, fontSize = 22.sp, color = PastelPrimary) },
            text = {
                Column {
                    Text(
                        text = "Paste the JSON backup string below to restore your journal:",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importInput,
                        onValueChange = { importInput = it },
                        label = { Text("Backup JSON") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                    if (importError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = importError!!, color = Color(0xFFD32F2F), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val success = onImportBackup(importInput.trim())
                            if (success) {
                                Toast.makeText(context, "Journal restored successfully!", Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                            } else {
                                importError = "Invalid JSON backup data. Please check and retry."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
                ) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete All Data?", fontFamily = PatrickHandFontFamily, fontSize = 22.sp, color = Color(0xFFC92A2A)) },
            text = {
                Text(
                    text = "Are you sure? This will erase all recorded days, study logs, reflections, and custom template changes. This cannot be undone.",
                    fontFamily = QuicksandFontFamily,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllData()
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "All data reset successfully.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC92A2A))
                ) { Text("Yes, Delete All") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}
