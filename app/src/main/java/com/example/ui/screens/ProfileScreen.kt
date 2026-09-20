package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.AccountStatus
import com.example.models.UserProfile
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onEditProfile: (String, String, String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDailyBonus: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenFaq: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onLogout: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavySlate)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(TealGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.fullName.take(2).uppercase(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile.fullName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = userProfile.phoneNumber,
                    fontSize = 13.sp,
                    color = Slate300
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = GoldEarnings.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Status: Demo Mode",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showEditDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Slate400)
                    ),
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Edit Profile", fontSize = 12.sp)
                }
            }
        }

        // Quick Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(width = 0.8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileMetaRow(label = "Email Address", value = userProfile.email)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)
                ProfileMetaRow(label = "Referral Code", value = userProfile.referralCode)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)
                ProfileMetaRow(label = "Member Since", value = userProfile.joinedDate)
            }
        }

        // Navigation Menu List
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(width = 0.8.dp)
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "App Settings",
                    subtitle = "Data limits, themes, Wi-Fi only mode",
                    onClick = onOpenSettings,
                    tag = "menu_settings"
                )
                HorizontalDivider(color = Slate200)
                ProfileMenuItem(
                    icon = Icons.Outlined.CardGiftcard,
                    title = "Daily Bonus Streak",
                    subtitle = "Check in for 7-day rewards",
                    onClick = onOpenDailyBonus,
                    tag = "menu_bonus"
                )
                HorizontalDivider(color = Slate200)
                ProfileMenuItem(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "Achievements",
                    subtitle = "Track milestones and badges",
                    onClick = onOpenAchievements,
                    tag = "menu_achievements"
                )
                HorizontalDivider(color = Slate200)
                ProfileMenuItem(
                    icon = Icons.Outlined.HelpOutline,
                    title = "FAQ & Knowledge Base",
                    subtitle = "Learn how DataCash PK calculates MB",
                    onClick = onOpenFaq,
                    tag = "menu_faq"
                )
                HorizontalDivider(color = Slate200)
                ProfileMenuItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "Data handling & Zero traffic routing",
                    onClick = onOpenPrivacy,
                    tag = "menu_privacy"
                )
                HorizontalDivider(color = Slate200)
                ProfileMenuItem(
                    icon = Icons.Outlined.Description,
                    title = "Terms of Service",
                    subtitle = "Platform rules & conditions",
                    onClick = onOpenTerms,
                    tag = "menu_terms"
                )
            }
        }

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("logout_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed.copy(alpha = 0.12f),
                contentColor = ErrorRed
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Log Out", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (showEditDialog) {
        var name by remember { mutableStateOf(userProfile.fullName) }
        var phone by remember { mutableStateOf(userProfile.phoneNumber) }
        var email by remember { mutableStateOf(userProfile.email) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEditProfile(name, phone, email)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Slate700)
                }
            }
        )
    }
}

@Composable
fun ProfileMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Slate400)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TealGreen,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Slate400
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Slate400,
            modifier = Modifier.size(18.dp)
        )
    }
}
