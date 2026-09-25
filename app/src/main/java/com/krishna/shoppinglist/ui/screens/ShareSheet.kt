package com.krishna.shoppinglist.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.krishna.shoppinglist.data.ShoppingItem
import com.krishna.shoppinglist.data.ShoppingList
import com.krishna.shoppinglist.ui.theme.WhatsAppGreen

private const val WHATSAPP_PACKAGE = "com.whatsapp"

private fun buildShareText(list: ShoppingList, items: List<ShoppingItem>): String {
    val header = "${list.name}"
    val body = items.joinToString("\n") { "- ${it.name}${if (it.isDone) " (done)" else ""}" }
    return "$header\n$body"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(
    list: ShoppingList,
    items: List<ShoppingItem>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val doneCount = items.count { it.isDone }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Share \"${list.name}\"", style = MaterialTheme.typography.titleMedium)
            Text(
                "${items.size} items · $doneCount done",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { shareViaWhatsApp(context, buildShareText(list, items)); onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                WhatsAppIcon()
                Spacer(Modifier.width(10.dp))
                Text("Share via WhatsApp")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { copyToClipboard(context, buildShareText(list, items)); onDismiss() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Link, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text("Copy link")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { shareAsPlainText(context, buildShareText(list, items)); onDismiss() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Notes, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text("Share as plain text")
            }

            Spacer(Modifier.height(14.dp))
            Text(
                "Anyone you send this to sees a snapshot of the list right now. Real-time shared editing needs a backend and isn't in this build yet.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun WhatsAppIcon() {
    // Simple glyph so we don't depend on an external icon pack for the brand mark.
    Row {
        Text("\u2611", color = Color.White)
    }
}

private fun shareViaWhatsApp(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage(WHATSAPP_PACKAGE)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp isn't installed", Toast.LENGTH_SHORT).show()
    }
}

private fun shareAsPlainText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share list"))
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Shopping list", text))
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}
