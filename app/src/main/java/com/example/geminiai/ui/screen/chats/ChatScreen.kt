package com.example.geminiai.ui.screen.chats

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddToDrive
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider.getUriForFile
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.geminiai.R
import com.example.geminiai.model.Chat
import com.example.geminiai.model.Message
import com.example.geminiai.ui.screen.DrawerContent
import com.example.geminiai.ui.theme.Black40
import com.example.geminiai.ui.theme.Blue50
import com.example.geminiai.ui.theme.DarkGrey80
import com.example.geminiai.ui.theme.Grey40
import com.example.geminiai.ui.theme.LightGreen40
import com.example.geminiai.ui.theme.LightRed40
import com.example.geminiai.ui.theme.Orange40
import com.example.geminiai.ui.util.CustomIconWithTextButton
import com.example.geminiai.ui.util.CustomIcons
import com.example.geminiai.ui.util.FilledIconColumnButton
import com.example.geminiai.ui.util.bitmap.loadBitmapWithCorrectOrientation
import com.example.geminiai.ui.util.iconSetting
import com.example.geminiai.ui.util.noRippleClickable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatId: Long,
    title: String,
    viewModel: ChatViewModel = hiltViewModel(key = "$chatId"),
    navigateToNewChat: (Chat) -> Unit,
    onExistingChatClick: (Chat) -> Unit,
) {

    LaunchedEffect(chatId) {
        viewModel.setChatId(chatId, title)
    }

    val chatListState by viewModel.chatListState.collectAsStateWithLifecycle()
    val chat by viewModel.chat.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val messagesBlock by viewModel.messageBlock.collectAsStateWithLifecycle()
    val input by viewModel.input.collectAsStateWithLifecycle()
    val isMessageState by viewModel.isSendMessageState.collectAsStateWithLifecycle()
    val isCanceledMessage by viewModel.isCanceledMessage.collectAsStateWithLifecycle()
    val sendEnabled by viewModel.sendEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val horizontalScrollState = rememberScrollState()
    val blockSelected = remember { mutableStateOf(false) }
    val scaffoldColor = if (blockSelected.value) Black40 else Color.Black

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false,
        confirmValueChange = { true }
    )

    val scaffoldSheetState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()

    val isOpenAddResource = remember { mutableStateOf(false) }
    val isOpenDropDownBar = remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val dpCacheWindow = LazyLayoutCacheWindow(ahead = 150.dp, behind = 100.dp)
    val listState = rememberLazyListState(cacheWindow = dpCacheWindow)

    val isChatTitleDialogOpen by viewModel.isChatTitleDialogOpen.collectAsStateWithLifecycle()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { imageUri = it }
    }
    var finalBitmap: Bitmap?

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f),
                drawerContainerColor = Black40,
                drawerContentColor = Color.White,
            ) {
                DrawerContent(
                    chat = chat,
                    chatListState = chatListState,
                    listState = listState,
                    navigateToNewChat = {
                        navigateToNewChat(chat)
                        chat.title.replace("", "")
                        scope.launch { drawerState.close() }
                    },
                    onExistingChatClick = { chat ->
                        scope.launch { drawerState.close() }
                        onExistingChatClick(chat)
                    }
                )
            }
        }
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldSheetState,
            modifier = Modifier.fillMaxSize(),
            containerColor = scaffoldColor,
            sheetContainerColor = DarkGrey80,
            sheetPeekHeight = 0.dp,
            topBar = {
                ChatTopBar(
                    title = chat.title,
                    scrollBehavior = scrollBehavior,
                    color = scaffoldColor,
                    onClickOpenBottomBar = {
                        isOpenDropDownBar.value = true
                        isOpenAddResource.value = false

                        scope.launch {
                            when (sheetState.currentValue) {
                                SheetValue.Hidden -> sheetState.expand()
                                SheetValue.Expanded -> sheetState.hide()
                                else -> sheetState.hide()
                            }
                        }
                    },
                    onClickOpenDrawerMenu = {
                        scope.launch {
                            drawerState.open()
                            sheetState.hide()
                        }
                    }
                )
            },
            sheetContent = {
                if (isOpenDropDownBar.value) {
                    ChatManagementButtons(
                        onRenameChatClick = {
                            scope.launch {
                                viewModel.openChatTitleDialog()
                                blockSelected.value = false
                                sheetState.hide()
                            }
                        },
                        onShareChatClick = { exportChat(context, viewModel) },
                        onClearChatClick = {
                            scope.launch {
                                viewModel.clearMessages()
                                blockSelected.value = false
                                sheetState.hide()
                            }
                        }
                    )
                }

                if (isOpenAddResource.value) {
                    AttachResource(
                        horizontalScrollState = horizontalScrollState,
                        onImagePickerClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                            scope.launch { sheetState.hide() }
                        }
                    )
                }
            }
        ) { innerPadding ->
            val layoutDirection = LocalLayoutDirection.current

            Column(modifier = modifier.padding(innerPadding)) {
                if (messages.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.hello),
                            modifier = Modifier,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.start_question),
                            modifier = Modifier,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        messagesBlock.messageList.forEach {
                            key(it.id) {
                                BlockList(
                                    text = it.textBlock,
                                    onClick = {
                                        viewModel.selectedBlock(it)
                                        blockSelected.value = true
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    MessageList(
                        messages = messages,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                        contentPadding = innerPadding.copy(layoutDirection, bottom = 16.dp),
                        isCanceledStateMessage = isCanceledMessage,
                        onRetryClick = {
                            scope.launch {
                                finalBitmap = imageUri?.let {
                                    withContext(Dispatchers.IO) {
                                        loadBitmapWithCorrectOrientation(context, it)
                                    }
                                }

                                viewModel.retryMessage(finalBitmap)
                            }
                        },
                        onThumbClick = { },
                        onThumbDownClick = { },
                        onCopyClick = { text -> viewModel.copyToClipboard(text) },
                        onSettingsClick = { },
                    )
                }
                InputBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars)),
                    input = input,
                    imageUri = imageUri,
                    contentPadding = innerPadding.copy(
                        layoutDirection,
                        top = 0.dp,
                        bottom = 6.dp,
                        start = 10.dp,
                        end = 10.dp
                    ),
                    sendEnabled = sendEnabled,
                    onImageClicked = { imageUri = null },
                    isMessageState = isMessageState.isStateMessage,
                    onInputChanged = { viewModel.updateInput(it) },
                    onSendClick = {
                        scope.launch {
                            finalBitmap = imageUri?.let {
                                withContext(Dispatchers.IO) {
                                    loadBitmapWithCorrectOrientation(context, it)
                                }
                            }

                            viewModel.sendMessage(finalBitmap)
                            imageUri = null
                        }
                    },
                    onCancelClick = { viewModel.cancelSendMessage() },
                    onAddClick = {
                        scope.launch {
                            isOpenAddResource.value = true
                            isOpenDropDownBar.value = false

                            sheetState.expand()
                        }
                    }
                )
            }
        }

        if (isChatTitleDialogOpen) {
            ChatTitleDialog(
                initialTitle = chat.title,
                onConfirmRequest = { title -> viewModel.updateChatTitle(title) },
                onDismissRequest = viewModel::closeChatTitleDialog
            )
        }
    }
}

@Composable
fun BlockList(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
            .background(Black40)
            .noRippleClickable(onClick = { onClick() }),
        contentAlignment = Alignment.Center,
        content = {
            Text(
                text = text,
                modifier = Modifier.padding(10.dp),
                color = Color.White
            )
        }
    )
}

@Composable
private fun InputBar(
    modifier: Modifier = Modifier,
    input: String,
    contentPadding: PaddingValues,
    imageUri: Uri? = null,
    sendEnabled: Boolean,
    isMessageState: Boolean,
    onImageClicked: () -> Unit,
    onInputChanged: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onCancelClick: () -> Unit,
    onAddClick: () -> Unit
) {

    val scrollState = rememberScrollState()
    var iconClickable by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val minHeight = 54.dp
    val maxHeight = screenHeight * 0.8f

    val boxHeight by animateDpAsState(
        targetValue = if (iconClickable) maxHeight else minHeight,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    )

    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val colorState = if (isMessageState) Color.White else Color.Transparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(120.dp)
            .clip(shape)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color.Gray,
                spotColor = Color.Gray
            )
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), shape)
            .background(Black40),
    ) {
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (imageUri != null) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Start),
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .width(100.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .noRippleClickable(onImageClicked),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            modifier = Modifier.size(16.dp),
                            contentDescription = "",
                            tint = Color.LightGray
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .weight(1f)
                        .requiredHeight(boxHeight)
                        .verticalScroll(scrollState),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.ask_gemini),
                            color = Color.LightGray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
                IconButton(
                    modifier = Modifier.align(Alignment.Top),
                    onClick = { iconClickable = !iconClickable },
                    content = {
                        Icon(
                            imageVector = Icons.Default.CloseFullscreen,
                            modifier = Modifier.size(14.dp),
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomIcons(
                    imageVector = Icons.Default.Add,
                    verticalPadding = 8.dp,
                    onClick = { onAddClick() }
                )

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                CustomIcons(
                    imageVector = Icons.Default.Tune,
                    verticalPadding = 8.dp,
                    modifier = Modifier.clickable {}
                )

                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, 45.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            text = stringResource(R.string.gemini_version),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                )

                FilledIconButton(
                    onClick = {},
                    modifier = Modifier.iconSetting(),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                    content = {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )

                IconButton(
                    onClick = { onSendClick(input) },
                    modifier = Modifier
                        .size(45.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(colorState),
                    enabled = sendEnabled,
                    content = {
                        AnimatedContent(
                            targetState = isMessageState,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                        fadeOut(animationSpec = tween(300))
                            },
                        ) { state ->
                            if (!state) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.Black.copy(alpha = 0.8f))
                                        .noRippleClickable(onClick = { onCancelClick() }),
                                    contentAlignment = Alignment.Center,
                                    content = {}
                                )
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.padding(bottom = if (iconClickable) 10.dp else 0.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    color: Color,
    onClickOpenDrawerMenu: () -> Unit,
    onClickOpenBottomBar: () -> Unit
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { onClickOpenBottomBar() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Dehaze,
                contentDescription = "",
                modifier = Modifier.clickable { onClickOpenDrawerMenu() },
                tint = Color.White
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "",
                tint = Color.White
            )
        },
        expandedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight,
        modifier = modifier.padding(horizontal = 10.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = color),
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageList(
    messages: List<Message>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    isCanceledStateMessage: Boolean,
    onRetryClick: () -> Unit,
    onThumbClick: () -> Unit,
    onThumbDownClick: () -> Unit,
    onCopyClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val dpCacheWindow = LazyLayoutCacheWindow(ahead = 150.dp, behind = 100.dp)
    val state = rememberLazyListState(cacheWindow = dpCacheWindow)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val minWidth = screenWidth * 0.1f
    val maxWidthMessage = screenWidth * 0.8f
    val maxWidthMessageIsIncoming = screenWidth * 1f

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) state.scrollToItem(messages.lastIndex)
    }

    LazyColumn(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {
        items(
            items = messages,
            key = { it.timestamp }
        ) { message ->
            val backgroundColor = if (message.isIncoming) Color.Transparent else Grey40
            val arrangement = if (message.isIncoming) Arrangement.Start else Arrangement.End
            val shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
            val shapeSender = if (!message.isIncoming) shape else MaterialTheme.shapes.large

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = arrangement
                ) {
                    Surface(
                        modifier = Modifier
                            .widthIn(
                                min = minWidth,
                                max = if (message.isIncoming) maxWidthMessageIsIncoming else maxWidthMessage
                            )
                            .wrapContentHeight(),
                        color = backgroundColor,
                        shape = shapeSender,
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            if (message.isIncoming) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (message.text.isEmpty()) {
                                        AnimatedCircularProgressIndicator()
                                    } else {
                                        Icon(
                                            painter = painterResource(R.drawable.gemini),
                                            modifier = Modifier.size(24.dp),
                                            contentDescription = "",
                                            tint = Blue50
                                        )
                                        Text("Google Search", color = Color.White)
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "",
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "",
                                            tint = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                            }
                            Text(text = message.text, color = Color.White)
                        }
                    }
                }

                if (!message.isIncoming) {
                    message.image?.let { it: Bitmap ->
                        Image(
                            modifier = Modifier
                                .align(Alignment.End)
                                .widthIn(max = 300.dp)
                                .padding(vertical = 16.dp)
                                .clip(shape = RoundedCornerShape(12.dp)),
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                        )
                    }
                }
            }

            if (message.isIncoming) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isCanceledStateMessage && message == messages.last()) {
                        IconButton(
                            onClick = { onRetryClick() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    tint = Color.Gray,
                                    contentDescription = ""
                                )
                            }
                        )
                        IconButton(
                            onClick = { },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                            content = {
                                Icon(
                                    imageVector = Icons.Outlined.Flag,
                                    tint = Color.Gray,
                                    contentDescription = ""
                                )
                            }
                        )
                    } else {
                        IconButton(
                            onClick = { onThumbClick() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ThumbUpOffAlt,
                                    tint = Color.Gray,
                                    contentDescription = ""
                                )
                            }
                        )
                        IconButton(
                            onClick = { onThumbDownClick() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ThumbDownOffAlt,
                                    tint = Color.Gray,
                                    contentDescription = ""
                                )
                            }
                        )
                        IconButton(
                            onClick = { onCopyClick(message.text) },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.CopyAll,
                                    tint = Color.Gray,
                                    contentDescription = ""
                                )
                            }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { onSettingsClick() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    tint = Color.Gray,
                                    contentDescription = ""
                                )
                            }
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = "Gemini can make mistakes. Check answers. Learn more",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ChatManagementButtons(
    onRenameChatClick: () -> Unit,
    onShareChatClick: () -> Unit,
    onClearChatClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CustomIconWithTextButton(
            imageVector = Icons.Default.Share,
            text = stringResource(R.string.share_a_chat),
            onClick = onShareChatClick
        )
        CustomIconWithTextButton(
            imageVector = Icons.Default.Edit,
            text = stringResource(R.string.rename_the_chat),
            onClick = onRenameChatClick
        )
        CustomIconWithTextButton(
            imageVector = Icons.Default.RestoreFromTrash,
            text = stringResource(R.string.clear_the_chat),
            onClick = onClearChatClick
        )
    }
}

@Composable
private fun AttachResource(
    horizontalScrollState: ScrollState,
    onImagePickerClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconColumnButton(
            icon = Icons.Default.PhotoCamera,
            text = stringResource(R.string.open_the_camera),
            onClick = {}
        )
        FilledIconColumnButton(
            icon = Icons.Default.Image,
            text = stringResource(R.string.open_a_gallery),
            onClick = onImagePickerClick
        )
        FilledIconColumnButton(
            icon = Icons.Default.AttachFile,
            text = stringResource(R.string.attach_file),
            onClick = {}
        )
        FilledIconColumnButton(
            icon = Icons.Default.AddToDrive,
            text = stringResource(R.string.disk),
            onClick = {}
        )
    }
}

@Composable
fun AnimatedCircularProgressIndicator() {
    val colors = listOf(
        Blue50,
        Orange40,
        LightRed40,
        LightGreen40
    )

    val infiniteTransition = rememberInfiniteTransition(label = "colorTransition")

    val color by infiniteTransition.animateColor(
        initialValue = colors.first(),
        targetValue = colors.last(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorAnimation"
    )

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            strokeWidth = 2.dp,
            color = color
        )
        Icon(
            painter = painterResource(R.drawable.gemini),
            modifier = Modifier.size(24.dp),
            contentDescription = "",
            tint = Blue50
        )
    }
}

private fun PaddingValues.copy(
    layoutDirection: LayoutDirection,
    start: Dp? = null,
    top: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null,
) = PaddingValues(
    start = start ?: calculateStartPadding(layoutDirection),
    top = top ?: calculateTopPadding(),
    end = end ?: calculateEndPadding(layoutDirection),
    bottom = bottom ?: calculateBottomPadding(),
)

private fun exportChat(context: Context, chatViewModel: ChatViewModel) {
    try {
        val (fileName, fileContent) = chatViewModel.exportChat()
        val file = File(context.getExternalFilesDir(null), fileName)
        file.writeText(fileContent)
        val uri = getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/markdown"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Chat Export").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val resInfo =
            context.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        resInfo.forEach { res ->
            context.grantUriPermission(
                res.activityInfo.packageName,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        context.startActivity(chooser)
    } catch (e: Exception) {
        Log.e("ChatExport", "Failed to export chat", e)
        Toast.makeText(context, "Failed to export chat", Toast.LENGTH_SHORT).show()
    }
}
