package org.example.project.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.AppColors

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search decks..."
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
        placeholder = { Text(placeholder, color = AppColors.TextMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppColors.TextMuted) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.WarmBrown,
            unfocusedContainerColor = AppColors.WarmBrown,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = AppColors.Amber,
            focusedTextColor = AppColors.TextPrimary,
            unfocusedTextColor = AppColors.TextPrimary
        )
    )
}

@Composable
fun ChipRow(
    items: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selected
            val bg by animateColorAsState(
                if (isSelected) AppColors.Amber else AppColors.WarmBrown,
                animationSpec = tween(300), label = "chipBg"
            )
            val textColor by animateColorAsState(
                if (isSelected) AppColors.DarkChocolate else AppColors.TextSecondary,
                animationSpec = tween(300), label = "chipText"
            )
            val scale by animateFloatAsState(
                if (isSelected) 1.05f else 1f,
                animationSpec = tween(200), label = "chipScale"
            )
            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bg)
                    .clickable { onSelected(item) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(item, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun StatsCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accentColor: Color = AppColors.Amber
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.CardSurface)
            .padding(16.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = AppColors.TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = AppColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (action != null) {
            Text(
                action,
                color = AppColors.Amber,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onAction?.invoke() }
            )
        }
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded.value) 180f else 0f,
        animationSpec = tween(300),
        label = "chevron"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.CardSurface)
                .clickable { expanded.value = !expanded.value }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded.value) "Collapse" else "Expand",
                tint = AppColors.Amber,
                modifier = Modifier.size(22.dp).graphicsLayer { rotationZ = rotationAngle }
            )
        }
        androidx.compose.animation.AnimatedVisibility(visible = expanded.value) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val alpha = if (enabled) 1f else 0.5f
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(AppColors.DeepRed, AppColors.BurntOrange, AppColors.Amber)
                ).let { it }
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = Color.White.copy(alpha = alpha),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.WarmBrown)
            .padding(3.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            val bg by animateColorAsState(
                if (isSelected) AppColors.CardSurface else Color.Transparent,
                animationSpec = tween(250), label = "segBg"
            )
            val textColor by animateColorAsState(
                if (isSelected) AppColors.Amber else AppColors.TextMuted,
                animationSpec = tween(250), label = "segText"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bg)
                    .clickable { onSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(item, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.WarmBrown),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AppColors.Amber, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = AppColors.TextPrimary, fontSize = 15.sp)
            if (subtitle != null) {
                Text(subtitle, color = AppColors.TextMuted, fontSize = 12.sp)
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun WarmSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = AppColors.Amber,
            checkedTrackColor = AppColors.CardSurface,
            uncheckedThumbColor = AppColors.TextMuted,
            uncheckedTrackColor = AppColors.WarmBrown,
            uncheckedBorderColor = AppColors.CardSurface
        )
    )
}

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Int = 6,
    trackColor: Color = AppColors.WarmBrown,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(800), label = "progress"
    )
    val displayProgress = if (animated) animatedProgress else progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape((height / 2).dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(displayProgress)
                .height(height.dp)
                .clip(RoundedCornerShape((height / 2).dp))
                .background(Brush.horizontalGradient(listOf(AppColors.DeepRed, AppColors.Amber)))
        )
    }
}

@Composable
fun Badge(text: String, modifier: Modifier = Modifier) {
    val color = when (text) {
        "New" -> AppColors.Success
        "Popular" -> AppColors.Amber
        "Hard" -> AppColors.DeepRed
        "AI" -> AppColors.BurntOrange
        else -> AppColors.TextMuted
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    size: Int = 48,
    name: String = "",
    imageBytes: ByteArray? = null,
    onClick: (() -> Unit)? = null
) {
    val bitmap = androidx.compose.runtime.remember(imageBytes) {
        imageBytes?.let { org.example.project.platform.decodeImageBytes(it) }
    }
    if (bitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = bitmap,
            contentDescription = "Profile",
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .let { if (onClick != null) it.clickable { onClick() } else it },
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
    } else {
        val initials = name.take(1).uppercase().ifEmpty { "?" }
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(AppColors.DeepRed, AppColors.BurntOrange, AppColors.Amber))
                )
                .let { if (onClick != null) it.clickable { onClick() } else it },
            contentAlignment = Alignment.Center
        ) {
            Text(
                initials,
                color = Color.White,
                fontSize = (size / 3).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
