package com.rejowan.numberconverter.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.numberconverter.presentation.common.theme.OnboardingColors
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

/**
 * Shape style per page — three distinct visual languages so each page reads
 * differently from the next:
 *  - HEX:    8-percent rounded with cut diagonals (binary/digital feel)
 *  - ROUNDED: standard rounded percentages (modular/numeric feel)
 *  - PILL:    fully rounded ovals (continuous/historical feel)
 */
private enum class ShapeStyle { HEX, ROUNDED, PILL }

private data class ShapeConfig(
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val rotation: Float = 0f,
    val alpha: Float = 0.1f,
    val topStartCorner: Int = 50,
    val topEndCorner: Int = 50,
    val bottomStartCorner: Int = 50,
    val bottomEndCorner: Int = 50,
    val style: ShapeStyle = ShapeStyle.ROUNDED
)

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val accentColor: Color,
    val iconShapeStyle: ShapeStyle,
    val topStartCorner: Int,
    val topEndCorner: Int,
    val bottomStartCorner: Int,
    val bottomEndCorner: Int,
    val iconContainerScale: Float = 1f,
    val iconOffsetX: Float = 0f,
    val iconOffsetY: Float = 0f,
    val iconRotation: Float = 0f,
    val bgShape1: ShapeConfig,
    val bgShape2: ShapeConfig,
    val bgShape3: ShapeConfig,
    val bgShape4: ShapeConfig? = null,
    val bgShape5: ShapeConfig? = null
)

private fun shapeFor(
    style: ShapeStyle,
    topStart: Int,
    topEnd: Int,
    bottomStart: Int,
    bottomEnd: Int
): Shape = when (style) {
    ShapeStyle.PILL -> CircleShape
    ShapeStyle.HEX -> CutCornerShape(
        topStartPercent = (topStart / 2).coerceIn(0, 50),
        topEndPercent = (topEnd / 2).coerceIn(0, 50),
        bottomStartPercent = (bottomStart / 2).coerceIn(0, 50),
        bottomEndPercent = (bottomEnd / 2).coerceIn(0, 50)
    )
    ShapeStyle.ROUNDED -> RoundedCornerShape(
        topStartPercent = topStart.coerceIn(0, 50),
        topEndPercent = topEnd.coerceIn(0, 50),
        bottomStartPercent = bottomStart.coerceIn(0, 50),
        bottomEndPercent = bottomEnd.coerceIn(0, 50)
    )
}

@Composable
private fun getOnboardingPages(): List<OnboardingPage> = listOf(
    // Page 1: Convert — angular hex feel
    OnboardingPage(
        icon = Icons.Rounded.SwapHoriz,
        title = "Convert Any Base",
        description = "Switch between binary, octal, decimal, and hexadecimal effortlessly — with full step-by-step explanations.",
        accentColor = OnboardingColors.Page1,
        iconShapeStyle = ShapeStyle.HEX,
        topStartCorner = 30,
        topEndCorner = 50,
        bottomStartCorner = 50,
        bottomEndCorner = 30,
        iconContainerScale = 1.04f,
        iconRotation = -3f,
        bgShape1 = ShapeConfig(
            offsetX = -50f, offsetY = 110f, size = 150f,
            rotation = 30f, alpha = 0.07f,
            topStartCorner = 10, topEndCorner = 50,
            bottomStartCorner = 50, bottomEndCorner = 10,
            style = ShapeStyle.HEX
        ),
        bgShape2 = ShapeConfig(
            offsetX = 270f, offsetY = 360f, size = 195f,
            rotation = -25f, alpha = 0.05f,
            topStartCorner = 50, topEndCorner = 18,
            bottomStartCorner = 18, bottomEndCorner = 50,
            style = ShapeStyle.HEX
        ),
        bgShape3 = ShapeConfig(
            offsetX = 305f, offsetY = 90f, size = 78f,
            rotation = 55f, alpha = 0.045f,
            topStartCorner = 22, topEndCorner = 50,
            bottomStartCorner = 22, bottomEndCorner = 50,
            style = ShapeStyle.ROUNDED
        ),
        bgShape4 = ShapeConfig(
            offsetX = -28f, offsetY = 540f, size = 100f,
            rotation = -40f, alpha = 0.04f,
            topStartCorner = 35, topEndCorner = 12,
            bottomStartCorner = 35, bottomEndCorner = 12,
            style = ShapeStyle.HEX
        ),
        bgShape5 = ShapeConfig(
            offsetX = 175f, offsetY = 220f, size = 58f,
            rotation = 25f, alpha = 0.03f,
            style = ShapeStyle.PILL
        )
    ),
    // Page 2: Calculator — modular rounded feel
    OnboardingPage(
        icon = Icons.Rounded.Calculate,
        title = "Calculate Across Bases",
        description = "Add, subtract, multiply, and divide in any base — the math and the breakdown are always shown.",
        accentColor = OnboardingColors.Page2,
        iconShapeStyle = ShapeStyle.ROUNDED,
        topStartCorner = 40,
        topEndCorner = 40,
        bottomStartCorner = 22,
        bottomEndCorner = 22,
        iconContainerScale = 1.0f,
        iconOffsetY = -4f,
        iconRotation = 4f,
        bgShape1 = ShapeConfig(
            offsetX = 285f, offsetY = 130f, size = 205f,
            rotation = 18f, alpha = 0.06f,
            topStartCorner = 50, topEndCorner = 22,
            bottomStartCorner = 22, bottomEndCorner = 50,
            style = ShapeStyle.ROUNDED
        ),
        bgShape2 = ShapeConfig(
            offsetX = -90f, offsetY = 320f, size = 175f,
            rotation = -22f, alpha = 0.055f,
            topStartCorner = 30, topEndCorner = 50,
            bottomStartCorner = 50, bottomEndCorner = 30,
            style = ShapeStyle.ROUNDED
        ),
        bgShape3 = ShapeConfig(
            offsetX = 195f, offsetY = 510f, size = 115f,
            rotation = 0f, alpha = 0.045f,
            style = ShapeStyle.PILL
        ),
        bgShape4 = ShapeConfig(
            offsetX = -32f, offsetY = 70f, size = 84f,
            rotation = 28f, alpha = 0.035f,
            topStartCorner = 50, topEndCorner = 30,
            bottomStartCorner = 30, bottomEndCorner = 50,
            style = ShapeStyle.ROUNDED
        ),
        bgShape5 = ShapeConfig(
            offsetX = 325f, offsetY = 440f, size = 52f,
            rotation = 0f, alpha = 0.03f,
            style = ShapeStyle.PILL
        )
    ),
    // Page 3: History — soft pill / continuous feel
    OnboardingPage(
        icon = Icons.Rounded.History,
        title = "History at a Glance",
        description = "Bookmark conversions, revisit them later, and pick up right where you left off.",
        accentColor = OnboardingColors.Page3,
        iconShapeStyle = ShapeStyle.PILL,
        topStartCorner = 50,
        topEndCorner = 50,
        bottomStartCorner = 50,
        bottomEndCorner = 50,
        iconContainerScale = 1.02f,
        iconOffsetX = 0f,
        iconRotation = 0f,
        bgShape1 = ShapeConfig(
            offsetX = -70f, offsetY = 410f, size = 230f,
            rotation = 30f, alpha = 0.06f,
            style = ShapeStyle.PILL
        ),
        bgShape2 = ShapeConfig(
            offsetX = 280f, offsetY = 70f, size = 158f,
            rotation = -20f, alpha = 0.055f,
            topStartCorner = 50, topEndCorner = 50,
            bottomStartCorner = 28, bottomEndCorner = 28,
            style = ShapeStyle.ROUNDED
        ),
        bgShape3 = ShapeConfig(
            offsetX = 315f, offsetY = 490f, size = 125f,
            rotation = 45f, alpha = 0.045f,
            style = ShapeStyle.PILL
        ),
        bgShape4 = ShapeConfig(
            offsetX = -45f, offsetY = 200f, size = 70f,
            rotation = -35f, alpha = 0.04f,
            style = ShapeStyle.PILL
        ),
        bgShape5 = ShapeConfig(
            offsetX = 145f, offsetY = 290f, size = 48f,
            rotation = 0f, alpha = 0.025f,
            style = ShapeStyle.PILL
        )
    )
)

/**
 * Three-page onboarding with morphing accent colors and ambient background shapes.
 * Adapted from Linky — distinct shape vocabulary per page (cut/rounded/pill) and
 * slower ambient timing for a calmer, computational feel.
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val onboardingPages = getOnboardingPages()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { onboardingPages.size }
    )

    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val isLastPage = currentPage == onboardingPages.size - 1

    val currentAccentColor by animateColorAsState(
        targetValue = onboardingPages[currentPage].accentColor,
        animationSpec = tween(450, easing = EaseInOutQuart),
        label = "accent"
    )

    // Icon container corner animations (slightly slower than Linky)
    val topStartCorner by animateFloatAsState(
        targetValue = onboardingPages[currentPage].topStartCorner.toFloat(),
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "topStart"
    )
    val topEndCorner by animateFloatAsState(
        targetValue = onboardingPages[currentPage].topEndCorner.toFloat(),
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "topEnd"
    )
    val bottomStartCorner by animateFloatAsState(
        targetValue = onboardingPages[currentPage].bottomStartCorner.toFloat(),
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "bottomStart"
    )
    val bottomEndCorner by animateFloatAsState(
        targetValue = onboardingPages[currentPage].bottomEndCorner.toFloat(),
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "bottomEnd"
    )

    val iconShapeStyle = onboardingPages[currentPage].iconShapeStyle

    val iconContainerScale by animateFloatAsState(
        targetValue = onboardingPages[currentPage].iconContainerScale,
        animationSpec = tween(500),
        label = "iconScale"
    )
    val iconOffsetX by animateFloatAsState(
        targetValue = onboardingPages[currentPage].iconOffsetX,
        animationSpec = tween(500),
        label = "iconOffsetX"
    )
    val iconOffsetY by animateFloatAsState(
        targetValue = onboardingPages[currentPage].iconOffsetY,
        animationSpec = tween(500),
        label = "iconOffsetY"
    )
    val iconRotation by animateFloatAsState(
        targetValue = onboardingPages[currentPage].iconRotation,
        animationSpec = tween(700),
        label = "iconRotation"
    )

    // Background shape transition animations
    val bg1X by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.offsetX, animationSpec = tween(700), label = "bg1X")
    val bg1Y by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.offsetY, animationSpec = tween(750), label = "bg1Y")
    val bg1Size by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.size, animationSpec = tween(600), label = "bg1Size")
    val bg1Rotation by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.rotation, animationSpec = tween(800), label = "bg1Rot")
    val bg1Alpha by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.alpha, animationSpec = tween(500), label = "bg1Alpha")
    val bg1TS by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.topStartCorner.toFloat(), animationSpec = tween(650), label = "bg1TS")
    val bg1TE by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.topEndCorner.toFloat(), animationSpec = tween(650), label = "bg1TE")
    val bg1BS by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.bottomStartCorner.toFloat(), animationSpec = tween(650), label = "bg1BS")
    val bg1BE by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape1.bottomEndCorner.toFloat(), animationSpec = tween(650), label = "bg1BE")
    val bg1Style = onboardingPages[currentPage].bgShape1.style

    val bg2X by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.offsetX, animationSpec = tween(800), label = "bg2X")
    val bg2Y by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.offsetY, animationSpec = tween(850), label = "bg2Y")
    val bg2Size by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.size, animationSpec = tween(700), label = "bg2Size")
    val bg2Rotation by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.rotation, animationSpec = tween(900), label = "bg2Rot")
    val bg2Alpha by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.alpha, animationSpec = tween(550), label = "bg2Alpha")
    val bg2TS by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.topStartCorner.toFloat(), animationSpec = tween(700), label = "bg2TS")
    val bg2TE by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.topEndCorner.toFloat(), animationSpec = tween(700), label = "bg2TE")
    val bg2BS by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.bottomStartCorner.toFloat(), animationSpec = tween(700), label = "bg2BS")
    val bg2BE by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape2.bottomEndCorner.toFloat(), animationSpec = tween(700), label = "bg2BE")
    val bg2Style = onboardingPages[currentPage].bgShape2.style

    val bg3X by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.offsetX, animationSpec = tween(650), label = "bg3X")
    val bg3Y by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.offsetY, animationSpec = tween(700), label = "bg3Y")
    val bg3Size by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.size, animationSpec = tween(550), label = "bg3Size")
    val bg3Rotation by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.rotation, animationSpec = tween(750), label = "bg3Rot")
    val bg3Alpha by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.alpha, animationSpec = tween(450), label = "bg3Alpha")
    val bg3TS by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.topStartCorner.toFloat(), animationSpec = tween(600), label = "bg3TS")
    val bg3TE by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.topEndCorner.toFloat(), animationSpec = tween(600), label = "bg3TE")
    val bg3BS by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.bottomStartCorner.toFloat(), animationSpec = tween(600), label = "bg3BS")
    val bg3BE by animateFloatAsState(targetValue = onboardingPages[currentPage].bgShape3.bottomEndCorner.toFloat(), animationSpec = tween(600), label = "bg3BE")
    val bg3Style = onboardingPages[currentPage].bgShape3.style

    val bg4Config = onboardingPages[currentPage].bgShape4
    val bg4X by animateFloatAsState(targetValue = bg4Config?.offsetX ?: -200f, animationSpec = tween(720), label = "bg4X")
    val bg4Y by animateFloatAsState(targetValue = bg4Config?.offsetY ?: 300f, animationSpec = tween(770), label = "bg4Y")
    val bg4Size by animateFloatAsState(targetValue = bg4Config?.size ?: 0f, animationSpec = tween(620), label = "bg4Size")
    val bg4Rotation by animateFloatAsState(targetValue = bg4Config?.rotation ?: 0f, animationSpec = tween(850), label = "bg4Rot")
    val bg4Alpha by animateFloatAsState(targetValue = bg4Config?.alpha ?: 0f, animationSpec = tween(500), label = "bg4Alpha")
    val bg4TS by animateFloatAsState(targetValue = (bg4Config?.topStartCorner ?: 50).toFloat(), animationSpec = tween(670), label = "bg4TS")
    val bg4TE by animateFloatAsState(targetValue = (bg4Config?.topEndCorner ?: 50).toFloat(), animationSpec = tween(670), label = "bg4TE")
    val bg4BS by animateFloatAsState(targetValue = (bg4Config?.bottomStartCorner ?: 50).toFloat(), animationSpec = tween(670), label = "bg4BS")
    val bg4BE by animateFloatAsState(targetValue = (bg4Config?.bottomEndCorner ?: 50).toFloat(), animationSpec = tween(670), label = "bg4BE")
    val bg4Style = bg4Config?.style ?: ShapeStyle.PILL

    val bg5Config = onboardingPages[currentPage].bgShape5
    val bg5X by animateFloatAsState(targetValue = bg5Config?.offsetX ?: -200f, animationSpec = tween(770), label = "bg5X")
    val bg5Y by animateFloatAsState(targetValue = bg5Config?.offsetY ?: 300f, animationSpec = tween(810), label = "bg5Y")
    val bg5Size by animateFloatAsState(targetValue = bg5Config?.size ?: 0f, animationSpec = tween(670), label = "bg5Size")
    val bg5Alpha by animateFloatAsState(targetValue = bg5Config?.alpha ?: 0f, animationSpec = tween(530), label = "bg5Alpha")
    val bg5Style = bg5Config?.style ?: ShapeStyle.PILL

    // Ambient infinite animations — slower than Linky for a calmer feel
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val bgRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bgRotation"
    )

    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )

    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundMorphingShapes(
                accentColor = currentAccentColor,
                rotation = bgRotation,
                drift = drift,
                breathe = breathe,
                shape1X = bg1X, shape1Y = bg1Y, shape1Size = bg1Size,
                shape1Rotation = bg1Rotation, shape1Alpha = bg1Alpha,
                shape1TS = bg1TS, shape1TE = bg1TE, shape1BS = bg1BS, shape1BE = bg1BE,
                shape1Style = bg1Style,
                shape2X = bg2X, shape2Y = bg2Y, shape2Size = bg2Size,
                shape2Rotation = bg2Rotation, shape2Alpha = bg2Alpha,
                shape2TS = bg2TS, shape2TE = bg2TE, shape2BS = bg2BS, shape2BE = bg2BE,
                shape2Style = bg2Style,
                shape3X = bg3X, shape3Y = bg3Y, shape3Size = bg3Size,
                shape3Rotation = bg3Rotation, shape3Alpha = bg3Alpha,
                shape3TS = bg3TS, shape3TE = bg3TE, shape3BS = bg3BS, shape3BE = bg3BE,
                shape3Style = bg3Style,
                shape4X = bg4X, shape4Y = bg4Y, shape4Size = bg4Size,
                shape4Rotation = bg4Rotation, shape4Alpha = bg4Alpha,
                shape4TS = bg4TS, shape4TE = bg4TE, shape4BS = bg4BS, shape4BE = bg4BE,
                shape4Style = bg4Style,
                shape5X = bg5X, shape5Y = bg5Y, shape5Size = bg5Size,
                shape5Alpha = bg5Alpha, shape5Style = bg5Style
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Skip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    if (!isLastPage) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(onboardingPages.size - 1)
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(
                                "Skip",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                    OnboardingPageContent(
                        page = onboardingPages[page],
                        pageOffset = pageOffset,
                        iconShapeStyle = iconShapeStyle,
                        topStartCorner = topStartCorner,
                        topEndCorner = topEndCorner,
                        bottomStartCorner = bottomStartCorner,
                        bottomEndCorner = bottomEndCorner,
                        iconContainerScale = iconContainerScale,
                        iconOffsetX = iconOffsetX,
                        iconOffsetY = iconOffsetY,
                        iconRotation = iconRotation,
                        pulseScale = pulseScale,
                        pulseAlpha = pulseAlpha,
                        glowScale = glowScale,
                        glowAlpha = glowAlpha,
                        accentColor = currentAccentColor
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        repeat(onboardingPages.size) { index ->
                            PageIndicator(
                                isSelected = index == currentPage,
                                accentColor = currentAccentColor,
                                breathe = if (index == currentPage) breathe else 1f
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isLastPage) {
                                onComplete()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(
                            topStart = topStartCorner.toInt().coerceIn(14, 26).dp,
                            topEnd = topEndCorner.toInt().coerceIn(14, 26).dp,
                            bottomStart = bottomStartCorner.toInt().coerceIn(14, 26).dp,
                            bottomEnd = bottomEndCorner.toInt().coerceIn(14, 26).dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentAccentColor
                        )
                    ) {
                        Text(
                            text = if (isLastPage) "Get Started" else "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!isLastPage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BackgroundMorphingShapes(
    accentColor: Color,
    rotation: Float,
    drift: Float,
    breathe: Float,
    shape1X: Float, shape1Y: Float, shape1Size: Float,
    shape1Rotation: Float, shape1Alpha: Float,
    shape1TS: Float, shape1TE: Float, shape1BS: Float, shape1BE: Float,
    shape1Style: ShapeStyle,
    shape2X: Float, shape2Y: Float, shape2Size: Float,
    shape2Rotation: Float, shape2Alpha: Float,
    shape2TS: Float, shape2TE: Float, shape2BS: Float, shape2BE: Float,
    shape2Style: ShapeStyle,
    shape3X: Float, shape3Y: Float, shape3Size: Float,
    shape3Rotation: Float, shape3Alpha: Float,
    shape3TS: Float, shape3TE: Float, shape3BS: Float, shape3BE: Float,
    shape3Style: ShapeStyle,
    shape4X: Float, shape4Y: Float, shape4Size: Float,
    shape4Rotation: Float, shape4Alpha: Float,
    shape4TS: Float, shape4TE: Float, shape4BS: Float, shape4BE: Float,
    shape4Style: ShapeStyle,
    shape5X: Float, shape5Y: Float, shape5Size: Float,
    shape5Alpha: Float, shape5Style: ShapeStyle
) {
    // Lissajous-like drift: x and y use cos/sin at different rates
    val driftX = cos(drift.toDouble()).toFloat() * 10f
    val driftY = sin((drift * 1.3f).toDouble()).toFloat() * 8f

    Box(
        modifier = Modifier
            .size(shape1Size.dp)
            .offset(x = (shape1X + driftX * 0.7f).dp, y = (shape1Y + driftY * 0.5f).dp)
            .scale(breathe)
            .rotate(shape1Rotation + rotation * 0.015f)
            .clip(shapeFor(shape1Style, shape1TS.toInt(), shape1TE.toInt(), shape1BS.toInt(), shape1BE.toInt()))
            .background(accentColor.copy(alpha = shape1Alpha))
    )

    Box(
        modifier = Modifier
            .size(shape2Size.dp)
            .offset(x = (shape2X - driftX * 0.5f).dp, y = (shape2Y + driftY * 0.8f).dp)
            .scale(1.02f + (breathe - 1f) * 0.6f)
            .rotate(shape2Rotation - rotation * 0.010f)
            .clip(shapeFor(shape2Style, shape2TS.toInt(), shape2TE.toInt(), shape2BS.toInt(), shape2BE.toInt()))
            .background(accentColor.copy(alpha = shape2Alpha))
    )

    Box(
        modifier = Modifier
            .size(shape3Size.dp)
            .offset(x = (shape3X + driftX * 0.4f).dp, y = (shape3Y - driftY * 1.1f).dp)
            .scale(breathe * 0.93f)
            .rotate(shape3Rotation + rotation * 0.020f)
            .clip(shapeFor(shape3Style, shape3TS.toInt(), shape3TE.toInt(), shape3BS.toInt(), shape3BE.toInt()))
            .background(accentColor.copy(alpha = shape3Alpha))
    )

    if (shape4Alpha > 0.01f) {
        Box(
            modifier = Modifier
                .size(shape4Size.dp)
                .offset(x = (shape4X + driftX * 1.1f).dp, y = (shape4Y - driftY * 0.4f).dp)
                .scale(1f + (breathe - 1f) * 0.8f)
                .rotate(shape4Rotation - rotation * 0.025f)
                .clip(shapeFor(shape4Style, shape4TS.toInt(), shape4TE.toInt(), shape4BS.toInt(), shape4BE.toInt()))
                .background(accentColor.copy(alpha = shape4Alpha))
        )
    }

    if (shape5Alpha > 0.01f) {
        Box(
            modifier = Modifier
                .size(shape5Size.dp)
                .offset(x = (shape5X - driftX * 0.3f).dp, y = (shape5Y + driftY * 0.6f).dp)
                .scale(breathe * 1.08f)
                .alpha(shape5Alpha * (0.8f + sin((drift * 2).toDouble()).toFloat() * 0.2f))
                .clip(if (shape5Style == ShapeStyle.PILL) CircleShape else RoundedCornerShape(50))
                .background(accentColor.copy(alpha = shape5Alpha))
        )
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageOffset: Float,
    iconShapeStyle: ShapeStyle,
    topStartCorner: Float,
    topEndCorner: Float,
    bottomStartCorner: Float,
    bottomEndCorner: Float,
    iconContainerScale: Float,
    iconOffsetX: Float,
    iconOffsetY: Float,
    iconRotation: Float,
    pulseScale: Float,
    pulseAlpha: Float,
    glowScale: Float,
    glowAlpha: Float,
    accentColor: Color
) {
    val contentAlpha by animateFloatAsState(
        targetValue = 1f - (pageOffset.absoluteValue * 0.6f).coerceIn(0f, 0.6f),
        animationSpec = spring(),
        label = "alpha"
    )

    val contentScale by animateFloatAsState(
        targetValue = 1f - (pageOffset.absoluteValue * 0.08f).coerceIn(0f, 0.08f),
        animationSpec = spring(),
        label = "scale"
    )

    val contentTranslateX by animateFloatAsState(
        targetValue = pageOffset * 30f,
        animationSpec = spring(),
        label = "translateX"
    )

    val iconShape = shapeFor(
        style = iconShapeStyle,
        topStart = topStartCorner.toInt(),
        topEnd = topEndCorner.toInt(),
        bottomStart = bottomStartCorner.toInt(),
        bottomEnd = bottomEndCorner.toInt()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .graphicsLayer {
                alpha = contentAlpha
                scaleX = contentScale
                scaleY = contentScale
                translationX = contentTranslateX
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = iconOffsetX.dp, y = iconOffsetY.dp)
                .scale(iconContainerScale)
                .rotate(iconRotation),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(glowScale)
                    .clip(iconShape)
                    .background(accentColor.copy(alpha = glowAlpha))
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale)
                    .clip(iconShape)
                    .background(accentColor.copy(alpha = pulseAlpha))
            )

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(iconShape)
                    .background(accentColor.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .scale(1f + (pulseScale - 1f) * 0.2f),
                    tint = accentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

/**
 * Page indicator — square-rounded chips instead of Linky's pill, slightly wider
 * when selected (28dp vs Linky's 24dp).
 */
@Composable
private fun PageIndicator(
    isSelected: Boolean,
    accentColor: Color,
    breathe: Float
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 8.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 320f),
        label = "width"
    )

    val color by animateColorAsState(
        targetValue = if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(280),
        label = "color"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .height(8.dp)
            .width(width)
            .scale(if (isSelected) 1f + (breathe - 1f) * 0.12f else 1f)
            .clip(RoundedCornerShape(3.dp))
            .background(color)
    )
}
