package com.example.geminiai.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.geminiai.R
import com.example.geminiai.model.Chat
import com.example.geminiai.ui.screen.chats.ChatListState
import com.example.geminiai.ui.util.noRippleClickable

@Composable
fun DrawerContent(
    chat: Chat,
    chatListState: ChatListState?,
    listState: LazyListState,
    navigateToNewChat: (Chat) -> Unit,
    onExistingChatClick: (Chat) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { navigateToNewChat(chat) },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DriveFileRenameOutline, contentDescription = "")
                Text(
                    text = stringResource(R.string.new_chat),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(18.dp),
                        contentDescription = ""
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.my_content))
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = ""
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.gem_bots))
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = ""
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 26.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.gemz),
                    contentDescription = ""
                )
                Text(
                    text = stringResource(R.string.storybook),
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 26.dp))

            Text(
                text = stringResource(R.string.chats),
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        chatListState?.let {
            itemsIndexed(
                items = it.chats,
                key = { _, chat -> chat.id }
            ) { _, chat ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable(onClick = { onExistingChatClick(chat) }),
                    headlineContent = {
                        Text(
                            text = chat.title,
                            maxLines = 1,
                            color = Color.Gray,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                        headlineColor = Color.White
                    )
                )
            }
        }
    }
}