package com.example.feature.home

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Category
import com.example.domain.model.TodoItem
import com.example.domain.model.TodoStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    registeredUser: String?,
    onNavigateToSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var searchByTitle by remember { mutableStateOf("") }
    var newTaskText by remember { mutableStateOf("") }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryColorHex by remember { mutableStateOf("#E6FFFF") }

    // Dialog state for long press action
    var longPressedTodo by remember { mutableStateOf<TodoItem?>(null) }

    // Color choices for creating a custom folder
    val colorOptions = listOf(
        "#EADDFF" to "Linh Lan Sáng (Tím)",
        "#E6FFFF" to "Xanh Dương Sáng",
        "#F9FFF5" to "Xanh Lá Nhạt",
        "#FFFDF0" to "Vàng Kem",
        "#FFF5F5" to "Đỏ San Hô Nhạt"
    )

    val activeTodosCount = state.todos.count { it.status == TodoStatus.ACTIVE }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF)),
        bottomBar = {
            // Persistent bottom inputs to add tasks in a gorgeous minimalist layout
            Surface(
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = Color(0xFFFEF7FF) // Seamless backdrop matching background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Thêm công việc mới",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF1D1B20)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTaskText,
                            onValueChange = { newTaskText = it },
                            placeholder = { Text("Bạn cần làm gì / học gì...", fontSize = 14.sp, color = Color(0xFF49454F)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_task_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF21005D),
                                unfocusedBorderColor = Color(0xFFF3EDF7)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                if (newTaskText.isBlank()) {
                                    Toast.makeText(context, "Vui lòng nhập tên công việc!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val selectedCatId = state.selectedCategoryId ?: state.categories.firstOrNull()?.id ?: "1"
                                viewModel.addTodo(newTaskText, selectedCatId)
                                newTaskText = ""
                                Toast.makeText(context, "Đã thêm công việc thành công!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEADDFF),
                                contentColor = Color(0xFF21005D)
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("add_task_button"),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm", modifier = Modifier.size(24.dp))
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
                .background(Color(0xFFFEF7FF))
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Danh Sách Todo",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Normal,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = if (activeTodosCount > 0) "$activeTodosCount công việc chưa hoàn thành" else "0 công việc còn lại",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF49454F),
                        fontWeight = FontWeight.Normal
                    )
                    if (registeredUser != null) {
                        Text(
                            text = "Đồng bộ: @$registeredUser",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF146C2E),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Cloud Sync navigation button
                IconButton(
                    onClick = onNavigateToSync,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFFEADDFF),
                            shape = CircleShape
                        )
                        .testTag("cloud_sync_nav_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Cloud Backup Synchronization",
                        tint = Color(0xFF21005D)
                    )
                }
            }

            // Search bar style matched to theme
            OutlinedTextField(
                value = searchByTitle,
                onValueChange = { searchByTitle = it },
                placeholder = { Text("Tìm kiếm công việc...", fontSize = 14.sp, color = Color(0xFF49454F)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = Color(0xFF49454F)) },
                trailingIcon = {
                    if (searchByTitle.isNotEmpty()) {
                        IconButton(onClick = { searchByTitle = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color(0xFF49454F))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .testTag("search_todo_bar"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF21005D),
                    unfocusedBorderColor = Color(0xFFF3EDF7)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Folders / Categories Horizontal Scroll Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thư mục của tôi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF1D1B20)
                    )
                    TextButton(
                        onClick = { showAddCategoryDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF21005D))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm thư mục", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tạo mới", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // "All" / "Tất cả" category item
                    item {
                        val isSelected = state.selectedCategoryId == null
                        InputChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("Tất cả", fontWeight = FontWeight.Medium) },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = Color(0xFFEADDFF),
                                selectedLabelColor = Color(0xFF21005D),
                                containerColor = Color.White,
                                labelColor = Color(0xFF49454F)
                            ),
                            border = InputChipDefaults.inputChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Color.Transparent else Color(0xFF79747E),
                                borderWidth = 1.dp
                            )
                        )
                    }

                    items(state.categories) { category ->
                        val isSelected = state.selectedCategoryId == category.id
                        val designColor = category.colorHex.toColorOrFallback()

                        InputChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category.id) },
                            label = { Text(category.name, fontWeight = FontWeight.Medium) },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = if (isSelected) Color(0xFFEADDFF) else Color.White,
                                selectedLabelColor = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F),
                                containerColor = Color.White,
                                labelColor = Color(0xFF49454F)
                            ),
                            border = InputChipDefaults.inputChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Color.Transparent else Color(0xFF79747E),
                                borderWidth = 1.dp
                            ),
                            trailingIcon = {
                                // Don't allow deleting default seed categories 1-4 for stability, but custom ones are deletable
                                if (category.id != "1" && category.id != "2" && category.id != "3" && category.id != "4") {
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteCategory(category.id)
                                            Toast.makeText(context, "Đã xóa thư mục!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Delete Folder",
                                            modifier = Modifier.size(12.dp),
                                            tint = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main To-Do list partition (Active on top, Completed & Failed on bottom)
            val filteredTodos = state.todos.filter { item ->
                val matchesCategory = state.selectedCategoryId == null || item.categoryId == state.selectedCategoryId
                val matchesSearch = searchByTitle.isBlank() || item.title.contains(searchByTitle, ignoreCase = true)
                matchesCategory && matchesSearch
            }

            val activeTodos = filteredTodos.filter { it.status == TodoStatus.ACTIVE }
            val completedOrFailedTodos = filteredTodos.filter { it.status != TodoStatus.ACTIVE }

            if (filteredTodos.isEmpty()) {
                // Beautiful empty illustration state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Empty folder logo",
                            tint = Color(0xFFEADDFF),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Thư mục trống",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF21005D)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Chưa có việc vặt hay công việc học tập nào ở đây. Nhập một việc mới ở phía dưới nhé!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF49454F),
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. ACTIVE ITEMS (Shown on top)
                    if (activeTodos.isNotEmpty()) {
                        item {
                            Text(
                                text = "CẦN THỰC HIỆN (${activeTodos.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                ),
                                color = Color(0xFF1D1B20).copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }

                        items(activeTodos, key = { it.id }) { todo ->
                            val folder = state.categories.find { it.id == todo.categoryId }
                            val baseColor = folder?.colorHex?.toColorOrFallback() ?: Color(0xFFEADDFF)

                            TodoItemRow(
                                todo = todo,
                                folderName = folder?.name ?: "Tất cả",
                                backgroundColor = baseColor,
                                onClick = { viewModel.toggleTodoCompleted(todo.id) },
                                onLongClick = { longPressedTodo = todo },
                                onDelete = { viewModel.deleteTodo(todo.id) }
                            )
                        }
                    }

                    // Spacer between Active and Completed sections
                    if (activeTodos.isNotEmpty() && completedOrFailedTodos.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 2. COMPLETED & FAILED ITEMS (Shown on bottom)
                    if (completedOrFailedTodos.isNotEmpty()) {
                        item {
                            Text(
                                text = "ĐA HOÀN THÀNH (${completedOrFailedTodos.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                ),
                                color = Color(0xFF49454F).copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }

                        items(completedOrFailedTodos, key = { it.id }) { todo ->
                            val folder = state.categories.find { it.id == todo.categoryId }
                            val baseColor = if (todo.status == TodoStatus.FAILED) {
                                Color(0xFFFFF1F1)
                            } else {
                                folder?.colorHex?.toColorOrFallback() ?: Color(0xFFEADDFF)
                            }

                            TodoItemRow(
                                todo = todo,
                                folderName = folder?.name ?: "Tất cả",
                                backgroundColor = baseColor,
                                onClick = { viewModel.toggleTodoCompleted(todo.id) },
                                onLongClick = { viewModel.toggleTodoCompleted(todo.id) }, // Quick reset to active
                                onDelete = { viewModel.deleteTodo(todo.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal adding Category (Folder) Dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Tạo Thư Mục Mới", color = Color(0xFF21005D), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Tên thư mục (ví dụ: Việc học, Đọc sách)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chọn màu sắc thư mục nhẹ nhàng:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1D1B20))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(colorOptions) { (hex, title) ->
                            val isColorSelected = newCategoryColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .border(
                                        width = if (isColorSelected) 3.dp else 1.dp,
                                        color = if (isColorSelected) Color(0xFF21005D) else Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .combinedClickable(
                                        onClick = { newCategoryColorHex = hex }
                                    )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.addCategory(newCategoryName, newCategoryColorHex)
                            newCategoryName = ""
                            newCategoryColorHex = "#EADDFF"
                            showAddCategoryDialog = false
                            Toast.makeText(context, "Đã tạo thư mục mới!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEADDFF),
                        contentColor = Color(0xFF21005D)
                    )
                ) {
                    Text("Tạo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddCategoryDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF49454F))
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    // Modal action on long click an Active item
    longPressedTodo?.let { todo ->
        AlertDialog(
            onDismissRequest = { longPressedTodo = null },
            title = { Text("Tùy chọn công việc", color = Color(0xFF21005D), fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có muốn đánh dấu công việc này là \"Không Hoàn Thành\" (Dropped/Failed) hoặc xóa nó không?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markTodoFailed(todo.id)
                        longPressedTodo = null
                        Toast.makeText(context, "Đã chuyển trạng thái Không Hoàn Thành!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828), contentColor = Color.White)
                ) {
                    Text("Không Hoàn Thành", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            viewModel.deleteTodo(todo.id)
                            longPressedTodo = null
                            Toast.makeText(context, "Đã xóa công việc!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF49454F))
                    ) {
                        Text("Xóa hẳn", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { longPressedTodo = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF21005D))
                    ) {
                        Text("Hủy", fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItemRow(
    todo: TodoItem,
    folderName: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = todo.status == TodoStatus.COMPLETED
    val isFailed = todo.status == TodoStatus.FAILED

    val cardBg = when {
        isCompleted -> Color(0xFFF3EDF7).copy(alpha = 0.5f)
        isFailed -> Color(0xFFFFF5F5)
        else -> Color.White
    }

    val cardBorderColor = when {
        isCompleted -> Color.Transparent
        isFailed -> Color(0xFFFFF1F1)
        else -> Color(0xFFF3EDF7)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .alpha(if (isCompleted || isFailed) 0.65f else 1.0f),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted || isFailed) 0.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant Status Tick/Cross Icon Indicator on the left
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = when {
                            isCompleted -> Color(0xFF146C2E)
                            isFailed -> Color(0xFFC62828)
                            else -> Color.White
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = when {
                            isCompleted -> Color(0xFF146C2E)
                            isFailed -> Color(0xFFC62828)
                            else -> Color(0xFF146C2E)
                        },
                        shape = CircleShape
                    )
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed tick",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (isFailed) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Failed mark",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Main texts
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        fontWeight = if (isCompleted || isFailed) FontWeight.Normal else FontWeight.Medium,
                        fontStyle = if (isCompleted) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                    ),
                    color = when {
                        isCompleted -> Color(0xFF49454F)
                        isFailed -> Color(0xFFC62828)
                        else -> Color(0xFF1D1B20)
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Custom folder color preview circle
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(backgroundColor)
                            .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = folderName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF49454F),
                        fontWeight = FontWeight.Medium
                    )
                    if (isFailed) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• Không hoàn thành",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }

            // Quick Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa công việc",
                    tint = Color(0xFF49454F).copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Convenient extension helpers to style pastel colors and borders elegantly in Compose
fun String.toColorOrFallback(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color(0xFFEADDFF)
    }
}

fun Color.darken(factor: Float): Color {
    return Color(
        red = this.red * (1f - factor),
        green = this.green * (1f - factor),
        blue = this.blue * (1f - factor),
        alpha = this.alpha
    )
}
