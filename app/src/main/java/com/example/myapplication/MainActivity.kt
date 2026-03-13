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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                AlbumApp()
            }
        }
    }
}

data class Album(
    val id: Int,
    val title: String,
    val singer: String,
    val year: Int,
    val genre: String,
)


val sampleAlbumList = listOf(
    Album(1, "Thank You, Next", "Ariana Grande", 2019, "Pop"),
    Album(2, "BRAT", "Charli XCX", 2024, "Pop"),
    Album(3, "Pop 2", "Charli XCX", 2017, "Pop"),
    Album(4, "how i'm feeling now", "Charli XCX", 2020, "Pop"),
    Album(5, "¥€$", "Tommy Cash", 2018, "Rap, Hip-Hop"),
    Album(6, "Enema Of The State", "Blink-182", 1999, "Pop Rock"),
)

@Composable
fun AlbumApp() {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            sampleAlbumList
        } else {
            sampleAlbumList.filter { album ->
                album.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlbumListScreen(
        albumList = filteredList,
        searchQuery = searchQuery,
        onSearchQueryChange = { newQuery -> searchQuery = newQuery }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    albumList: List<Album>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Album Viewer") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by title") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (albumList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No album found")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(albumList) { album ->
                        AlbumCard(album = album)
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumCard(album: Album) {
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
                    text = album.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = album.singer,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp
                )
                Text(
                    text = "Year: ${album.year}",
                    fontSize = 16.sp
                )
                Text(
                    text = "Genre: ${album.genre}",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        AlbumApp()
    }
}