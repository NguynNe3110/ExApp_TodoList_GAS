package com.example.feature.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthSyncScreen(
    viewModel: AuthViewModel,
    onNavigateBack: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showAdvanced by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đồng bộ đám mây", fontWeight = FontWeight.Normal, fontSize = 22.sp, letterSpacing = (-0.5).sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigateBack(state.loggedInUser) },
                        modifier = Modifier.testTag("auth_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở lại", tint = Color(0xFF1D1B20))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFEF7FF),
                    titleContentColor = Color(0xFF1D1B20)
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFEF7FF))
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // General Information Card styled precisely
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEADDFF)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFF21005D),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Ứng dụng hoạt động offline mặc định. Hãy điền tài khoản để đồng bộ thư mục & việc cần làm trên mọi thiết bị thông qua Firestore đám mây!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF21005D),
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form container Styled elegantly with clean minimalist outlines
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "TÀI KHOẢN ĐỒNG BỘ",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        ),
                        color = Color(0xFF49454F)
                    )

                    // Username Input
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = { viewModel.updateUsername(it) },
                        label = { Text("Tên tài khoản (username)", color = Color(0xFF49454F)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF49454F)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_username_field"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF21005D),
                            unfocusedBorderColor = Color(0xFFF3EDF7)
                        )
                    )

                    // Password Input
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { Text("Mật khẩu", color = Color(0xFF49454F)) },
                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF49454F)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_field"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF21005D),
                            unfocusedBorderColor = Color(0xFFF3EDF7)
                        )
                    )

                    // Advanced Toggle Button
                    TextButton(
                        onClick = { showAdvanced = !showAdvanced },
                        modifier = Modifier.align(Alignment.Start),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF21005D))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Advanced Settings",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAdvanced) "Ẩn cấu hình Firestore nâng cao" else "Hiển thị cấu hình Firestore nâng cao", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Advanced Project ID Input
                    AnimatedVisibility(visible = showAdvanced) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = state.projectId,
                                onValueChange = { viewModel.updateProjectId(it) },
                                label = { Text("Google Cloud Project ID", color = Color(0xFF49454F)) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_project_id_field"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF21005D),
                                    unfocusedBorderColor = Color(0xFFF3EDF7)
                                )
                            )

                            OutlinedTextField(
                                value = state.apiKey,
                                onValueChange = { viewModel.updateApiKey(it) },
                                label = { Text("Firebase Web API Key (Khóa API Web)", color = Color(0xFF49454F)) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_api_key_field"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF21005D),
                                    unfocusedBorderColor = Color(0xFFF3EDF7)
                                )
                            )

                            Text(
                                text = "Lưu ý: Mặc định sử dụng demo database. Nếu sử dụng Project ID riêng, bạn cần điền Khóa API Web (lấy trong Firebase Console -> Cài đặt dự án -> Khóa API web) để vượt qua lỗi xác thực 403.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF49454F),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Operational Feedback (Loading, Success, Error)
            when (val screenState = state.screenState) {
                is AuthScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .testTag("auth_progress_indicator"),
                        color = Color(0xFF21005D)
                    )
                }
                is AuthScreenState.Success -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FFF5)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .border(1.dp, Color(0xFFEADDFF), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = "Success", tint = Color(0xFF146C2E))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Đồng bộ hoàn tất!", fontWeight = FontWeight.Bold, color = Color(0xFF146C2E))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(screenState.message, fontSize = 13.sp, color = Color(0xFF146C2E))
                        }
                    }
                }
                is AuthScreenState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .border(1.dp, Color(0xFFFFF1F1), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFC62828))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Lỗi xảy ra", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(screenState.message, fontSize = 13.sp, color = Color(0xFFC62828))
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Action: Restore (tải dữ liệu từ cloud về đè local)
                Button(
                    onClick = { viewModel.restoreCloud() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEADDFF),
                        contentColor = Color(0xFF21005D)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_restore_button"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Tải Về & Khôi Phục (Restore)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Action: Backup (đưa dữ liệu local lên cloud)
                Button(
                    onClick = { viewModel.backupCloud() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF21005D),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_backup_button"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Đồng Bộ & Lưu Lên Cloud (Backup)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF3EDF7))

                // Action: Register (tạo tài khoản mới)
                OutlinedButton(
                    onClick = { viewModel.registerAccount() },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_register_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF21005D)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF79747E))
                    )
                ) {
                    Text("Đăng ký tài khoản đồng bộ mới (Register)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
