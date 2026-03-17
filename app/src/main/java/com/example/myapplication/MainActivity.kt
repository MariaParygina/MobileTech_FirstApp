package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// import com.example.my1application.ui.theme.My1ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Временное решение - используем встроенную тему вместо My1ApplicationTheme
            // My1ApplicationTheme {
            Surface(color = MaterialTheme.colorScheme.background) {
                AnimeApp()
            }
            // }
        }
    }
}

data class Anime(
    val id: Int,
    val title: String,
    val year: Int,
    val genre: String,
    val episodes: Int,
)

val sampleAnimeList = listOf(
    Anime(1, "shaman king", 2000, "mech", 450),
    Anime(2, "code geass", 1999, "fantasy", 200),
)

@Composable
fun AnimeApp() {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            sampleAnimeList
        } else {
            sampleAnimeList.filter { anime ->
                anime.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AnimeListScreen(
        animeList = filteredList,
        searchQuery = searchQuery,
        onSearchQueryChange = { newQuery -> searchQuery = newQuery }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(
    animeList: List<Anime>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Anime Viewer") })
        }
    ) { innerPadding ->
        // Используем innerPadding здесь
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp) // Добавляем дополнительный отступ
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by title") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (animeList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No anime found")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(animeList) { anime ->
                        AnimeCard(anime = anime)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimeCard(anime: Anime) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anime.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                Text(
                    text = "${anime.year} - ${anime.genre}",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
                Text(
                    text = "Episodes: ${anime.episodes}",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        AnimeApp()
    }
}