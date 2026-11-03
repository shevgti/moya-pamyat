package ru.shev.moyapamyat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class Section(val title: String, val subtitle: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MemoryApp() }
    }
}

@Composable
private fun MemoryApp() {
    val sections = listOf(
        Section("Память", "Важные моменты и записи", Icons.Default.Favorite),
        Section("Задачи", "Планы и напоминания", Icons.Default.CheckCircle),
        Section("Чаты", "Общение и заметки", Icons.Default.Chat),
        Section("Финансы", "Доходы и расходы", Icons.Default.AccountBalanceWallet),
        Section("Друзья", "Люди и контакты", Icons.Default.People),
        Section("Календарь", "События и даты", Icons.Default.CalendarMonth)
    )
    var selected by remember { mutableStateOf("Главная") }

    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold(bottomBar = {
            NavigationBar {
                listOf("Главная", "Задачи", "Чаты").forEach { item ->
                    NavigationBarItem(
                        selected = selected == item,
                        onClick = { selected = item },
                        icon = {
                            Icon(when (item) {
                                "Задачи" -> Icons.Default.CheckCircle
                                "Чаты" -> Icons.Default.Chat
                                else -> Icons.Default.Home
                            }, contentDescription = null)
                        },
                        label = { Text(item) }
                    )
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
                Text("Моя память", style = MaterialTheme.typography.headlineMedium)
                Text("Твоё личное пространство", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(24.dp))
                if (selected == "Главная") {
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp)) {
                            Text("Добро пожаловать!", style = MaterialTheme.typography.titleLarge)
                            Text("Здесь будут собраны твои воспоминания, планы и важные люди.")
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Твои разделы", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(sections) { section ->
                            Card(Modifier.fillMaxWidth().height(132.dp).clickable { selected = section.title }, shape = RoundedCornerShape(20.dp)) {
                                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Icon(section.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Column {
                                        Text(section.title, style = MaterialTheme.typography.titleMedium)
                                        Text(section.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(selected, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Раздел подготовлен. Следующий шаг — перенести сюда точный дизайн из сохранённых макетов.")
                }
            }
        }
    }
}
