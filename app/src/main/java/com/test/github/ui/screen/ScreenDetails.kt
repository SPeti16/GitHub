package com.test.github.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.test.github.R
import com.test.github.helper.formatDate
import com.test.github.network.Repository
import com.test.github.ui.navigation.DetailsScreen
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenRepository(
    args: DetailsScreen,
    navController: NavHostController
)  {

    val context: Context = LocalContext.current
    val data = Json.decodeFromString<Repository>(args.data)
    val painter = rememberAsyncImagePainter(data.owner.avatarUrl)
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back icon")
                    }
                }
            )
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column (
                modifier = Modifier.padding(8.dp)
            ){

                Row (modifier = Modifier.padding(vertical = 12.dp)){
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp).weight(1f)
                    )
                    Column (
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            data.owner.login,
                            fontWeight = FontWeight.Bold,
                        )
                        Row{
                            OpenBrowser(context.getString(R.string.details_profile), data.owner.htmlUrl, Modifier.weight(1f), context)
                            OpenBrowser(context.getString(R.string.details_repository), data.htmlUrl, Modifier.weight(1f), context)
                        }
                    }
                }

                Text(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    text = data.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
                    contentAlignment = Alignment.TopStart) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        text = data.description ?: "",
                        textAlign = TextAlign.Start,
                    )
                }
                Row {
                    Column (
                        horizontalAlignment = Alignment.Start
                    ){
                        Text("${data.stargazersCount} ⭐")
                        Text("${data.forks} Υ")
                    }
                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ){
                        Text(
                            formatDate(data.createdAt),
                        )
                        Text(
                            formatDate(data.updatedAt),
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun OpenBrowser(title: String, url: String, modifier: Modifier, context: Context ){
    Column (modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(title)
        FloatingActionButton(
            modifier = Modifier.padding(8.dp),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        ) {
            Icon(Icons.Rounded.OpenInBrowser, contentDescription = "Open in browser")
        }
    }
}