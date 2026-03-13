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
import androidx.compose.ui.input.pointer.HistoricalChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember

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

data class AlbumStatus(
    val isFavorite: Boolean = false,
    val isListened: Boolean = false,
    val willListen: Boolean = false
)


val sampleAlbumList = listOf(
    Album(1, "Thank You, Next", "Ariana Grande", 2019, "Pop"),
    Album(2, "BRAT", "Charli XCX", 2024, "Pop"),
    Album(3, "Pop 2", "Charli XCX", 2017, "Pop"),
    Album(4, "how i'm feeling now", "Charli XCX", 2020, "Pop"),
    Album(5, "¥€$", "Tommy Cash", 2018, "Rap, Hip-Hop"),
    Album(6, "Enema Of The State", "Blink-182", 1999, "Pop Rock"),
)

enum class AlbumFilter {
    ALL,
    FAVORITES,
    WILL_LISTEN
}

@Composable
fun AlbumApp() {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    var currentFilter by rememberSaveable {
        mutableStateOf(AlbumFilter.ALL)
    }

    var albumStatuses by rememberSaveable {
        mutableStateOf(
            sampleAlbumList.associate { it.id to AlbumStatus() }
        )
    }

    val filteredList = remember(searchQuery, albumStatuses, currentFilter) {
        val searched = if (searchQuery.isBlank()) {
            sampleAlbumList
        } else {
            sampleAlbumList.filter { album ->
                album.title.contains(searchQuery, ignoreCase = true)  ||
                album.singer.contains(searchQuery, ignoreCase = true)
            }
        }

        searched.filter { album ->
            val status = albumStatuses[album.id] ?: AlbumStatus()
            when (currentFilter) {
                AlbumFilter.ALL -> true
                AlbumFilter.FAVORITES -> status.isFavorite
                AlbumFilter.WILL_LISTEN -> status.willListen
            }
        }
    }


    AlbumListScreen(
        albumList = filteredList,
        albumStatuses = albumStatuses,
        onStatusChange = { albumId, newStatus ->
            albumStatuses = albumStatuses.toMutableMap().apply {
                put(albumId, newStatus)
            }
        },
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        currentFilter = currentFilter,
        onFilterChange = { currentFilter = it }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    albumList: List<Album>,
    albumStatuses: Map<Int, AlbumStatus>,
    onStatusChange: (Int, AlbumStatus) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentFilter: AlbumFilter,
    onFilterChange: (AlbumFilter) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Album Viewer") }) }) { innerPadding ->
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
                label = { Text("Search by title or artist") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = currentFilter == AlbumFilter.ALL,
                    onClick = { onFilterChange(AlbumFilter.ALL) },
                    label = { Text("All") }
                )

                FilterChip(
                    selected = currentFilter == AlbumFilter.FAVORITES,
                    onClick = { onFilterChange(AlbumFilter.FAVORITES) },
                    label = { Text("Favorites") }
                )

                FilterChip(
                    selected = currentFilter == AlbumFilter.WILL_LISTEN,
                    onClick = { onFilterChange(AlbumFilter.WILL_LISTEN) },
                    label = { Text("Listen Later") }
                )

//                FilterChip(
//                    selected = currentFilter == AlbumFilter.LISTENED,
//                    onClick = { onFilterChange(AlbumFilter.LISTENED) },
//                    label = { Text("Listened") }
//                )
            }


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
                        AlbumCard(
                            album = album,
                            status = albumStatuses[album.id] ?: AlbumStatus(),
                            onStatusChange = { newStatus ->
                                onStatusChange(album.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AlbumCard(album: Album,
              status: AlbumStatus,
              onStatusChange: (AlbumStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = album.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = album.singer, fontStyle = FontStyle.Italic, fontSize = 16.sp)
            Text(text = "Year: ${album.year}", fontSize = 16.sp)
            Text(text = "Genre: ${album.genre}", fontSize = 16.sp)

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    onStatusChange(status.copy(isFavorite = !status.isFavorite))
                }) {
                    Text(if (status.isFavorite) "Favorite" else "Like")
                }

                Button(onClick = {
                    onStatusChange(status.copy(willListen = !status.willListen))
                }) {
                    Text(if (status.willListen) "In Playlist" else "Add to Playlist")
                }
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