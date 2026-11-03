package ru.shev.moyapamyat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
        Section("Чаты", "Личные разговоры", Icons.Default.Chat),
        Section("Финансы", "Доходы и расходы", Icons.Default.AccountBalanceWallet),
        Section("Друзья", "Важные люди", Icons.Default.People),
        Section("Календарь", "События и даты", Icons.Default.CalendarMonth)
    )
    var selected by remember { mutableStateOf("Главная") }
    var dark by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }

    val memories = remember { mutableStateListOf<String>() }
    val tasks = remember { mutableStateListOf<String>() }
    val chats = remember { mutableStateListOf<String>() }
    val finances = remember { mutableStateListOf<String>() }
    val friends = remember { mutableStateListOf<String>() }
    val events = remember { mutableStateListOf<String>() }
    val taskDone = remember { mutableStateStateMapOf<Int, Boolean>() }

    val data = when (selected) {
        "Память" -> memories
        "Задачи" -> tasks
        "Чаты" -> chats
        "Финансы" -> finances
        "Друзья" -> friends
        "Календарь" -> events
        else -> memories
    }
    val colors = if (dark) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Моя память", fontWeight = FontWeight.Bold)
                            Text("Твоё личное пространство", style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    actions = {
                        IconButton(onClick = { dark = !dark }) {
                            Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Сменить тему")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (selected != "Главная") {
                    FloatingActionButton(onClick = { draft = ""; showAdd = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                }
            },
            bottomBar = {
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
            }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)
            ) {
                if (selected == "Главная") {
                    HomeContent(sections, memories.size, tasks.size, events.size) { selected = it }
                } else {
                    SectionContent(selected, data, taskDone)
                }
            }
        }

        if (showAdd) {
            AlertDialog(
                onDismissRequest = { showAdd = false },
                title = { Text(when (selected) {
                    "Память" -> "Новая запись"
                    "Задачи" -> "Новая задача"
                    "Финансы" -> "Новая операция"
                    "Друзья" -> "Добавить человека"
                    "Календарь" -> "Новое событие"
                    else -> "Новое сообщение"
                }) },
                text = {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        label = { Text("Напиши здесь") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (draft.isNotBlank()) data.add(draft.trim())
                        showAdd = false
                    }) { Text("Сохранить") }
                },
                dismissButton = {
                    TextButton(onClick = { showAdd = false }) { Text("Отмена") }
                }
            )
        }
    }
}

@Composable
private fun HomeContent(
    sections: List<Section>,
    memoryCount: Int,
    taskCount: Int,
    eventCount: Int,
    open: (String) -> Unit
) {
    Spacer(Modifier.height(12.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(22.dp)) {
            Text("Всё важное — в одном месте", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Сохраняй воспоминания, записывай планы и держи рядом важных людей.")
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryValue("Записи", memoryCount)
                SummaryValue("Задачи", taskCount)
                SummaryValue("События", eventCount)
            }
        }
    }
    Spacer(Modifier.height(22.dp))
    Text("Твои разделы", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
        itemsIndexed(sections) { _, section ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { open(section.title) },
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                        Icon(section.icon, contentDescription = null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(section.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Открыть")
                }
            }
        }
    }
}

@Composable
private fun SummaryValue(label: String, value: Int) {
    Column {
        Text(value.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SectionContent(
    title: String,
    entries: SnapshotStateList<String>,
    taskDone: MutableMap<Int, Boolean>
) {
    Spacer(Modifier.height(14.dp))
    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(6.dp))
    Text(
        when (title) {
            "Память" -> "Сохраняй здесь важные мысли и моменты жизни."
            "Задачи" -> "Добавляй дела и отмечай выполненные."
            "Чаты" -> "Личные заметки и важные разговоры."
            "Финансы" -> "Записывай доходы, расходы и покупки."
            "Друзья" -> "Храни заметки о близких людях."
            else -> "Записывай важные даты и события."
        },
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))
    if (entries.isEmpty()) {
        Box(Modifier.fillMaxWidth().padding(top = 54.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Text("Пока здесь пусто", style = MaterialTheme.typography.titleMedium)
                Text("Нажми «+», чтобы добавить первую запись.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 90.dp)) {
            itemsIndexed(entries) { index, entry ->
                Card(shape = RoundedCornerShape(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (title == "Задачи") {
                            Checkbox(
                                checked = taskDone[index] == true,
                                onCheckedChange = { taskDone[index] = it }
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(entry, modifier = Modifier.weight(1f))
                        IconButton(onClick = { entries.removeAt(index); taskDone.remove(index) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Удалить")
                        }
                    }
                }
            }
        }
    }
}
