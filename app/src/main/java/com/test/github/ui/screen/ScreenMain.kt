package com.test.github.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowLeft
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.test.github.helper.formatDate
import com.test.github.network.Repository
import com.test.github.ui.navigation.DetailsScreen
import com.test.github.ui.screen.viewModel.ViewModelMain
import kotlinx.serialization.json.Json


@Composable
fun ScreenMain(
    navController: NavHostController,
    viewModel: ViewModelMain = hiltViewModel()
)  {

    DisposableEffect(Unit) {
        val job = viewModel.observeUserSearch()
        onDispose {
            job.cancel()
        }
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchList by viewModel.searchList.collectAsState()

    var page by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        modifier = Modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .clickable {
                    focusManager.clearFocus()
                }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){

                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search icon"
                        )
                        BasicTextField(
                            state = viewModel.searchTextFieldState,
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { focusState ->
                                    viewModel.onFocusChanged(focusState.isFocused)
                                },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            onKeyboardAction = {
                                viewModel.startSearch(viewModel.searchTextFieldState.text.toString(),page)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }

                        )



                    }
                    AnimatedVisibility(visible = viewModel.searchTextFieldState.text.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete icon",
                            tint = Color.Red,
                            modifier = Modifier.clickable {
                                viewModel.searchTextFieldState.edit { delete(0, length) }
                            }
                        )
                    }
                    AnimatedVisibility(visible = viewModel.searchTextFieldState.text.isNotBlank()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send icon",
                            tint = Color.Blue,
                            modifier = Modifier.clickable {
                                viewModel.startSearch(viewModel.searchTextFieldState.text.toString(),page)
                            }
                        )
                    }
                }

                AnimatedVisibility(visible =  searchList.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector =  Icons.AutoMirrored.Rounded.ArrowLeft,
                            contentDescription = "Left icon",
                            tint = if(page == 1) Color.Gray else Color.Blue,
                            modifier = Modifier.clickable {
                                if(page > 1) page--
                                viewModel.startSearch(viewModel.searchTextFieldState.text.toString(),page)
                            }

                        )
                        Box(
                            modifier = Modifier
                                .border(2.dp, MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ){
                            Text(page.toString())
                        }

                        Icon(
                            imageVector =  Icons.AutoMirrored.Rounded.ArrowRight,
                            contentDescription = "Left icon",
                            tint = Color.Blue,
                            modifier = Modifier.clickable {
                                page++
                                viewModel.startSearch(viewModel.searchTextFieldState.text.toString(),page)
                            }
                        )
                        Box(modifier = Modifier.weight(1f))
                    }
                }



                HorizontalDivider(
                    color = Color.Black,
                    thickness = 2.dp
                )

                when(val state = searchState){
                    ViewModelMain.ScreenState.Searching -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    is ViewModelMain.ScreenState.Empty -> {
                        GetList(state.result, click = {
                            navController.navigate(DetailsScreen(Json.encodeToString(it)))
                        })
                    }
                    is ViewModelMain.ScreenState.Content -> {
                        GetList(state.result, click = {
                            navController.navigate(DetailsScreen(Json.encodeToString(it)))
                        })
                    }
                    is ViewModelMain.ScreenState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Column {
                                Text(
                                    text = state.message,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    is ViewModelMain.ScreenState.Selected -> {
                        Column (Modifier.fillMaxSize()){
                            if(state.result["qualifier"]!!.isNotEmpty()){
                                GetSearchHelp(
                                    state.result["qualifier"]!!,
                                    click = {
                                        val text = viewModel.searchTextFieldState.text.split(" ")
                                        val baseText = text.dropLast(1).joinToString(" ")
                                        var newText = text.last().split(":")[0]+":"+it
                                        viewModel.searchTextFieldState.edit { delete(0, length)}
                                        newText = if(baseText.isNotEmpty()) "$baseText $newText" else newText
                                        viewModel.searchTextFieldState.edit { insert(0, newText)}
                                    },
                                    icon = {Icon(imageVector =  Icons.Rounded.Add, contentDescription = "Add icon")}
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = Color.Black,
                                    thickness = 2.dp
                                )
                            }
                            GetSearchHelp(
                                state.result["history"]!!,
                                click = {
                                    viewModel.searchTextFieldState.edit { delete(0, length)}
                                    viewModel.searchTextFieldState.edit { insert(0, it)}
                                        },
                                icon = {Icon(imageVector =  Icons.Rounded.History, contentDescription = "History icon")}
                            )
                        }
                    }
                }



            }
        }

    }
}

@Composable
private fun GetList(searchList: List<Repository>, click: (Repository) -> Unit){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(searchList.size) { i ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (i == 0) 8.dp else 0.dp),
                onClick = {click(searchList[i])}
            ) {
                Column (
                    modifier = Modifier.padding(8.dp),
                ){
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            modifier = Modifier.weight(1f),
                            text = searchList[i].name,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
                        Column (
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End

                        ){
                            Text(
                                formatDate(searchList[i].updatedAt),
                            )
                            Text(
                                "${searchList[i].stargazersCount} ⭐",
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min=48.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            searchList[i].description?:"",
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GetSearchHelp(searchList: List<String>, click: (String) -> Unit, icon: @Composable () -> Unit ){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(searchList.size) { i ->
            Row (
                modifier = Modifier.fillMaxWidth()
                    .clickable { click(searchList[i]) },
                verticalAlignment = Alignment.CenterVertically
            ){
                icon()
                Text(
                    text = searchList[i],
                    modifier = Modifier
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}