package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.components.ProgressBar
import org.example.project.data.SampleData
import org.example.project.platform.currentTimeMillis
import org.example.project.theme.AppColors

// Fun facts shown randomly at the top
private val funFacts = listOf(
    "\uD83E\uDDE0 Laughing for 15 minutes burns up to 40 calories",
    "\uD83D\uDE02 The average person laughs about 17 times a day",
    "\uD83C\uDFAD The word 'comedy' comes from the Greek 'komos' (a revel)",
    "\uD83D\uDCA1 Humor activates the brain's reward center just like chocolate",
    "\uD83C\uDF1F People remember information 6x better when paired with humor",
    "\uD83E\uDD21 The first joke dates back to 1900 BC in ancient Sumeria",
    "\uD83D\uDC36 Dogs can actually 'laugh' \u2014 it sounds like panting",
    "\u2764\uFE0F Couples who laugh together report higher relationship satisfaction",
    "\uD83C\uDF0D Every culture on Earth has some form of humor",
    "\uD83C\uDFB5 Laughter is contagious \u2014 you're 30x more likely to laugh around others"
)

private val jokesByCategory = mapOf(
    "Business" to listOf(
        "Why did the marketer break up with the calendar? Their dates never converted.",
        "A startup\u2019s burn rate is like a candle \u2014 beautiful until you realize you\u2019re in the dark.",
        "Why do entrepreneurs make bad comedians? They always pivot before the punchline.",
        "I told my boss I needed a raise. He said \u201cYou\u2019re already outstanding in your field.\u201d I work remotely.",
        "What\u2019s a VC\u2019s favorite music? Anything with a good pitch.",
        "Why did the SEO expert cross the road? To get more traffic.",
        "I asked my CRM for relationship advice. It just kept sending follow-ups.",
        "Why don\u2019t marketers ever win at poker? They always show their hand in the funnel.",
        "My startup\u2019s runway is so short, even a paper airplane would overshoot it.",
        "What did the KPI say to the dashboard? \u201cStop putting me on display!\u201d"
    ),
    "Languages" to listOf(
        "I told my friend I speak three languages. He said \u201cNamely?\u201d I said \u201cEnglish, Bad English, and Sarcasm.\u201d",
        "Why did the verb break up with the noun? There was no agreement between them.",
        "I\u2019m learning sign language. It\u2019s pretty handy.",
        "What\u2019s the longest word in English? \u201cSmiles\u201d \u2014 there\u2019s a mile between the first and last letter.",
        "Past, present, and future walked into a bar. It was tense.",
        "Why do French people eat snails? Because they don\u2019t like fast food.",
        "A comma splice walks into a bar, it has a drink.",
        "Never trust atoms that speak multiple languages. They make up everything in every language.",
        "How do you say goodbye to a Spanish dog? Adios, arf-migo!",
        "I used to be a translator but I lost something in the process."
    ),
    "Science" to listOf(
        "Why can you never trust atoms? They make up everything.",
        "I told a chemistry joke once. There was no reaction.",
        "What did the biologist wear on a first date? Designer genes.",
        "Why did the sun go to school? To get a little brighter.",
        "Two blood cells met and fell in love. Alas, it was all in vein.",
        "A photon checks into a hotel. The bellhop asks \u201cNeed help with luggage?\u201d \u201cNo thanks, I\u2019m traveling light.\u201d",
        "Why do biologists look forward to casual Fridays? They can wear genes to work.",
        "What do you call an educated tube? A graduated cylinder.",
        "Schr\u00f6dinger\u2019s cat walks into a bar. And doesn\u2019t.",
        "I would make a chemistry joke but all the good ones argon."
    ),
    "History" to listOf(
        "Why were the early days of history called the Dark Ages? Because there were so many knights.",
        "What did Caesar say to Cleopatra? Toga-ther we can rule the world.",
        "I\u2019m reading a book about anti-gravity in ancient Rome. I can\u2019t put it down.",
        "Why did the archaeologist go bankrupt? Because his career was in ruins.",
        "History teachers always bring up the past. It\u2019s their job.",
        "Napoleon didn\u2019t design his own shirts. He had Bonaparte.",
        "What\u2019s a Roman\u2019s least favorite algebra? Finding X \u2014 Brutus already did.",
        "Why did the medieval painter struggle? He had too many art-illery problems.",
        "The Renaissance was basically Europe\u2019s glow-up phase.",
        "Ancient Egyptian architects were great at pyramid schemes."
    ),
    "Technology" to listOf(
        "Why do programmers prefer dark mode? Because light attracts bugs.",
        "There are 10 types of people: those who understand binary and those who don\u2019t.",
        "A SQL query walks into a bar, sees two tables, and asks \u201cCan I join you?\u201d",
        "Why did the developer go broke? He used up all his cache.",
        "!false \u2014 It\u2019s funny because it\u2019s true.",
        "How many programmers does it take to change a light bulb? None, that\u2019s a hardware problem.",
        "Why was the JavaScript developer sad? He didn\u2019t Node how to Express himself.",
        "A TCP packet walks into a bar and says \u201cI\u2019d like a beer.\u201d Bartender: \u201cYou\u2019d like a beer?\u201d \u201cYes, a beer.\u201d",
        "What\u2019s a computer\u2019s least favorite food? Spam.",
        "Git commit -m \u201cFixed bug\u201d \u2014 narrator: he did not fix the bug."
    ),
    "Psychology" to listOf(
        "Why did the psychologist bring a ladder to work? To reach the higher self.",
        "Pavlov is sitting at a bar. The phone rings. He says \u201cOh no, I forgot to feed the dog.\u201d",
        "How many Freudians does it take to change a light bulb? Two \u2014 one to hold the bulb and one to hold the mother... I mean ladder.",
        "A Freudian slip is when you say one thing but mean your mother.",
        "Why did the cognitive bias go to therapy? It couldn\u2019t see things from other perspectives.",
        "I\u2019m not passive-aggressive. I\u2019m aggressively passive. There\u2019s a difference.",
        "My therapist says I have a fear of abandonment. Then she went on vacation.",
        "Denial, anger, bargaining, depression, acceptance \u2014 also the 5 stages of debugging.",
        "Why did the neuron feel lonely? It had too many axons to grind.",
        "I told my therapist about my dreams. She said \u201cThat\u2019s just your unconscious talking.\u201d"
    ),
    "Geography" to listOf(
        "Why did the map go to therapy? It had too many issues with boundaries.",
        "What\u2019s the capital of Australia? The letter A.",
        "I have a friend who\u2019s obsessed with mountains. I told him to peak somewhere else.",
        "Why do rivers never get lost? They always follow the current trends.",
        "What did the ocean say to the beach? Nothing, it just waved.",
        "Iceland is green and Greenland is icy. Whoever named them was the original troll.",
        "Why did the tectonic plate break up with the other? There was too much friction.",
        "A plateau is the highest form of flattery.",
        "What\u2019s the fastest country? Rush-a.",
        "The Dead Sea isn\u2019t dead, it just has a very salty personality."
    )
)

@Composable
fun JokeScreen(modifier: Modifier = Modifier) {
    val clipboardManager = LocalClipboardManager.current
    val categories = remember { SampleData.decks.map { it.category }.distinct().sorted() }
    val ratings = remember { mutableStateMapOf<String, Int>() }
    val viewedCategories = remember { mutableStateMapOf<String, Boolean>() }
    var visible by remember { mutableStateOf(false) }
    val jokeDay = remember { (currentTimeMillis() / 86400000).toInt() }
    var copiedCategory by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { visible = true }

    // Reset copied indicator after 2 seconds
    LaunchedEffect(copiedCategory) {
        if (copiedCategory != null) {
            delay(2000)
            copiedCategory = null
        }
    }

    val viewedCount = viewedCategories.count { it.value }
    val totalJokeCategories = categories.size
    val totalStars = ratings.values.sum()
    val funFact = remember(jokeDay) { funFacts[jokeDay % funFacts.size] }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.DarkChocolate)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Fixed header
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -30 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("\uD83C\uDCCF", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text("Daily Jokes", color = AppColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Take a break from studying and have a laugh!",
                    color = AppColors.TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Scrollable content
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {

            // Fun fact of the day
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(AppColors.DeepRed.copy(alpha = 0.15f), AppColors.BurntOrange.copy(alpha = 0.1f))))
                    .padding(14.dp)
            ) {
                Text(funFact, color = AppColors.TextSecondary, fontSize = 13.sp, fontStyle = FontStyle.Italic)
            }

            Spacer(Modifier.height(16.dp))

            // Today's progress card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.CardSurface)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Today's Progress", color = AppColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("$viewedCount / $totalJokeCategories", color = AppColors.Amber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    ProgressBar(viewedCount.toFloat() / totalJokeCategories.coerceAtLeast(1))
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MiniStat("\uD83D\uDC40", "$viewedCount", "Viewed")
                        MiniStat("\u2B50", "$totalStars", "Stars given")
                        MiniStat(
                            "\uD83C\uDFC6",
                            if (ratings.isNotEmpty()) {
                                val avg = ratings.values.average()
                                "${(avg * 10).toInt() / 10.0}"
                            } else "-",
                            "Avg rating"
                        )
                    }
                    if (viewedCount == totalJokeCategories) {
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppColors.Success.copy(alpha = 0.15f))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "\uD83C\uDF89 You've seen all of today's jokes! Come back tomorrow!",
                                color = AppColors.Success, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Joke of the day (featured, random category)
            val featuredCategory = remember(jokeDay) { categories[jokeDay % categories.size] }
            val featuredJokes = jokesByCategory[featuredCategory] ?: emptyList()
            val featuredJoke = remember(jokeDay) {
                if (featuredJokes.isNotEmpty()) featuredJokes[(jokeDay * 7) % featuredJokes.size] else ""
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(AppColors.DeepRed, AppColors.BurntOrange, AppColors.Amber)))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("\uD83C\uDFAD", fontSize = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Joke of the Day", color = AppColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "\u201c$featuredJoke\u201d",
                        color = AppColors.TextPrimary,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "\u2014 $featuredCategory",
                        color = AppColors.TextPrimary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Browse by topic",
                color = AppColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))

            // Joke cards per category
            categories.forEach { category ->
                val jokes = jokesByCategory[category] ?: return@forEach
                val jokeIndex = ((jokeDay + category.hashCode()) % jokes.size).let { if (it < 0) it + jokes.size else it }
                val joke = jokes[jokeIndex]
                val isViewed = viewedCategories[category] == true
                val rating = ratings[category] ?: 0
                val isCopied = copiedCategory == category

                JokeCard(
                    category = category,
                    joke = joke,
                    rating = rating,
                    isViewed = isViewed,
                    jokeDay = jokeDay,
                    isCopied = isCopied,
                    onView = { viewedCategories[category] = true },
                    onRate = { stars -> ratings[category] = stars },
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(joke))
                        copiedCategory = category
                    }
                )
                Spacer(Modifier.height(14.dp))
            }

            // Global countdown at the bottom
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.CardSurface)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("New jokes drop at midnight \uD83C\uDF19", color = AppColors.TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    CountdownToNextJoke()
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MiniStat(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = AppColors.TextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun JokeCard(
    category: String,
    joke: String,
    rating: Int,
    isViewed: Boolean,
    jokeDay: Int,
    isCopied: Boolean,
    onView: () -> Unit,
    onRate: (Int) -> Unit,
    onCopy: () -> Unit
) {
    var revealed by remember(category, jokeDay) { mutableStateOf(isViewed) }

    val categoryEmoji = when (category) {
        "Business" -> "\uD83D\uDCBC"
        "Languages" -> "\uD83C\uDF0D"
        "Science" -> "\uD83E\uDDEA"
        "History" -> "\uD83C\uDFDB\uFE0F"
        "Technology" -> "\uD83D\uDCBB"
        "Psychology" -> "\uD83E\uDDE0"
        "Geography" -> "\uD83C\uDF0E"
        else -> "\uD83C\uDCCF"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(AppColors.CardSurface, AppColors.CardSurfaceLight.copy(alpha = 0.5f))))
            .animateContentSize(tween(300))
            .then(if (!revealed) Modifier.clickable { revealed = true; onView() } else Modifier)
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(categoryEmoji, fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(category, color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        if (revealed && rating > 0) {
                            Row {
                                repeat(rating) {
                                    Text("\u2B50", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
                if (!revealed) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.Amber.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Tap to reveal", color = AppColors.Amber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else if (isViewed) {
                    Text("\u2705", fontSize = 16.sp)
                }
            }

            if (revealed) {
                Spacer(Modifier.height(14.dp))

                Text(
                    "\u201c$joke\u201d",
                    color = AppColors.TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(14.dp))

                // Rating + Copy row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stars
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { star ->
                            Icon(
                                if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$star stars",
                                tint = if (star <= rating) AppColors.Amber else AppColors.TextMuted,
                                modifier = Modifier.size(26.dp).clickable { onRate(star) }
                            )
                        }
                    }
                    // Copy button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isCopied) {
                            Text("Copied!", color = AppColors.Success, fontSize = 12.sp)
                        }
                        IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Rounded.ContentCopy,
                                "Copy joke",
                                tint = AppColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.WarmBrown.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\uD83C\uDCCF Tap to see today's joke", color = AppColors.TextMuted, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun CountdownToNextJoke() {
    var secondsLeft by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            val nowSec = currentTimeMillis() / 1000
            val nextMidnight = ((nowSec / 86400) + 1) * 86400
            secondsLeft = nextMidnight - nowSec
            delay(1000)
        }
    }

    val hours = (secondsLeft / 3600).toInt()
    val minutes = ((secondsLeft % 3600) / 60).toInt()
    val seconds = (secondsLeft % 60).toInt()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("\u23F0", fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        // Timer digits
        TimerDigit(hours.toString().padStart(2, '0'))
        Text(" : ", color = AppColors.Amber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TimerDigit(minutes.toString().padStart(2, '0'))
        Text(" : ", color = AppColors.Amber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TimerDigit(seconds.toString().padStart(2, '0'))
    }
}

@Composable
private fun TimerDigit(value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.WarmBrown)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(value, color = AppColors.Amber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
