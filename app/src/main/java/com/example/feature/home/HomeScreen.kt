package com.example.feature.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    var showSearchField by remember { mutableStateOf(false) }
    var showAddTodoDialog by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    var selectedTaskCategoryId by remember { mutableStateOf<String?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryColorHex by remember { mutableStateOf("#E6FFFF") }
    var longPressedTodo by remember { mutableStateOf<TodoItem?>(null) }
    var longPressedCategory by remember { mutableStateOf<Category?>(null) }
    var categoryRenameText by remember { mutableStateOf("") }

    val colorOptions = listOf(
        "#EADDFF" to "Linh Lan Sáng (Tím)",
        "#E6FFFF" to "Xanh Dương Sáng",
        "#F9FFF5" to "Xanh Lá Nhạt",
        "#FFFDF0" to "Vàng Kem",
        "#FFF5F5" to "Đỏ San Hô Nhạt"
    )

    val activeTodosCount = state.todos.count { it.status == TodoStatus.ACTIVE }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(showSearchField) {
        if (showSearchField) {
            searchFocusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        floatingActionButton = {
            Button(
                onClick = {
                    selectedTaskCategoryId = state.selectedCategoryId ?: state.categories.firstOrNull()?.id
                    showAddTodoDialog = true
                },
                shape = CircleShape,
                modifier = Modifier.size(64.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm công việc", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (activeTodosCount > 0) "$activeTodosCount công việc chưa hoàn thành" else "0 công việc còn lại",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                    if (registeredUser != null) {
                        Text(
                            text = "Tài khoản đồng bộ: @$registeredUser",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            showSearchField = !showSearchField
                            if (!showSearchField) {
                                searchByTitle = ""
                                keyboardController?.hide()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .testTag("search_toggle_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Tìm kiếm công việc", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onNavigateToSync,
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .testTag("cloud_sync_nav_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Cloud Backup Synchronization", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            AnimatedVisibility(
                visible = showSearchField,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = searchByTitle,
                    onValueChange = { searchByTitle = it },
                    placeholder = { Text("Tìm kiếm công việc...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingIcon = {
                        if (searchByTitle.isNotEmpty()) {
                            IconButton(onClick = { searchByTitle = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .focusRequester(searchFocusRequester)
                        .testTag("search_todo_bar"),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nhấn giữ thư mục để đổi tên hoặc xóa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )

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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(
                        onClick = { showAddCategoryDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
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
                    item {
                        val isSelected = state.selectedCategoryId == null
                        Surface(
                            modifier = Modifier.combinedClickable(onClick = { viewModel.selectCategory(null) }),
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "Tất cả",
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }

                    items(state.categories) { category ->
                        val isSelected = state.selectedCategoryId == category.id
                        Surface(
                            modifier = Modifier.combinedClickable(
                                onClick = { viewModel.selectCategory(category.id) },
                                onLongClick = {
                                    longPressedCategory = category
                                    categoryRenameText = category.name
                                }
                            ),
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = category.name,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredTodos = state.todos.filter { item ->
                val matchesCategory = state.selectedCategoryId == null || item.categoryId == state.selectedCategoryId
                val matchesSearch = searchByTitle.isBlank() || item.title.contains(searchByTitle, ignoreCase = true)
                matchesCategory && matchesSearch
            }

            val activeTodos = filteredTodos.filter { it.status == TodoStatus.ACTIVE }
            val completedOrFailedTodos = filteredTodos.filter { it.status != TodoStatus.ACTIVE }

            if (filteredTodos.isEmpty()) {
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
                            tint = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Thư mục trống",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Chưa có việc vặt hay công việc học tập nào ở đây. Nhập một việc mới ở phía dưới nhé!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    if (activeTodos.isNotEmpty()) {
                        item {
                            Text(
                                text = "CẦN THỰC HIỆN (${activeTodos.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }

                        items(activeTodos, key = { it.id }) { todo ->
                            val folder = state.categories.find { it.id == todo.categoryId }
                            val baseColor = folder?.colorHex?.toColorOrFallback() ?: MaterialTheme.colorScheme.primaryContainer

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

                    if (activeTodos.isNotEmpty() && completedOrFailedTodos.isNotEmpty()) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    if (completedOrFailedTodos.isNotEmpty()) {
                        item {
                            Text(
                                text = "ĐÃ HOÀN THÀNH (${completedOrFailedTodos.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }

                        items(completedOrFailedTodos, key = { it.id }) { todo ->
                            val folder = state.categories.find { it.id == todo.categoryId }
                            val baseColor = if (todo.status == TodoStatus.FAILED) {
                                Color(0xFFFFF1F1)
                            } else {
                                folder?.colorHex?.toColorOrFallback() ?: MaterialTheme.colorScheme.primaryContainer
                            }

                            TodoItemRow(
                                todo = todo,
                                folderName = folder?.name ?: "Tất cả",
                                backgroundColor = baseColor,
                                onClick = { viewModel.toggleTodoCompleted(todo.id) },
                                onLongClick = { viewModel.toggleTodoCompleted(todo.id) },
                                onDelete = { viewModel.deleteTodo(todo.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddTodoDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddTodoDialog = false
                newTaskText = ""
                selectedTaskCategoryId = null
            },
            title = { Text("Thêm công việc mới", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        placeholder = { Text("Bạn cần làm gì / học gì...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        minLines = 2,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Chọn thư mục", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.categories, key = { it.id }) { category ->
                            val isSelected = selectedTaskCategoryId == category.id
                            Surface(
                                modifier = Modifier.combinedClickable(onClick = { selectedTaskCategoryId = category.id }),
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline)
                            ) {
                                Text(
                                    text = category.name,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskText.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập tên công việc!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val selectedCatId = selectedTaskCategoryId ?: state.categories.firstOrNull()?.id ?: "1"
                        viewModel.addTodo(newTaskText, selectedCatId)
                        newTaskText = ""
                        selectedTaskCategoryId = null
                        showAddTodoDialog = false
                        Toast.makeText(context, "Đã thêm công việc thành công!", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("Thêm") }
            },
            dismissButton = {
                TextButton(onClick = { showAddTodoDialog = false }) { Text("Hủy") }
            }
        )
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Tạo Thư Mục Mới", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold) },
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
                    Text("Chọn màu sắc thư mục nhẹ nhàng:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(colorOptions) { (hex, _) ->
                            val isColorSelected = newCategoryColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .border(
                                        width = if (isColorSelected) 3.dp else 1.dp,
                                        color = if (isColorSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .combinedClickable(onClick = { newCategoryColorHex = hex })
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Tạo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddCategoryDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    longPressedCategory?.let { category ->
        val canDelete = category.id != "1" && category.id != "2" && category.id != "3" && category.id != "4"
        AlertDialog(
            onDismissRequest = {
                longPressedCategory = null
                categoryRenameText = ""
            },
            title = { Text("Quản lý thư mục", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = categoryRenameText,
                    onValueChange = { categoryRenameText = it },
                    label = { Text("Tên thư mục") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameCategory(category.id, categoryRenameText)
                        longPressedCategory = null
                        categoryRenameText = ""
                        Toast.makeText(context, "Đã đổi tên thư mục!", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("Lưu") }
            },
            dismissButton = {
                Row {
                    if (canDelete) {
                        TextButton(
                            onClick = {
                                viewModel.deleteCategory(category.id)
                                longPressedCategory = null
                                categoryRenameText = ""
                                Toast.makeText(context, "Đã xóa thư mục!", Toast.LENGTH_SHORT).show()
                            }
                        ) { Text("Xóa") }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(onClick = {
                        longPressedCategory = null
                        categoryRenameText = ""
                    }) { Text("Hủy") }
                }
            }
        )
    }

    longPressedTodo?.let { todo ->
        AlertDialog(
            onDismissRequest = { longPressedTodo = null },
            title = { Text("Tùy chọn công việc", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có muốn đánh dấu công việc này là \"Không Hoàn Thành\" (Dropped/Failed) hoặc xóa nó không?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markTodoFailed(todo.id)
                        longPressedTodo = null
                        Toast.makeText(context, "Đã chuyển trạng thái Không Hoàn Thành!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828), contentColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("Không Hoàn Thành", color = MaterialTheme.colorScheme.surface, fontWeight = FontWeight.Bold)
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
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text("Xóa hẳn", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { longPressedTodo = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
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
        isCompleted -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        isFailed -> Color(0xFFFFF5F5)
        else -> MaterialTheme.colorScheme.surface
    }

    val cardBorderColor = when {
        isCompleted -> Color.Transparent
        isFailed -> Color(0xFFFFF1F1)
        else -> MaterialTheme.colorScheme.outlineVariant
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
                            isCompleted -> MaterialTheme.colorScheme.primary
                            isFailed -> Color(0xFFC62828)
                            else -> MaterialTheme.colorScheme.surface
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = when {
                            isCompleted -> MaterialTheme.colorScheme.primary
                            isFailed -> Color(0xFFC62828)
                            else -> MaterialTheme.colorScheme.primary
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
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (isFailed) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Failed mark",
                        tint = MaterialTheme.colorScheme.surface,
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
                        isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant
                        isFailed -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.onBackground
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    if (isFailed) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "â€¢ KhĂ´ng hoĂ n thĂ nh",
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
                    contentDescription = "XĂ³a cĂ´ng viá»‡c",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Convenient extension helpers to style pastel colors and borders elegantly in Compose
@Composable
fun String.toColorOrFallback(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primaryContainer
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
