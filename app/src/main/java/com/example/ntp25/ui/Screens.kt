package com.example.ntp25.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.ntp25.R

@Composable
fun HomeScreen() {
    var catFacts by remember {
        mutableStateOf(
            listOf(
                "Kediler günde 12-16 saat uyuyabilir.",
                "Bir kedi ortalama 48 km/s hıza ulaşabilir.",
                "Kedilerin burun izleri insanların parmak izleri gibidir, benzersizdir.",
                "Kediler mırlayarak hem kendilerini hem de insanları sakinleştirir.",
                "Bir kedi 200'den fazla farklı sesi çıkarabilir.",
                "Kedilerin kulakları 180 derece dönebilir.",
                "Kediler ultrasonik sesleri duyabilir.",
                "Bir kedinin kalbi dakikada 110-140 kez atar.",
                "Kediler rüya görür ve REM uykusu yaşar.",
                "Kedilerin 32 kasla kontrol edilen kulakları vardır."
            )
        )
    }

    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    )

    var draggedItem by remember { mutableIntStateOf(-1) }
    var targetItem by remember { mutableIntStateOf(-1) }
    var isDragActive by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "🐱 Kedi Bilgileri (Sürüklenebilir)",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "💡 Kartları sürükleyerek yeniden sıralayabilirsiniz",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sürükleme durumu göstergesi
        if (isDragActive) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🔄 Sürükleme aktif - Bırakmak için parmağınızı kaldırın",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Kare kartları için grid yapısı
        for (i in catFacts.indices step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sol kart
                DraggableCard(
                    fact = catFacts[i],
                    index = i,
                    isDragged = draggedItem == i,
                    isTarget = targetItem == i,
                    backgroundColor = colors[i % colors.size],
                    onDragStart = {
                        draggedItem = it
                        isDragActive = true
                    },
                    onDragEnd = { from, to ->
                        if (from != to && to >= 0 && to < catFacts.size) {
                            val newList = catFacts.toMutableList()
                            val item = newList.removeAt(from)
                            newList.add(to, item)
                            catFacts = newList
                        }
                        draggedItem = -1
                        targetItem = -1
                        isDragActive = false
                    },
                    onDragOver = { targetItem = it },
                    modifier = Modifier.weight(1f)
                )

                // Sağ kart (eğer varsa)
                if (i + 1 < catFacts.size) {
                    DraggableCard(
                        fact = catFacts[i + 1],
                        index = i + 1,
                        isDragged = draggedItem == i + 1,
                        isTarget = targetItem == i + 1,
                        backgroundColor = colors[(i + 1) % colors.size],
                        onDragStart = {
                            draggedItem = it
                            isDragActive = true
                        },
                        onDragEnd = { from, to ->
                            if (from != to && to >= 0 && to < catFacts.size) {
                                val newList = catFacts.toMutableList()
                                val item = newList.removeAt(from)
                                newList.add(to, item)
                                catFacts = newList
                            }
                            draggedItem = -1
                            targetItem = -1
                            isDragActive = false
                        },
                        onDragOver = { targetItem = it },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    // Boş alan için spacer
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Alt boşluk
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun DraggableCard(
    fact: String,
    index: Int,
    isDragged: Boolean,
    isTarget: Boolean,
    backgroundColor: Color,
    onDragStart: (Int) -> Unit,
    onDragEnd: (Int, Int) -> Unit,
    onDragOver: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetY by remember { mutableFloatStateOf(0f) }
    val haptic = LocalHapticFeedback.current

    val elevation by animateDpAsState(
        targetValue = if (isDragged) 12.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isDragged) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isDragged) 0.85f else 1f,
        animationSpec = tween(300),
        label = "alpha"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isDragged) 2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .aspectRatio(1f) // Kare yapmak için
            .graphicsLayer {
                translationY = offsetY
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                rotationZ = rotation
            }
            .zIndex(if (isDragged) 10f else 0f)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(index) {
                detectDragGestures(
                    onDragStart = { offset: Offset ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDragStart(index)
                    },
                    onDragEnd = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val targetIndex = when {
                            offsetY > 80 -> (index + 2).coerceAtMost(9)
                            offsetY < -80 -> (index - 2).coerceAtLeast(0)
                            else -> index
                        }
                        onDragEnd(index, targetIndex)
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount: Offset ->
                        change.consume()
                        offsetY += dragAmount.y

                        val targetIndex = when {
                            offsetY > 80 -> (index + 2).coerceAtMost(9)
                            offsetY < -80 -> (index - 2).coerceAtLeast(0)
                            else -> index
                        }
                        onDragOver(targetIndex)
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isTarget && !isDragged) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            } else {
                backgroundColor
            }
        ),
        border = if (isTarget && !isDragged) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else if (isDragged) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        } else null,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Üst kısım - Sürükleme ikonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Sürükle",
                    tint = if (isDragged) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(if (isDragged) 24.dp else 20.dp)
                )
            }

            // Orta kısım - Metin
            Text(
                text = fact,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDragged) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(Alignment.CenterVertically)
            )

            // Alt kısım - Kedi emojisi
            Text(
                text = "🐱",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.member_image),
            contentDescription = "kullanıcı",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
        )
    }
}