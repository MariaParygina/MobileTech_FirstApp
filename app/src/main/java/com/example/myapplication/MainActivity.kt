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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.FilterChipDefaults

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
    val listenState: ListenState = ListenState.NONE
)

val sampleAlbumList = listOf(
    Album(1, "Thank You, Next", "Ariana Grande", 2019, "Pop"),
    Album(2, "BRAT", "Charli XCX", 2024, "Pop"),
    Album(3, "Pop 2", "Charli XCX", 2017, "Pop"),
    Album(4, "Kid A", "Radiohead", 2000, "Electronica, Ambient"),
    Album(5, "Swimming", "Mac Miller", 2018, "Rap"),
    Album(6, "Meds", "Placebo", 2006, "Alternative Rock"),
    Album(7, "In Rainbows", "Radiohead", 2007, "Alternative Rock"),
    Album(8, "how i'm feeling now", "Charli XCX", 2020, "Pop"),
    Album(9, "¥€$", "Tommy Cash", 2018, "Rap, Hip-Hop"),
    Album(10, "Enema Of The State", "Blink-182", 1999, "Pop Rock"),
)

enum class AlbumFilter {
    ALL,
    FAVORITES,
    IN_PLAYLIST,
    LISTENING,
    LISTENED
}

enum class ListenState {
    NONE,
    IN_PLAYLIST,
    LISTENING,
    LISTENED
}

class AlbumStateHolder {
    var searchQuery by mutableStateOf("")
    var currentFilter by mutableStateOf(AlbumFilter.ALL)
    var albumStatuses by mutableStateOf(
        sampleAlbumList.associate { it.id to AlbumStatus() }
    )

    val filteredList: List<Album>
        get() {
            val searched = if (searchQuery.isBlank()) {
                sampleAlbumList
            } else {
                sampleAlbumList.filter { album ->
                    album.title.contains(searchQuery, ignoreCase = true) ||
                            album.singer.contains(searchQuery, ignoreCase = true)
                }
            }

            return searched.filter { album ->
                val status = albumStatuses[album.id] ?: AlbumStatus()
                when (currentFilter) {
                    AlbumFilter.ALL -> true
                    AlbumFilter.FAVORITES -> status.isFavorite
                    AlbumFilter.IN_PLAYLIST -> status.listenState == ListenState.IN_PLAYLIST
                    AlbumFilter.LISTENING -> status.listenState == ListenState.LISTENING
                    AlbumFilter.LISTENED -> status.listenState == ListenState.LISTENED
                }
            }
        }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun updateFilter(filter: AlbumFilter) {
        currentFilter = filter
    }

    fun updateAlbumStatus(albumId: Int, newStatus: AlbumStatus) {
        albumStatuses = albumStatuses.toMutableMap().apply {
            put(albumId, newStatus)
        }
    }
}

@Composable
fun AlbumApp() {
    val stateHolder = remember { AlbumStateHolder() }

    AlbumListScreen(
        albumList = stateHolder.filteredList,
        albumStatuses = stateHolder.albumStatuses,
        onStatusChange = { albumId, newStatus ->
            stateHolder.updateAlbumStatus(albumId, newStatus)
        },
        searchQuery = stateHolder.searchQuery,
        onSearchQueryChange = { stateHolder.updateSearchQuery(it) },
        currentFilter = stateHolder.currentFilter,
        onFilterChange = { stateHolder.updateFilter(it) }
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
                .padding(horizontal = 4.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                label = { Text("Search by title or artist") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8E8F0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val favorites = albumStatuses.count { it.value.isFavorite }
                    val inPlaylist = albumStatuses.count { it.value.listenState == ListenState.IN_PLAYLIST }
                    val listening = albumStatuses.count { it.value.listenState == ListenState.LISTENING }
                    val listened = albumStatuses.count { it.value.listenState == ListenState.LISTENED }

                    Text("Liked $favorites", fontSize = 14.sp)
                    Text("Saved $inPlaylist", fontSize = 14.sp)
                    Text("In process $listening", fontSize = 14.sp)
                    Text("Done $listened", fontSize = 14.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    3.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                FilterChip(
                    selected = currentFilter == AlbumFilter.ALL,
                    onClick = { onFilterChange(AlbumFilter.ALL) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF484D6D),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = currentFilter == AlbumFilter.FAVORITES,
                    onClick = { onFilterChange(AlbumFilter.FAVORITES) },
                    label = { Text("Favorites") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF484D6D),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = currentFilter == AlbumFilter.IN_PLAYLIST,
                    onClick = { onFilterChange(AlbumFilter.IN_PLAYLIST) },
                    label = { Text("In Playlist") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF484D6D),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = currentFilter == AlbumFilter.LISTENING,
                    onClick = { onFilterChange(AlbumFilter.LISTENING) },
                    label = { Text("Listening") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF484D6D),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = currentFilter == AlbumFilter.LISTENED,
                    onClick = { onFilterChange(AlbumFilter.LISTENED) },
                    label = { Text("Done") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF484D6D),
                        selectedLabelColor = Color.White
                    )
                )
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
fun AlbumCard(
    album: Album,
    status: AlbumStatus,
    onStatusChange: (AlbumStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDFDFE5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp
                ),
        ) {
            Text(text = album.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = album.singer, fontStyle = FontStyle.Italic, fontSize = 16.sp)
            Text(text = "Year: ${album.year}", fontSize = 16.sp)
            Text(text = "Genre: ${album.genre}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(4.dp))
            val statusText = when (status.listenState) {
                ListenState.NONE -> "Status: Not in playlist"
                ListenState.IN_PLAYLIST -> "Status: In playlist"
                ListenState.LISTENING -> "Status: Listening"
                ListenState.LISTENED -> "Status: Listened"
            }
            Text(text = statusText, fontSize = 12.sp, color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                Button(
                    onClick = {
                        onStatusChange(status.copy(isFavorite = !status.isFavorite))
                    },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD72638)
                    )
                ) {
                    Text(if (status.isFavorite) "Favorite" else "Like")
                }

                Button(
                    onClick = {
                        val newState = when (status.listenState) {
                            ListenState.NONE -> ListenState.IN_PLAYLIST
                            ListenState.IN_PLAYLIST -> ListenState.LISTENING
                            ListenState.LISTENING -> ListenState.LISTENED
                            ListenState.LISTENED -> ListenState.NONE
                        }
                        onStatusChange(status.copy(listenState = newState))
                    },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF484D6D)
                    )
                ) {
                    val text = when (status.listenState) {
                        ListenState.NONE -> "Add to playlist"
                        ListenState.IN_PLAYLIST -> "Start listening"
                        ListenState.LISTENING -> "Make Listened"
                        ListenState.LISTENED -> "Start again"
                    }
                    Text(text)
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