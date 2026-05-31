package com.example.alususalgebra

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alususalgebra.db.AlUsusRecordEntity
import com.example.ui.theme.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlUsusScreen(
    viewModel: AlUsusViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val historyItems by viewModel.historyItems.collectAsState()

    val timeString = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        sdf.format(Date()) + " (UTC)"
    }

    // Force elegant native RTL layout globally for perfect Arabic typography
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = CosmicDeepSpace,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "الجبر الأصولي • ALGEBRA AL-USUS",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmicTealAccent,
                                    letterSpacing = 0.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Text(
                                text = "هندسة الفضاءات المعرفية والأبعاد الأولية",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = CosmicMutedText
                                )
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = CosmicDeepSpace.copy(alpha = 0.95f),
                        titleContentColor = CosmicWhite
                    ),
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicSlateLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = timeString,
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = CosmicEmerald,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    HorizontalDivider(color = CosmicSlateLight, thickness = 1.dp)
                    NavigationBar(
                        containerColor = CosmicSlate,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeTab == AlUsusTab.PLAYGROUND,
                            onClick = { viewModel.selectTab(AlUsusTab.PLAYGROUND) },
                            modifier = Modifier.testTag("tab_playground"),
                            label = { Text("المختبر الأصولي", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Build, contentDescription = "Calculators") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CosmicDeepSpace,
                                selectedTextColor = CosmicTealAccent,
                                indicatorColor = CosmicTealAccent,
                                unselectedTextColor = CosmicMutedText,
                                unselectedIconColor = CosmicMutedText
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == AlUsusTab.TEXT_TOPOLOGY,
                            onClick = { viewModel.selectTab(AlUsusTab.TEXT_TOPOLOGY) },
                            modifier = Modifier.testTag("tab_topology"),
                            label = { Text("طوبولوجيا النصوص", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Edit, contentDescription = "Text Analysis") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CosmicDeepSpace,
                                selectedTextColor = CosmicTealAccent,
                                indicatorColor = CosmicTealAccent,
                                unselectedTextColor = CosmicMutedText,
                                unselectedIconColor = CosmicMutedText
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == AlUsusTab.PRIMAL_HUNTER,
                            onClick = { viewModel.selectTab(AlUsusTab.PRIMAL_HUNTER) },
                            modifier = Modifier.testTag("tab_hunter"),
                            label = { Text("صائد الأصول", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Primal Hunter") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CosmicDeepSpace,
                                selectedTextColor = CosmicTealAccent,
                                indicatorColor = CosmicTealAccent,
                                unselectedTextColor = CosmicMutedText,
                                unselectedIconColor = CosmicMutedText
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == AlUsusTab.MANIFESTO,
                            onClick = { viewModel.selectTab(AlUsusTab.MANIFESTO) },
                            modifier = Modifier.testTag("tab_manifesto"),
                            label = { Text("البيان الأصولي", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Info, contentDescription = "Manifesto") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CosmicDeepSpace,
                                selectedTextColor = CosmicTealAccent,
                                indicatorColor = CosmicTealAccent,
                                unselectedTextColor = CosmicMutedText,
                                unselectedIconColor = CosmicMutedText
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(CosmicDeepSpace)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                        },
                        label = "tab_transitions"
                    ) { targetState ->
                        when (targetState) {
                            AlUsusTab.PLAYGROUND -> PlaygroundTab(viewModel)
                            AlUsusTab.TEXT_TOPOLOGY -> TextTopologyTab(viewModel)
                            AlUsusTab.PRIMAL_HUNTER -> PrimalHunterTab(viewModel)
                            AlUsusTab.MANIFESTO -> ManifestoTab()
                        }
                    }
                }

                // SQLite record cabinet drawer at bottom
                HistoryRecordsDrawer(
                    historyItems = historyItems,
                    onSelectLoad = { item ->
                        if (item.type == "NUMBER") {
                            viewModel.activeInput.value = item.value
                            viewModel.onActiveInputChange(item.value)
                            viewModel.selectTab(AlUsusTab.PLAYGROUND)
                        } else {
                            viewModel.textToAnalyze.value = item.value
                            viewModel.selectTab(AlUsusTab.TEXT_TOPOLOGY)
                        }
                    },
                    onDelete = { id -> viewModel.deleteCard(id) },
                    onClearAll = { viewModel.clearHistory() }
                )
            }
        }
    }
}

@Composable
fun PlaygroundTab(viewModel: AlUsusViewModel) {
    val activeInput by viewModel.activeInput.collectAsState()
    val factorization by viewModel.factorization.collectAsState()
    val kasrTree by viewModel.kasrTree.collectAsState()
    val topology by viewModel.topology.collectAsState()
    val conjResult by viewModel.conjugationResult.collectAsState()
    val modConjResult by viewModel.modifiedConjugationResult.collectAsState()

    val mInput by viewModel.mInput.collectAsState()
    val nInput by viewModel.nInput.collectAsState()
    val masafaDistance by viewModel.masafaDistance.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "المختبر الرياضي للأعداد النقطية",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicEmerald
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "أدخل أي عدد صحيح موجب n لمعاينته دلالياً وإزاحته فضائياً",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = CosmicMutedText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Start
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = activeInput,
                            onValueChange = { viewModel.onActiveInputChange(it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = CosmicWhite, fontSize = 16.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicTealAccent,
                                unfocusedBorderColor = CosmicSlateLight,
                                cursorColor = CosmicTealAccent
                            ),
                            label = { Text("العدد الصحيح n", color = CosmicMutedText, fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_factor")
                        )

                        Button(
                            onClick = { viewModel.saveActiveToHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicTealAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_active"),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Done, contentDescription = "Save", tint = CosmicDeepSpace)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("أرشفة", color = CosmicDeepSpace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Text(
                        text = "عينات أصولية مشهورة:",
                        color = CosmicMutedText,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 4.dp),
                        textAlign = TextAlign.Start
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val presets = listOf("12", "30", "60", "360", "840", "1260")
                        items(presets) { num ->
                            AssistChip(
                                onClick = { viewModel.onActiveInputChange(num) },
                                label = { Text(num, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CosmicTealAccent) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = CosmicSlateLight),
                                border = null
                            )
                        }
                    }
                }
            }
        }

        if (factorization != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "البصمة الأصولية والمتجه الأولي",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTealAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = factorization!!.vectorString,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicWhite,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        HorizontalDivider(color = CosmicSlateLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "تمثيل محاور الفضاء الأولي 2D Coordinates",
                            style = TextStyle(fontSize = 11.sp, color = CosmicMutedText),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        PrimalCoordinatesGraph(vector = factorization!!)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "العمليات والمؤشرات الأصولية",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CosmicWhite),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            title = "النبض (Nabad)",
                            value = factorization?.vectorString?.take(18) ?: "0",
                            description = "بصمة الأسوس الأولية",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "الوهج (Wahaj)",
                            value = AlUsusBigCore.computeWahajBig(factorization!!.n).toString(),
                            description = "المسافة من الواحد L¹",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            title = "القدرة (Qudra)",
                            value = AlUsusBigCore.computeQudraBig(factorization!!.n).toString(),
                            description = "حاصل ضرب الأسوس (التعقيد)",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "المعول (Mi'wal)",
                            value = String.format(Locale.US, "%.3f", AlUsusBigCore.computeMiwalBig(factorization!!.n)),
                            description = "شدة الإشعاع العكسي",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            title = "العمد (A'mad)",
                            value = AlUsusBigCore.computeAmadBig(factorization!!.n).toString(),
                            description = "ترتيب أكبر محور أولي",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "الجذر (Kernel)",
                            value = AlUsusBigCore.computeGidrBig(factorization!!.n).toString(),
                            description = "مجموع الأنوية المتفردة",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "سبحة الأعداد السلسية (Al-Sabaha Chain)",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTealAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "سلسلة إزالة العوامل الأولية الأصغر بالتكرار الكامل:",
                            style = TextStyle(fontSize = 11.sp, color = CosmicMutedText)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val sabaha = AlUsusBigCore.computeSabahaBig(factorization!!.n)
                        if (sabaha.isEmpty()) {
                            Text("العدد أولي أو يساوي ١، طول السبحة = صفر", color = CosmicMutedText, fontSize = 12.sp)
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                sabaha.forEachIndexed { idx, pair ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CosmicSlateLight)
                                            .border(1.dp, CosmicTealAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "p_${AlUsusBigCore.getPrimeIndexBig(pair.first)} = ${pair.first}",
                                                color = CosmicWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "الأس: ${pair.second}",
                                                color = CosmicEmerald,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    if (idx < sabaha.size - 1) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "next",
                                            tint = CosmicTealAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "العكس الأصولي ونظرية المسارات",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTealAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("العكس الأصولي المعزّز n*", color = CosmicWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("صعود صارم تضاعفي ينفجر", color = CosmicMutedText, fontSize = 10.sp)
                                Text(
                                    text = conjResult,
                                    color = if (conjResult.contains("💥")) CosmicAmber else CosmicTealAccent,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("العكس المُزاح المستقر n°", color = CosmicWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("إزاحة دورية تنهار تدريجياً لـ ١", color = CosmicMutedText, fontSize = 10.sp)
                                Text(
                                    text = modConjResult,
                                    color = CosmicEmerald,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "أداة قياس المسافة الأصولية (Hamming Metric)",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "d_E(m, n) = Wahaj(m) + Wahaj(n) - 2 × Wahaj(gcd(m, n))",
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CosmicMutedText,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = mInput,
                            onValueChange = { viewModel.onMasafaInputChange(true, it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = CosmicWhite, fontSize = 14.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicEmerald,
                                unfocusedBorderColor = CosmicSlateLight
                            ),
                            label = { Text("العدد الأول m", color = CosmicMutedText, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = nInput,
                            onValueChange = { viewModel.onMasafaInputChange(false, it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = CosmicWhite, fontSize = 14.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicEmerald,
                                unfocusedBorderColor = CosmicSlateLight
                            ),
                            label = { Text("العدد الثاني n", color = CosmicMutedText, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (masafaDistance != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicSlateLight)
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "المسافة الأصولية d_E بين العددين تساوي:",
                                    color = CosmicMutedText,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = masafaDistance.toString(),
                                    color = CosmicTealAccent,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (masafaDistance == 2 && mInput.toBigIntegerOrNull() != null && nInput.toBigIntegerOrNull() != null && mInput.toBigInteger().isProbablePrime(20) && nInput.toBigInteger().isProbablePrime(20)) {
                                        "ملاحظة هندسية: كلاهما أوليان، وتباعدهما ثابت ومقداره دائماً = ٢"
                                    } else {
                                        "مقياس التباعد البنيوي بالفضاء لا نهائي الأبعاد"
                                    },
                                    color = CosmicEmerald,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CosmicSlate),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, color = CosmicTealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = CosmicWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(2.dp))
            Text(description, color = CosmicMutedText, fontSize = 9.sp)
        }
    }
}

@Composable
fun PrimalCoordinatesGraph(vector: AlUsusVectorBig) {
    val factors = vector.factors
    val maxIndex = if (factors.isEmpty()) 4 else (factors.maxOfOrNull { it.primeIndex } ?: 4).coerceAtLeast(4)
    val map = factors.associate { it.primeIndex to it.exponent }
    
    // Safety for rendering huge numbers:
    if (maxIndex > 200) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(CosmicSlateLight, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "تمثيل الفضاء المعرفي ضخم جداً للرسم البياني", color = CosmicAmber, fontSize = 12.sp)
        }
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(CosmicSlateLight, shape = RoundedCornerShape(8.dp))
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        val width = size.width
        val height = size.height
        val padding = 32.dp.toPx()

        val usableWidth = width - (padding * 2)
        val stepX = usableWidth / (maxIndex - 1).coerceAtLeast(1)

        drawLine(
            color = CosmicMutedText.copy(alpha = 0.35f),
            start = Offset(padding, height),
            end = Offset(width - padding, height),
            strokeWidth = 3f
        )

        for (idx in 1..maxIndex) {
            val exp = map[idx] ?: 0
            val x = padding + (idx - 1) * stepX

            val maxHeightScale = height - 12.dp.toPx()
            val drawHeight = (exp.toFloat() / 4f).coerceAtMost(1f) * maxHeightScale

            drawLine(
                color = if (exp > 0) CosmicTealAccent else CosmicMutedText.copy(alpha = 0.15f),
                start = Offset(x, height),
                end = Offset(x, height - drawHeight),
                strokeWidth = if (exp > 0) 5f else 1.5f,
                cap = StrokeCap.Round
            )

            if (exp > 0) {
                drawCircle(
                    color = CosmicEmerald,
                    radius = 6.dp.toPx(),
                    center = Offset(x, height - drawHeight)
                )
            }
        }
    }
}

@Composable
fun TextTopologyTab(viewModel: AlUsusViewModel) {
    val textToAnalyze by viewModel.textToAnalyze.collectAsState()
    val responseState by viewModel.textResponseState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "التأويل اللغوي عبر طوبولوجيا الدلالة",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicTealAccent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "أدخل أي مقطع نصي لتحليله كمتشعب ومعرفة مؤشرات أرقام بيتي (Betti Complexity) والأصول والترابط البنيوي عبر Gemini",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = CosmicMutedText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Start
                    )

                    OutlinedTextField(
                        value = textToAnalyze,
                        onValueChange = { viewModel.textToAnalyze.value = it },
                        textStyle = TextStyle(color = CosmicWhite, fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicTealAccent,
                            unfocusedBorderColor = CosmicSlateLight,
                            cursorColor = CosmicTealAccent
                        ),
                        placeholder = { Text("أدخل المقطع المعرفي هنا...", color = CosmicMutedText, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp)
                            .testTag("text_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.performTextTopologyAnalysis() },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicEmerald),
                        shape = RoundedCornerShape(8.dp),
                        enabled = responseState !is TextAnalysisState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analyze_button")
                    ) {
                        if (responseState is TextAnalysisState.Loading) {
                            CircularProgressIndicator(color = CosmicDeepSpace, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري قراءة بنية النص طوبولوجياً...", color = CosmicDeepSpace, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Analyze", tint = CosmicDeepSpace)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("التحويل والتحليل الذكي الطوبولوجي", color = CosmicDeepSpace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        when (val state = responseState) {
            is TextAnalysisState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("جاري استخلاص طوبولوجيا المعنى المعرفية عبر الذكاء الاصطناعي الرمزي-الهندسي...", color = CosmicMutedText, fontSize = 12.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(color = CosmicTealAccent, trackColor = CosmicSlate)
                        }
                    }
                }
            }
            is TextAnalysisState.Error -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                        border = BorderStroke(1.dp, CosmicCrimson)
                    ) {
                        Text(
                            text = "فشل التحليل: ${state.message}\nتم تحويل الوضع تلقائياً لحاسبة التقديرات الهندسية المباشرة.",
                            color = CosmicCrimson,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            is TextAnalysisState.Success -> {
                val data = state.result
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("أرقام بيتي الدلالية (Betti Invariant Structure)", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CosmicSlateLight)
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("H₀ الروابط / التكتلات", color = CosmicMutedText, fontSize = 10.sp)
                                        Text(data.betti0.toString(), color = CosmicWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CosmicSlateLight)
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("H₁ فجوات السياق دلالياً", color = CosmicMutedText, fontSize = 10.sp)
                                        Text(data.betti1.toString(), color = CosmicAmber, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("شكل الفضاء المعرفي التفاعلي", color = CosmicMutedText, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            SemanticHomologyCanvas(betti0 = data.betti0, betti1 = data.betti1)

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(data.semanticManifoldDescription, color = CosmicWhite, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }

                item {
                    Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("مصفوفة تحويل الأصول للمفردات", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            data.alUsusTranslation.forEach { mapping ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CosmicSlateLight)
                                        .padding(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "e_${mapping.primeIndex} = ${mapping.prime}^${mapping.exponent}",
                                                color = CosmicEmerald,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = mapping.word,
                                                color = CosmicWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = mapping.meaningExplanation,
                                            color = CosmicMutedText,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(CosmicSlateLight).padding(8.dp)) {
                                    Text("مجموع الوهج: ${data.totalWahaj}", color = CosmicWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(CosmicSlateLight).padding(8.dp)) {
                                    Text("أقصى عمد: ${data.totalAmad}", color = CosmicWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("التقرير العلمي والتأويل التكاملي", color = CosmicEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = data.philosophicalInterpretation,
                                color = CosmicWhite,
                                fontSize = 12.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
            }
            else -> {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("تلميح أصولي طوبولوجي", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "أدخل أي مقطع نصي واضغط حلل لاستخلاص شبكة الترابط الدلالي ووصف المتشعب اللغوي والمعنى الأكاديمي عبر Gemini.",
                                color = CosmicMutedText,
                                fontSize = 11.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SemanticHomologyCanvas(betti0: Int, betti1: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(CosmicSlateLight, shape = RoundedCornerShape(8.dp))
    ) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)

        val clustersCount = betti0.coerceAtLeast(1)
        val radius = 45.dp.toPx()
        val nodePoints = mutableListOf<Offset>()
        for (i in 0 until clustersCount) {
            val angle = (2 * Math.PI * i / clustersCount).toFloat()
            val x = center.x + radius * cos(angle)
            val y = center.y + radius * sin(angle)
            val pt = Offset(x, y)
            nodePoints.add(pt)

            drawCircle(
                color = CosmicTealAccent,
                radius = 7.dp.toPx(),
                center = pt
            )
        }

        for (i in nodePoints.indices) {
            for (j in (i + 1) until nodePoints.size) {
                drawLine(
                    color = CosmicTealAccent.copy(alpha = 0.35f),
                    start = nodePoints[i],
                    end = nodePoints[j],
                    strokeWidth = 2f
                )
            }
        }

        val holesCount = betti1.coerceAtZero()
        if (holesCount > 0) {
            for (h in 1..holesCount) {
                val cycleRadius = 18.dp.toPx() * h
                drawCircle(
                    color = CosmicAmber,
                    radius = cycleRadius,
                    center = center,
                    style = Stroke(
                        width = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
        }
    }
}

@Composable
fun PrimalHunterTab(viewModel: AlUsusViewModel) {
    val primeIndexInput by viewModel.primeIndexInput.collectAsState()
    val calculatedPrimeResult by viewModel.calculatedPrimeResult.collectAsState()

    val eigenvalues by viewModel.eigenvalues.collectAsState()
    val spacings by viewModel.spacings.collectAsState()
    val dftSpectrum by viewModel.dftSpectrum.collectAsState()
    val sValue by viewModel.sParameter.collectAsState()
    val dimValue by viewModel.primeDimension.collectAsState()

    val crypX by viewModel.cryptoXInput.collectAsState()
    val crypY by viewModel.cryptoYInput.collectAsState()
    val crypXExp by viewModel.cryptoXExponents.collectAsState()
    val crypYExp by viewModel.cryptoYExponents.collectAsState()
    val crypXCiph by viewModel.cryptoXCiphertext.collectAsState()
    val crypYCiph by viewModel.cryptoYCiphertext.collectAsState()
    val crypSumCiph by viewModel.cryptoSumCiphertext.collectAsState()
    val decExp by viewModel.decryptedExponents.collectAsState()
    val decNum by viewModel.decryptedNumber.collectAsState()

    val svdK by viewModel.svdKValue.collectAsState()
    val svdSingular by viewModel.svdSingularValues.collectAsState()
    val svdErr by viewModel.svdReconstructionError.collectAsState()

    val generation by viewModel.aksGeneration.collectAsState()
    val grid by viewModel.aksGrid.collectAsState()

    val tropXStr by viewModel.tropicalXInputStr.collectAsState()
    val tropY by viewModel.tropicalYGenerated.collectAsState()
    val tropXSol by viewModel.tropicalXSolved.collectAsState()
    val tropSolv by viewModel.tropicalIsSolvable.collectAsState()
    val tropLog by viewModel.tropicalLog.collectAsState()
    val metricExponents by viewModel.metricTensorExponents.collectAsState()
    val detExponents by viewModel.primalDeterminantExponents.collectAsState()

    var subTab by remember { mutableStateOf(0) } // 0: Sieve/Wahaj, 1: A1 Spectral, 2: Crypto, 3: SVD DNA, 4: Al-Aks
    var limitStr by remember { mutableStateOf("100") }
    var testNumStr by remember { mutableStateOf("109") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle SubTab Header (Horizontal Scrollable row of chips)
        item {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .background(CosmicSlate, shape = RoundedCornerShape(10.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val subTabLabels = listOf(
                    "غربال الوهج والعدد n",
                    "المؤثر وطيف A1",
                    "التشفير التماثلي الكوني",
                    "ضغط الـ DNA و SVD",
                    "ديناميكا العكس الأصولي",
                    "الهندسة الاستوائية والمحدّد"
                )
                subTabLabels.forEachIndexed { index, label ->
                    val isSelected = subTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CosmicTealAccent else CosmicSlateLight)
                            .clickable { subTab = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) CosmicDeepSpace else CosmicWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        if (subTab == 0) {
            // SUB-TAB 0: PRIME GENERATION, SEEKING, AND WAHAJ PRIMALITY PROOF
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "صائد الأصول والبعد n (Primal Hunter)",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTealAccent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = "الأعداد الأولية ليست مجرد أرقام بل هي المحاور الأصولية المحركة للكون. احسب العدد الأولي p_n بناءً على رتبته الإحداثية n",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = CosmicMutedText
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            textAlign = TextAlign.Start
                        )

                        OutlinedTextField(
                            value = primeIndexInput,
                            onValueChange = { viewModel.onPrimeIndexChange(it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = CosmicWhite, fontSize = 16.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicTealAccent,
                                unfocusedBorderColor = CosmicSlateLight,
                                cursorColor = CosmicTealAccent
                            ),
                            label = { Text("أدخل رتبة البعد n", color = CosmicMutedText, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_prime_index")
                        )
                    }
                }
            }

            if (calculatedPrimeResult != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "المحور الأصولي الأولي p_$primeIndexInput هو:",
                                color = CosmicMutedText,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                  text = calculatedPrimeResult.toString(),
                                  color = CosmicEmerald,
                                  fontSize = 44.sp,
                                  fontWeight = FontWeight.Black,
                                  fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CosmicSlateLight)
                                    .padding(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "خصائص ميكانيكية للمحور p_$primeIndexInput:",
                                        color = CosmicTealAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "١. البعد المعياري L⁰ = ١ (نقطة أحادية الأسطوانة).",
                                        color = CosmicWhite,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "٢. الوهج الطبيعي (Wahaj) = ١ (أشد الأعداد إشعاعاً في رتبتها).",
                                        color = CosmicWhite,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "٣. المسافة العكسية للأوليات متباعدة بمقدار ثابت d_E = ٢.",
                                        color = CosmicWhite,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // WAHAJ PRIMALITY PROOF SECTION
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "برهان الوهج للأولية (Wahaj Primality Proof)",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTealAccent
                            )
                        )
                        Text(
                            text = "مبدأ أصولي: الوهج وهو مجموع أسس عوامل التحليل يساوي ١ للعدد الأولي فقط! Wahaj(n) = 1 ⟺ n is prime.",
                            style = TextStyle(fontSize = 11.sp, color = CosmicMutedText),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = testNumStr,
                            onValueChange = { testNumStr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = CosmicWhite, fontSize = 15.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicTealAccent,
                                unfocusedBorderColor = CosmicSlateLight,
                                cursorColor = CosmicTealAccent
                            ),
                            label = { Text("أدخل عدداً للاختبار", color = CosmicMutedText, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        val numToTest = testNumStr.toLongOrNull() ?: 0L
                        if (numToTest > 1L) {
                            val w = AlUsusCore.computeWahaj(numToTest)
                            val isP = w == 1
                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isP) CosmicEmerald.copy(alpha = 0.08f) else CosmicAmber.copy(alpha = 0.08f))
                                    .border(1.dp, if (isP) CosmicEmerald else CosmicAmber, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isP) "العدد $numToTest هو عدد أولي قطعاً 🌟" else "العدد $numToTest هو عدد مركب هندسياً 🔸",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isP) CosmicEmerald else CosmicAmber,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val factorObj = AlUsusCore.factorize(numToTest)
                                    Text(
                                        text = "التحليل الأصولي: ${factorObj.vectorString}",
                                        color = CosmicWhite,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "قيمة الوهج Wahaj($numToTest) = $w",
                                        color = CosmicWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // PRIME SIEVE SECTION (Sieve of Eratosthenes up to limit)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "مولد وغربال الأعداد الأولية (Sieve of Eratosthenes)",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTealAccent
                            )
                        )
                        Text(
                            text = "توليد جميع الأعداد الأولية بكفاءة عالية حتى سقف حد مخصص N.",
                            style = TextStyle(fontSize = 11.sp, color = CosmicMutedText),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = limitStr,
                            onValueChange = { limitStr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = CosmicWhite, fontSize = 15.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicTealAccent,
                                unfocusedBorderColor = CosmicSlateLight,
                                cursorColor = CosmicTealAccent
                            ),
                            label = { Text("الحد الأقصى N", color = CosmicMutedText, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        val limNum = limitStr.toIntOrNull() ?: 100
                        val prList = remember(limNum) {
                            if (limNum in 2..5000) {
                                AlUsusCore.sievePrimes(limNum)
                            } else if (limNum > 5000) {
                                AlUsusCore.sievePrimes(5000) // safety limit
                            } else {
                                emptyList()
                            }
                        }

                        if (prList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("الأوليات المُولدة (${prList.size} عدد):", color = CosmicWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(prList) { p ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CosmicSlateLight)
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = p.toString(),
                                            color = CosmicTealAccent,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                            if (limNum > 5000) {
                                Text("* تم تحديد السقف بـ 5000 لحفظ الأداء بالرسوم والذاكرة.", color = CosmicAmber, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        } else if (subTab == 1) {
            // SUB-TAB 1: MATRIX A1 SPECTRAL CORE ANALYZER
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "المؤثر الطيفي المصحح وجبر المصفوفة A1",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTealAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "نقوم بإسقاط عناصر مصفوفة A1 (بيانات إسقاط أصولي) إلى متجهات أصولية ثابتة البعد ثم تكوين المؤثر الطيفي H وعرض توزيع القيم الذاتية وفواصل GUE و DFT التذبذبي.",
                            style = TextStyle(fontSize = 11.sp, color = CosmicMutedText, lineHeight = 16.sp)
                        )
                    }
                }
            }

            // Interactive Controls for Spectral Analyzer
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("عامل الإزاحة الطيفية (Zeta Param s): ", color = CosmicWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = String.format(Locale.US, "%.3f", sValue),
                                color = CosmicTealAccent,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                        Slider(
                            value = sValue,
                            onValueChange = { viewModel.onSParameterChange(it) },
                            valueRange = -1.5f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = CosmicTealAccent,
                                activeTrackColor = CosmicTealAccent,
                                inactiveTrackColor = CosmicSlateLight
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("أبعاد الإسقاط الأصولي K (توسيع الأعمدة): ", color = CosmicWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(10, 20, 30, 50).forEach { dim ->
                                val isSelected = dimValue == dim
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CosmicTealAccent else CosmicSlateLight)
                                        .clickable { viewModel.onPrimeDimensionChange(dim) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dim بعداً",
                                        color = if (isSelected) Color.White else CosmicWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Eigenvalues Graph Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("١. رصيف الطيف والقيم الذاتية الـ 19 المرتبة (λ)", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("يوضح التدرج الطيفي المتصاعد لمؤثر ريمان المصحح المبني من دمج المتجهات الأصولية.", color = CosmicMutedText, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(CosmicSlateLight, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            if (eigenvalues.isEmpty()) return@Canvas
                            val width = size.width
                            val height = size.height

                            val maxE = eigenvalues.maxOrNull() ?: 1.0
                            val minE = eigenvalues.minOrNull() ?: -1.0
                            val range = (maxE - minE).coerceAtLeast(1e-5)

                            val stepX = width / 18f

                            // grid
                            for (i in 0..4) {
                                val y = height * (i / 4f)
                                drawLine(
                                    color = CosmicMutedText.copy(alpha = 0.15f),
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 1f
                                )
                            }

                            val points = mutableListOf<Offset>()
                            for (idx in 0 until 19) {
                                val x = idx * stepX
                                val y = height - ((eigenvalues[idx] - minE) / range).toFloat() * height
                                val pt = Offset(x, y)
                                points.add(pt)

                                drawCircle(
                                    color = CosmicTealAccent,
                                    radius = 4.dp.toPx(),
                                    center = pt
                                )
                            }

                            for (idx in 0 until 18) {
                                drawLine(
                                    color = CosmicTealAccent,
                                    start = points[idx],
                                    end = points[idx + 1],
                                    strokeWidth = 2.5f
                                )
                            }
                        }
                    }
                }
            }

            // Spacings Card vs GUE repulsion
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("٢. فواصل الطيف والتوزيع الإحصائي (Δλ)", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("توزيع التباينات والفواصل يشير إلى ظاهرة التنافر الطيفي الشهيرة لـ GUE المماثلة لأصفار دالة لـ ζ.", color = CosmicMutedText, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .background(CosmicSlateLight, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            if (spacings.isEmpty()) return@Canvas
                            val width = size.width
                            val height = size.height

                            val maxS = spacings.maxOrNull() ?: 1.0
                            val stepX = width / 17f

                            for (idx in 0 until 18) {
                                val x = idx * stepX + stepX * 0.3f
                                val barHeight = (spacings[idx] / maxS).toFloat() * (height - 10.dp.toPx())

                                drawLine(
                                    color = CosmicEmerald,
                                    start = Offset(x, height),
                                    end = Offset(x, height - barHeight),
                                    strokeWidth = 6.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }
            }

            // Fourier Wave peaks Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("٣. الكثافة الطيفية الطوبولوجية ودورية التذبذب |S(t)|", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("باستخدام تحويـل فورييه وتحليل DFT، نحصل على قمم رنين حقيقية عند مسافات الأعداد الأولية اللوغاريتمية log p.", color = CosmicMutedText, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(CosmicSlateLight, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            if (dftSpectrum.isEmpty()) return@Canvas
                            val width = size.width
                            val height = size.height

                            val maxD = dftSpectrum.maxOrNull() ?: 1.0
                            val stepX = width / 59f

                            val points = mutableListOf<Offset>()
                            for (idx in 0 until 60) {
                                val x = idx * stepX
                                val y = height - (dftSpectrum[idx] / maxD).toFloat() * height
                                points.add(Offset(x, y))
                            }

                            for (idx in 0 until 59) {
                                drawLine(
                                    color = CosmicAmber,
                                    start = points[idx],
                                    end = points[idx + 1],
                                    strokeWidth = 2.5f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }
            }
        } else if (subTab == 2) {
            // Header card
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("التشفير التماثلي الأصولي التام (Homomorphic Primal Crypto)", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "مبدأ الجبر الأصولي: ضرب الأعداد في الفضاء العادي n × m يقابله جمع متجهاتها الأصولية في فضاء الترشيح e_n + e_m. هذا يسمح بإجراء الحسابات على الأرقام وهي مشفرة بالكامل دون فك تشفيرها أولاً!",
                            color = CosmicMutedText, fontSize = 11.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            // Controls card
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("الرسائل والأرقام المدخلة للتشفير (يقبل القسمة على 2, 3, 5, 7, 11, 13):", color = CosmicWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = crypX,
                                onValueChange = { viewModel.onCryptoInputChange(true, it) },
                                label = { Text("الرسالة X", color = CosmicMutedText, fontSize = 12.sp) },
                                textStyle = TextStyle(color = CosmicEmerald, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicTealAccent, unfocusedBorderColor = CosmicSlateLight)
                            )
                            OutlinedTextField(
                                value = crypY,
                                onValueChange = { viewModel.onCryptoInputChange(false, it) },
                                label = { Text("الرسالة Y", color = CosmicMutedText, fontSize = 12.sp) },
                                textStyle = TextStyle(color = CosmicEmerald, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicTealAccent, unfocusedBorderColor = CosmicSlateLight)
                            )
                        }
                    }
                }
            }

            // Exponents display and ciphertexts
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("التمثيل والتحويل إلى الفضاء المتجهي المشفر:", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // X
                            Column(modifier = Modifier.weight(1f).background(CosmicSlateLight, RoundedCornerShape(8.dp)).padding(10.dp)) {
                                Text("رسالة X: $crypX", color = CosmicWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("متجه الأسوس: (${crypXExp.joinToString(", ")})", color = CosmicMutedText, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("الCipher المشفر C_x:", color = CosmicAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("(${crypXCiph.map { String.format(Locale.US, "%.1f", it) }.joinToString(", ")})", color = CosmicAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                            // Y
                            Column(modifier = Modifier.weight(1f).background(CosmicSlateLight, RoundedCornerShape(8.dp)).padding(10.dp)) {
                                Text("رسالة Y: $crypY", color = CosmicWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("متجه الأسوس: (${crypYExp.joinToString(", ")})", color = CosmicMutedText, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("الCipher المشفر C_y:", color = CosmicAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("(${crypYCiph.map { String.format(Locale.US, "%.1f", it) }.joinToString(", ")})", color = CosmicAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }

            // Homomorphic Addition
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("العملية التماثلية الاندماجية (Homomorphic Sum):", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
                        Text("C_sum = C_x + C_y", color = CosmicWhite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 16.sp)

                        Box(modifier = Modifier.fillMaxWidth().background(CosmicSlateLight, RoundedCornerShape(8.dp)).padding(12.dp)) {
                            Text(
                                "(${crypSumCiph.map { String.format(Locale.US, "%.1f", it) }.joinToString(", ")})",
                                color = CosmicEmerald,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Decryption with Equation system solving
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("البرهان والفك الرياضي للمعادلات (Decryption Output):", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())

                        Text("الأسوس المكونة المستخرجة:", color = CosmicMutedText, fontSize = 11.sp)
                        Text("(${decExp.joinToString(", ")})", color = CosmicWhite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 17.sp)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("الرسالة المفكوكة (الناتج الحركي للضرب):", color = CosmicWhite, fontSize = 12.sp)
                        Text(
                            text = if (decNum > 0) decNum.toString() else "خارج منطاق التغطية للأسوس",
                            color = CosmicEmerald,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )

                        val expected = (crypX.toLongOrNull() ?: 1L) * (crypY.toLongOrNull() ?: 1L)
                        val isVerified = decNum == expected

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isVerified) CosmicEmerald.copy(alpha = 0.08f) else CosmicAmber.copy(alpha = 0.08f))
                                .border(1.dp, if (isVerified) CosmicEmerald else CosmicAmber, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (isVerified) {
                                    "✨ برهان الاتساق الرياضي تام! لقد تمكن الكمبيوتر من ضرب الأعداد المشفرة بالكامل ($crypX × $crypY) وحصلنا على الناتج المفكوك $decNum دون كشف العوامل!"
                                } else {
                                    "⚠️ الأعداد غير مقتصرة على القواسم الأولية الستة الأولى، يرجى استخدام أعداد عواملها الأولية مكونة فقط من: 2, 3, 5, 7, 11, 13 لبرهنة التشفير."
                                },
                                color = if (isVerified) CosmicEmerald else CosmicAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        } else if (subTab == 3) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ضغط الـ DNA المصفوفي للأعداد الطوبولوجية (Matrix SVD Compression)", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "نستخدم تحليل القيمة المنفردة (Singular Value Decomposition) لاستخراج نوى الحركة والمكونات الطيفية الستة (DNA) التي تولد الفضاء الأصولي لمصفوفة A1 بالكامل وتضغط خلاياها.",
                            color = CosmicMutedText, fontSize = 11.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            // Slider Control
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("أبعاد نواة الضغط الكهروغناطيسية K:", color = CosmicWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "$svdK من أصل 6 مركبات",
                                color = CosmicTealAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Slider(
                            value = svdK.toFloat(),
                            onValueChange = { viewModel.onSvdKValueChange(it.toInt()) },
                            valueRange = 1f..6f,
                            steps = 4,
                            colors = SliderDefaults.colors(
                                thumbColor = CosmicTealAccent,
                                activeTrackColor = CosmicTealAccent,
                                inactiveTrackColor = CosmicSlateLight
                            )
                        )
                    }
                }
            }

            // Accuracy circular score
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("مؤشرات الاتساق ودقة الضغط للمصفوفة A1:", color = CosmicTealAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("دقة جودة الهيكل الأساسي", color = CosmicMutedText, fontSize = 11.sp)
                                Text(
                                    text = String.format(Locale.US, "%.2f%%", 100.0 - svdErr),
                                    color = CosmicEmerald,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("نسبة تشويه البيانات المفقودة", color = CosmicMutedText, fontSize = 11.sp)
                                Text(
                                    text = String.format(Locale.US, "%.2f%%", svdErr),
                                    color = if (svdErr > 15.0) CosmicAmber else CosmicTealAccent,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { ((100.0 - svdErr) / 100.0).toFloat() },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = CosmicEmerald,
                            trackColor = CosmicSlateLight
                        )
                    }
                }
            }

            // Singular values graph bar
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("القيم الذاتية وجزيئات الـ DNA الستة للمصفوفة (σ):", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("تمثل مستويات الطاقة المتصلة المكتشفة في إسقاط A1 التعددي مرتبة تنازلياً.", color = CosmicMutedText, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            svdSingular.reversed().forEachIndexed { idx, value ->
                                val proportion = (value / (svdSingular.maxOrNull() ?: 1.0)).toFloat()
                                val isKept = idx < svdK
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "مستوى σ_${idx + 1}:",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = if (isKept) CosmicWhite else CosmicMutedText,
                                        modifier = Modifier.width(60.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(14.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isKept) CosmicTealAccent.copy(alpha = 0.2f) else CosmicSlateLight)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(proportion)
                                                .background(if (isKept) CosmicTealAccent else CosmicMutedText.copy(alpha = 0.5f))
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = String.format(Locale.US, "%.3f", value),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = if (isKept) CosmicTealAccent else CosmicMutedText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (subTab == 4) {
            // Header card
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ديناميكا العكس الأصولي للمصفوفة (Al-Aks Matrix Evolution)", color = CosmicTealAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "الأنظمة الديناميكية الحية للجبر الأصولي! كل خلية في مصفوفة A1 تشكل كائناً أعدادياً حياً يتطور عبر الأجيال الزمنية: الأعداد الأولية ثابتة (الثقوب السوداء المستقرة)، الأعداد البسيطة تذبذب وتستقر (النوابض Pulsars)، والأعداد الكومبوزت الضخمة تنفجر وتطلق طاقتها للجيران لتغيير خصائص الفضاء (الهياكل العريضة Supernovas).",
                            color = CosmicMutedText, fontSize = 11.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            // Controls
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("الخط الحركي الفضائي الـ CA:", color = CosmicMutedText, fontSize = 11.sp)
                            Text(
                                text = "الجيل الزمنـي T = $generation",
                                color = CosmicAmber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.resetAksGrid() },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicSlateLight)
                            ) {
                                Text("إعادة ضبط ↺", color = CosmicWhite, fontSize = 12.sp)
                            }
                            Button(
                                onClick = { viewModel.nextGenerationAks() },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicTealAccent)
                            ) {
                                Text("الجيل التالي ➔", color = CosmicDeepSpace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Legend
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(CosmicTealAccent))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ثقب أسود (أولي)", color = CosmicWhite, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(CosmicAmber))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نابض (تغير)", color = CosmicWhite, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(CosmicSlateLight))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الفراغ المترشح (١)", color = CosmicWhite, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Grid card displaying 19 rows and 6 columns of matrix cells!
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("الحالة الهندسية الحركية لخلايا الفضاء A1:", color = CosmicWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

                        grid.forEachIndexed { rIdx, rowValues ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                rowValues.forEachIndexed { cIdx, cellValue ->
                                    val isP = AlUsusCore.isPrime(cellValue)
                                    val isVacuum = cellValue <= 1L
                                    val bgColor = when {
                                        isP -> CosmicTealAccent
                                        isVacuum -> CosmicSlateLight
                                        else -> CosmicAmber
                                    }
                                    val textColor = if (isP) CosmicDeepSpace else CosmicWhite

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(bgColor)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (cellValue > 99999) "💥INF" else cellValue.toString(),
                                            color = textColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (subTab == 5) {
            // Header Explanation Card
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CosmicSlate)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "الهندسة الاستوائية وحل معادلات ديوفانتس المصفوفة الأصولية",
                            color = CosmicTealAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "نحن نربط بين قوانين المسافات الاستوائية (Max-Plus Algebra) والتحليل الموتّري لتفكيك مصفوفة A1 وحل معادلات Diophantine الصعبة على الأبعاد الأولية الستة الأولى (2، 3، 5، 7، 11، 13).",
                            color = CosmicMutedText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Applied Track: Diophantine Tropical Solver Interactive
            item {
                Card(colors = CardColors(containerColor = CosmicSlate, contentColor = CosmicWhite, disabledContainerColor = CosmicSlate, disabledContentColor = CosmicWhite)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🔴 المسار الأول: حل Diophantine المتوازن (A1 ⊗_E X = Y)",
                            color = CosmicAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "أدخل متجه الأسس المجهول X (6 مدخلات تفصلها فاصلة، بحد أقصى 20 لتبسيط التحليل):",
                            color = CosmicWhite,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = tropXStr,
                            onValueChange = { viewModel.onTropicalXInputChange(it) },
                            modifier = Modifier.fillMaxWidth().testTag("tropical_x_input"),
                            textStyle = TextStyle(color = CosmicWhite, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicTealAccent,
                                unfocusedBorderColor = CosmicSlateLight,
                                focusedContainerColor = CosmicSlateLight.copy(alpha = 0.5f),
                                unfocusedContainerColor = CosmicSlateLight.copy(alpha = 0.2f)
                            ),
                            placeholder = { Text("مثال: 2, 3, 1, 5, 2, 1", color = CosmicMutedText) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show generated Y vector (truncated or styled nicely)
                        Text(
                            text = "المتجه المتولد Y (الأحجام الأصولية الفائقة 19×1):",
                            color = CosmicWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CosmicSlateLight, shape = RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Y = [ ${if (tropY.isNotEmpty()) tropY.take(6).joinToString(", ") else "0"} ... ] (19 مدخلات)",
                                color = CosmicTealAccent,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show Reconstructed X vector
                        Text(
                            text = "استخراج المجهول X عبر مبرهنة الحل الأصولي:",
                            color = CosmicWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (tropSolv) CosmicTealAccent.copy(alpha = 0.15f) else CosmicAmber.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "X المحلول = [ ${tropXSol.joinToString(", ")} ]",
                                    color = if (tropSolv) CosmicTealAccent else CosmicAmber,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "تقرير دورة الطاقة وتأكيد الاتساق الطوبولوجي:",
                            color = CosmicMutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CosmicSlateLight, RoundedCornerShape(6.dp))
                                .background(CosmicDeepSpace.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = tropLog,
                                color = CosmicWhite,
                                fontSize = 10.sp,
                                lineHeight = 15.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Analytical Track: Metric Tensor G_A1 & Primal Determinant
            item {
                Card(colors = CardColors(containerColor = CosmicSlate, contentColor = CosmicWhite, disabledContainerColor = CosmicSlate, disabledContentColor = CosmicWhite)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🔵 المسار الثاني: المترية السداسية والمحدّد الأصولي لـ A1",
                            color = CosmicTealAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "يقيس الموتّر المتري G_A1 التشابك الأصولي الكلي في مكرر الـ lcm المتقاطع لأعمدة A1 الستة. المحدد الأصولي يمثل الحجم الفائق (Hyper-volume) الذي لا يتغير.",
                            color = CosmicMutedText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dropdown or list of metric tensor exponents for each prime dimension
                        var selectedPrimeIdxForMetric by remember { mutableStateOf(0) }
                        val primesList = listOf(2, 3, 5, 7, 11, 13)

                        Text(
                            text = "اختر البعد الأولي (p) لعرض أسس موتر المترية (6×6):",
                            color = CosmicWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            primesList.forEachIndexed { idx, p ->
                                val isSel = selectedPrimeIdxForMetric == idx
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) CosmicTealAccent else CosmicSlateLight)
                                        .clickable { selectedPrimeIdxForMetric = idx }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "p=$p",
                                        color = if (isSel) CosmicDeepSpace else CosmicWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show selected 6x6 matrix of exponents
                        Text(
                            text = "أسس موتر المترية G_A1 للعدد p = ${primesList[selectedPrimeIdxForMetric]}:",
                            color = CosmicTealAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CosmicDeepSpace.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            val currentMatrix = if (metricExponents.size > selectedPrimeIdxForMetric) metricExponents[selectedPrimeIdxForMetric] else Array(6) { IntArray(6) }
                            for (r in 0 until 6) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    for (c in 0 until 6) {
                                        val valExp = if (currentMatrix.size > r && currentMatrix[r].size > c) currentMatrix[r][c] else 0
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(CosmicSlateLight.copy(alpha = 0.6f), RoundedCornerShape(3.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = valExp.toString(),
                                                color = CosmicWhite,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Prime Signature visualization
                        Text(
                            text = "التوقيع الأولي ومحدد الصلابة الأصولي det_E(G_A1):",
                            color = CosmicAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "يقاس الحجم الفائق عبر lcm على كل التباديل الـ 720 لـ S6. بما أن مصفوفة A1 صلبة ومستقلة، فإن المحدد الكوني له أسس ضخمة تبرهن استقلالية الأعمدة:",
                            color = CosmicMutedText,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Display Signature exponents
                        val sigPrimes = listOf(2, 3, 5, 7, 11, 13)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CosmicSlateLight.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "التوقيع الأولي (الأسس الأولية للصلابة):",
                                    color = CosmicWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            sigPrimes.forEachIndexed { pIdx, p ->
                                val exp = if (detExponents.size > pIdx) detExponents[pIdx] else 0
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "البُعد الأولي p = $p", color = CosmicWhite, fontSize = 10.sp)
                                        Text(text = "الأس α_$p = $exp", color = CosmicTealAccent, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    // Mini Progress bar
                                    val progress = (exp.toFloat() / 500f).coerceIn(0.1f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .background(CosmicDeepSpace, RoundedCornerShape(2.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progress)
                                                .fillMaxHeight()
                                                .background(CosmicTealAccent, RoundedCornerShape(2.dp))
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManifestoTab() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSlate),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "البيان العلمي والمشروع التأسيسي",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTealAccent
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "نحن لا نرى العدد كخط مسطح، بل كمتجه في فضاء لا نهائي المحاور. كل عدد أولي هو رتبة إحداثية، وكل بعد يمتد إلى اللانهاية.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = CosmicWhite,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Justify
                    )
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSlate),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Axioms & Operations of Al-Usus",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTealAccent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                ManifestoRow(title = "① الأصول (Al-Usus Space)", detail = "العدد الأولي هو المحور eₚ والعدد الصحيح هو متجه فريد.")
                ManifestoRow(title = "② الرقم المقدّر (Qudra)", detail = "حاصل ضرب أسوس التحليل ليعبر عن مستوى التشتت بالأبعاد.")
                ManifestoRow(title = "③ العمد (A'mad)", detail = "عمق أكبر محور أولي مُعمر، وهو يقابل رتبته الإحداثية.")
                ManifestoRow(title = "④ المسافة (Al-Masafa)", detail = "معيار L¹ بالفضاء المتجهي للأعداد وهو حقيقي تماماً.")
                ManifestoRow(title = "⑤ المدارات والأعكاس", detail = "تدرس تكرارية التموضع وتصف المتواليات الهابطة والصاعدة.")
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSlate),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "تحليل النص الطوبولوجي الدلالي",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicEmerald
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "تمثل الرتب الدلالية تحليلاً عميقاً للهومولوجيا المستمرة (Persistent Homology)، حيث نصل لخلاصة فهم الشكل الرياضي للنص دلالياً ونقيس حجم المكونات المتصلة H0 والفراغات الفكرية H1 بطريقة تضمن استقرار المعنى ومقاومته للتعديلات العشوائية بموجب نظرية ثبات الباركود.",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = CosmicWhite,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Justify
                    )
                )
            }
        }
    }
}

@Composable
fun ManifestoRow(title: String, detail: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, color = CosmicWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(detail, color = CosmicMutedText, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = CosmicSlateLight, thickness = 0.5.dp)
    }
}

@Composable
fun HistoryRecordsDrawer(
    historyItems: List<AlUsusRecordEntity>,
    onSelectLoad: (AlUsusRecordEntity) -> Unit,
    onDelete: (Int) -> Unit,
    onClearAll: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(CosmicSlate),
        colors = CardDefaults.cardColors(containerColor = CosmicSlate)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Expand",
                        tint = CosmicTealAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "أرشيف الفضاء التاريخي (${historyItems.size})",
                        color = CosmicWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                if (expanded && historyItems.isNotEmpty()) {
                    IconButton(onClick = onClearAll) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = CosmicCrimson)
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(CosmicDeepSpace)
                ) {
                    if (historyItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("السجل فارغ. سيتم إدارج Presets تلقائياً بالأرشفة.", color = CosmicMutedText, fontSize = 11.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            items(historyItems) { item ->
                                HistoryItemRow(
                                    item = item,
                                    onSelect = { onSelectLoad(item) },
                                    onDelete = { onDelete(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(
    item: AlUsusRecordEntity,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = CosmicSlate),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Clear, contentDescription = "Delete", tint = CosmicMutedText, modifier = Modifier.size(16.dp))
            }

            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.type == "TEXT") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CosmicEmerald.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("نـصّ", color = CosmicEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CosmicTealAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("عـدد", color = CosmicTealAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (item.value.length > 22) item.value.take(22) + "..." else item.value,
                        color = CosmicWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = if (item.type == "NUMBER") FontFamily.Monospace else FontFamily.Default
                    )
                }

                val descText = remember(item.resultJson) {
                    try {
                        val obj = JSONObject(item.resultJson)
                        if (item.type == "NUMBER") {
                            "المتجه: ${obj.optString("vectorString", "")} • وهج: ${obj.optInt("wahaj", 0)}"
                        } else {
                            "أرقام بيتي: H0=${obj.optInt("b0", 1)} H1=${obj.optInt("b1", 0)}"
                        }
                    } catch (e: Exception) {
                        ""
                    }
                }
                Text(descText, color = CosmicMutedText, fontSize = 9.sp)
            }
        }
    }
}

fun AlUsusCore.getPrimalIndexStr(prime: Long): String {
    return getPrimeIndex(prime).toString()
}

fun Int.coerceAtZero(): Int {
    return if (this < 0) 0 else this
}
