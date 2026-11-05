package ru.shev.moyapamyat

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val bg = Color(0xFF101827)
private val panel = Color(0xFF202B43)
private val blue = Color(0xFF3197FF)
private val purple = Color(0xFFB56CFF)
private val green = Color(0xFF43E879)
private val orange = Color(0xFFFF9E16)
private val pink = Color(0xFFFF4385)
private val muted = Color(0xFFAAB5CE)

private data class MemoryEntry(val section: String, val title: String, val detail: String, val amount: String = "")

private data class EventItem(val day: Int, val title: String, val color: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector, val recurring: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {
    var month by remember { mutableStateOf(YearMonth.of(2026, 11)) }
    var selectedDay by remember { mutableIntStateOf(4) }
    var page by remember { mutableStateOf("Главная") }
    var addDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var dayInput by remember { mutableStateOf("10") }
    var detailInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    val entries = remember {
        mutableStateListOf<MemoryEntry>().apply {
            val raw = context.getSharedPreferences("moya_pamyat", Context.MODE_PRIVATE).getString("entries", "[]") ?: "[]"
            runCatching {
                val array = JSONArray(raw)
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    add(MemoryEntry(item.optString("section"), item.optString("title"), item.optString("detail"), item.optString("amount")))
                }
            }
        }
    }
    LaunchedEffect(entries.toList()) {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(JSONObject().put("section", entry.section).put("title", entry.title).put("detail", entry.detail).put("amount", entry.amount))
        }
        context.getSharedPreferences("moya_pamyat", Context.MODE_PRIVATE).edit().putString("entries", array.toString()).apply()
    }
    val events = remember { mutableStateListOf(
        EventItem(4, "День рождения Марины", purple, Icons.Default.Cake),
        EventItem(12, "Оплатить кредит", orange, Icons.Default.CreditCard, true),
        EventItem(18, "Купить подарок", green, Icons.Default.CardGiftcard),
        EventItem(29, "Важная дата", pink, Icons.Default.Favorite)
    ) }
    val monthEvents = events.filter { it.day <= month.lengthOfMonth() }
    val monthTitle = month.month.getDisplayName(TextStyle.FULL, Locale("ru")).replaceFirstChar { it.uppercase() } + " " + month.year

    MaterialTheme(colorScheme = darkColorScheme(primary = blue, background = bg, surface = panel, onSurface = Color.White, onBackground = Color.White, onSurfaceVariant = muted)) {
        Scaffold(
            containerColor = bg,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = bg, titleContentColor = Color.White),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Psychology, null, tint = blue, modifier = Modifier.size(48.dp))
                                Icon(Icons.Default.Favorite, null, tint = pink, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Моя память", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                                Text("Планируй. Помни. Живи.", color = muted, fontSize = 13.sp)
                            }
                        }
                    },
                    actions = { IconButton(onClick = { page = "Настройки" }) { Icon(Icons.Default.Settings, "Настройки") } }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1A253A)) {
                    val tabs = listOf("Главная" to Icons.Default.Home, "Статистика" to Icons.Default.BarChart, "Поиск" to Icons.Default.Search, "Профиль" to Icons.Default.Person)
                    tabs.forEach { tab ->
                        NavigationBarItem(selected = page == tab.first, onClick = { page = tab.first }, icon = { Icon(tab.second, tab.first) }, label = { Text(tab.first, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = blue, selectedTextColor = blue, indicatorColor = Color(0xFF263B5B), unselectedIconColor = muted, unselectedTextColor = muted))
                    }
                }
            },
            floatingActionButton = { if (page != "Главная") FloatingActionButton(onClick = { addDialog = true }, containerColor = blue) { Icon(Icons.Default.Add, "Добавить") } }
        ) { padding ->
            if (page == "Главная") {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column(Modifier.fillMaxWidth().background(panel, RoundedCornerShape(24.dp)).padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                IconButton(onClick = { month = month.minusMonths(1); selectedDay = 1 }) { Icon(Icons.Default.ChevronLeft, "Назад") }
                                Text(monthTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { month = month.plusMonths(1); selectedDay = 1 }) { Icon(Icons.Default.ChevronRight, "Вперёд") }
                            }
                            Row(Modifier.fillMaxWidth()) {
                                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { Text(it, Modifier.weight(1f), color = muted, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                            }
                            HorizontalDivider(color = Color(0xFF34415F), modifier = Modifier.padding(vertical = 8.dp))
                            val total = CalendarRules.cellCount(month)
                            for (week in 0 until total / 7) {
                                Row(Modifier.fillMaxWidth()) {
                                    for (col in 0..6) {
                                        val day = CalendarRules.dayAtCell(month, week * 7 + col)
                                        Box(Modifier.weight(1f).height(47.dp), contentAlignment = Alignment.Center) {
                                            if (day != null) {
                                                val item = monthEvents.firstOrNull { it.day == day }
                                                if (day == selectedDay) Box(Modifier.size(37.dp).background(blue, CircleShape))
                                                Column(Modifier.fillMaxSize().clickable { selectedDay = day }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                    Text(day.toString(), color = Color.White, fontWeight = FontWeight.SemiBold)
                                                    if (item != null) Box(Modifier.padding(top = 2.dp).size(6.dp).background(item.color, CircleShape))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Column(Modifier.fillMaxWidth().background(panel, RoundedCornerShape(24.dp)).padding(vertical = 8.dp)) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, null)
                                Spacer(Modifier.width(9.dp))
                                Text("Ближайшие события", fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("Все события ➜", color = blue, fontSize = 12.sp, modifier = Modifier.clickable { page = "События" })
                            }
                            val upcoming = monthEvents.sortedBy { it.day }.filter { it.day >= selectedDay }.take(4)
                            val shown = if (upcoming.isEmpty()) monthEvents.sortedBy { it.day }.take(4) else upcoming
                            if (shown.isEmpty()) Text("На этот месяц событий пока нет", color = muted, modifier = Modifier.padding(16.dp))
                            shown.forEach { item ->
                                Row(Modifier.fillMaxWidth().clickable { selectedDay = item.day }.padding(horizontal = 15.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(42.dp).background(item.color.copy(alpha = .18f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(item.icon, null, tint = item.color) }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(item.day.toString() + " число", color = item.color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(item.title, fontSize = 15.sp)
                                        if (item.recurring) Text("Повторяется каждый месяц", color = muted, fontSize = 10.sp)
                                    }
                                    Icon(Icons.Default.ChevronRight, null, tint = muted)
                                }
                                HorizontalDivider(color = Color(0xFF34415F), modifier = Modifier.padding(horizontal = 14.dp))
                            }
                        }
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Твоё пространство", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Tile("Память", Icons.Default.Psychology, purple, Modifier.weight(1f)) { page = "Память" }
                                Tile("Задачи", Icons.Default.CheckBox, blue, Modifier.weight(1f)) { page = "Задачи" }
                                Tile("Чаты", Icons.Default.Chat, green, Modifier.weight(1f)) { page = "Чаты" }
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Tile("Финансы", Icons.Default.AccountBalanceWallet, orange, Modifier.weight(1f)) { page = "Финансы" }
                                Tile("Друзья", Icons.Default.People, pink, Modifier.weight(1f)) { page = "Друзья" }
                                Tile("Календарь", Icons.Default.CalendarMonth, blue, Modifier.weight(1f)) { page = "Календарь" }
                            }
                        }
                    }
                }
            } else {
                val sectionEntries = entries.filter { it.section == page }
                Column(Modifier.fillMaxSize().padding(padding).padding(18.dp)) {
                    Text(page, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(when (page) {
                        "Память" -> "Сохраняй важные мысли, идеи и воспоминания."
                        "Задачи" -> "Записывай дела и отмечай, что важно."
                        "Чаты" -> "Личные заметки о разговорах и договорённостях."
                        "Финансы" -> "Учитывай расходы, доходы и финансовые заметки."
                        "Друзья" -> "Храни важные сведения о близких людях."
                        "Календарь", "События" -> "Твои события и важные даты."
                        "Статистика" -> "Обзор сохранённых записей по разделам."
                        "Поиск" -> "Поиск по сохранённым записям."
                        "Профиль" -> "Твоё личное пространство."
                        else -> "Раздел приложения «Моя память»."
                    }, color = muted)
                    Spacer(Modifier.height(18.dp))
                    if (page == "Статистика") {
                        Text("Всего записей: ${entries.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        listOf("Память", "Задачи", "Чаты", "Финансы", "Друзья").forEach { name ->
                            Text("$name: ${entries.count { it.section == name }}", modifier = Modifier.padding(top = 10.dp), color = muted)
                        }
                    } else if (page == "Поиск") {
                        var query by remember { mutableStateOf("") }
                        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Найти запись") }, singleLine = true)
                        val results = entries.filter { it.title.contains(query, true) || it.detail.contains(query, true) }
                        Spacer(Modifier.height(12.dp))
                        if (results.isEmpty()) Text("Ничего не найдено", color = muted)
                        results.forEach { EntryCard(it) }
                    } else if (page == "События" || page == "Календарь") {
                        monthEvents.sortedBy { it.day }.forEach { Text(it.day.toString() + " число — " + it.title, Modifier.padding(vertical = 8.dp)) }
                    } else {
                        if (sectionEntries.isEmpty()) Text("Пока здесь пусто. Нажми +, чтобы добавить первую запись.", color = muted, modifier = Modifier.padding(vertical = 12.dp))
                        sectionEntries.forEach { EntryCard(it) }
                    }
                }
            }
        }
        if (addDialog) {
            AlertDialog(onDismissRequest = { addDialog = false }, title = { Text("Новое событие") },
                text = {
                    Column {
                        OutlinedTextField(title, { title = it }, label = { Text("Название события") })
                        Spacer(Modifier.height(8.dp))
                        if (page == "Главная" || page == "Календарь" || page == "События") {
                            OutlinedTextField(dayInput, { dayInput = it.filter(Char::isDigit).take(2) }, label = { Text("Число месяца") })
                        } else {
                            OutlinedTextField(detailInput, { detailInput = it }, label = { Text("Описание (необязательно)") }, minLines = 2)
                            if (page == "Финансы") OutlinedTextField(amountInput, { amountInput = it }, label = { Text("Сумма") }, singleLine = true)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (title.isNotBlank()) {
                            if (page == "Главная" || page == "Календарь" || page == "События") {
                                val d = dayInput.toIntOrNull()
                                if (d != null && CalendarRules.isValidDay(month, d)) {
                                    events.add(EventItem(d, title.trim(), blue, Icons.Default.Event))
                                    selectedDay = d
                                    title = ""
                                    addDialog = false
                                }
                            } else {
                                val section = if (page in listOf("Память", "Задачи", "Чаты", "Финансы", "Друзья")) page else "Память"
                                entries.add(MemoryEntry(section, title.trim(), detailInput.trim(), amountInput.trim()))
                                title = ""
                                detailInput = ""
                                amountInput = ""
                                addDialog = false
                            }
                        }
                    }) { Text("Сохранить") }
                },
                dismissButton = { TextButton(onClick = { addDialog = false }) { Text("Отмена") } }
            )
        }
    }
}

@Composable
private fun Tile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, modifier: Modifier, action: () -> Unit) {
    Column(modifier.height(92.dp).background(tint.copy(alpha = .17f), RoundedCornerShape(17.dp)).clickable(onClick = action).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(5.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun EntryCard(entry: MemoryEntry) {
    Column(
        Modifier.fillMaxWidth().padding(bottom = 10.dp)
            .background(panel, RoundedCornerShape(16.dp)).padding(14.dp)
    ) {
        Text(entry.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        if (entry.detail.isNotBlank()) Text(entry.detail, color = muted, modifier = Modifier.padding(top = 5.dp))
        if (entry.amount.isNotBlank()) Text(entry.amount, color = green, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
    }
}
