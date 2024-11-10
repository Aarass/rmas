package com.example.rmas.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rmas.R
import com.example.rmas.models.MapItem
import com.example.rmas.models.Review
import com.example.rmas.models.Tag
import com.example.rmas.models.User
import com.example.rmas.viewModels.ReviewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapItemPreview(
    item: MapItem,
    currentUser: User?,
    getTagById: (tagId: String) -> Tag?,
    reviewsViewModel: ReviewsViewModel = viewModel(),
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 0.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(0.dp)
                )

                StarRatingDisplayWithStats(4.3f, 16120)
            }
        }

        HorizontalMultiBrowseCarousel(
            state = CarouselState { item.images.count() },
            modifier = Modifier.width(412.dp).height(221.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(start = 20.dp)
        ) { i ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.images[i])
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.no_image)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(205.dp).maskClip(MaterialTheme.shapes.extraLarge)
            )
        }

        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 6.dp)) {
            item.tags.forEachIndexed { i, tagId ->
                val tag: Tag? = getTagById(tagId)
                tag?.let { tag ->
                    Spacer(modifier = Modifier.width(if (i == 0) 20.dp else 10.dp))
                    FilterChip(
                        label = {
                            Text(tag.name)
                        },
                        selected = false,
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            selectedContainerColor = MaterialTheme.colorScheme.background,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = false,
                        ),
                        onClick = {}
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        HorizontalDivider()

        var comment by rememberSaveable { mutableStateOf("") }
        var rating by rememberSaveable { mutableIntStateOf(0) }

        val reviews = remember { mutableStateListOf<Review>() }

        LaunchedEffect(item.id) {
            reviews.clear()
            reviews.addAll(reviewsViewModel.getReviewsForMapItem(item.id))

            reviews.find { review ->
                review.userId == currentUser?.uid
            }?.let { myReview ->
                rating = myReview.rating
                comment = myReview.comment ?: ""
            }
        }

        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .heightIn(0.dp, 100.dp)
                        .padding(20.dp, 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "From the owner",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 10.dp),
                    )
                    Text(
                        text = "\"${item.description}\"",
                        fontSize = 14.sp,
                    )
                }
                HorizontalDivider()

            }
            item{
                Column(
                    modifier = Modifier.padding(20.dp, 8.dp)
                ) {
                    Text(
                        text = "Rate and review",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 10.dp),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1.0f),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            ) {
                                val size = 40.dp
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(currentUser?.imageUrl)
                                        .placeholder(R.drawable.avatar)
                                        .error(R.drawable.no_image)
                                        .build(),
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(size)
                                        .clip(CircleShape)
                                )

                                StarRatingBar(
                                    modifier = Modifier.padding(start = 10.dp),
                                    rating = rating,
                                    starSize = size,
                                    starSpacing = 0.dp,
                                ) { newValue -> rating = newValue }
                            }

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 1,
                                maxLines = 3,
                                value = comment,
                                onValueChange = { comment = it }
                            )
                        }

                        val context = LocalContext.current

                        IconButton(
                            modifier = Modifier.padding(start = 20.dp).height(70.dp),
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary,
                            ),
                            onClick = {
                                currentUser?.let { user ->
                                    if (rating != 0) {
                                        reviewsViewModel.createReview(user.uid, item.id, rating, comment.ifEmpty { null }) { newReview ->
                                            reviews.indexOfFirst { review ->
                                                review.userId == currentUser.uid
                                            }.let { index ->
                                                if (index != -1) {
                                                    reviews[index] = newReview
                                                } else {
                                                    reviews.add(newReview)

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Filled.Done,
                                contentDescription = null,
                            )
                        }
                    }
                }
                HorizontalDivider()
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            val roundness = RoundedCornerShape(10.dp)
            items(reviews, key = { review -> review.userId }) { review ->
                var owner by remember { mutableStateOf(null as User?) }

                LaunchedEffect(review.userId) {
                    owner = reviewsViewModel.getUserById(review.userId)
                }

                owner?.let { user ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp).border(0.dp, MaterialTheme.colorScheme.outline, shape = roundness).clip(roundness).padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.imageUrl)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.no_image)
                                    .build(),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                            )
                            Column(
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text("${user.name} ${user.surname}")
                                StarRatingDisplay(review.rating.toFloat(), 20.dp, 2.dp)
                            }
                        }

                        review.comment?.let { comment ->
                            Text(
                                modifier = Modifier.padding(top = 6.dp),
                                text = comment
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    } }

@Composable
fun StarRatingDisplay(
    rating: Float,
    starSize: Dp = 12.dp,
    starSpacing: Dp =  0.5.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isSelected = i <= Math.round(rating)
//                val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val icon = Icons.Filled.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color.LightGray
//                    Color(0x20FFFFFF)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .width(starSize)
                    .height(starSize)
            )

            if (i < 5) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}

@Composable
fun StarRatingDisplayWithStats(
    rating: Float,
    count: Int,
) {
    val fontSize = 12.sp
    val fontWeight = FontWeight.Light
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "%.1f".format(rating), fontSize = fontSize, fontWeight = fontWeight)
        StarRatingDisplay(rating)
        Text(text = "(%,d)".format(count), fontSize = fontSize, fontWeight = fontWeight)
    }
}

@Composable
fun StarRatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    starSize: Dp = 30.dp,
    starSpacing: Dp = 2.dp,
    onRatingChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isSelected = i <= rating
            val icon = if(isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color.LightGray
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .clip(CircleShape)
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            if (rating == i) {
                                onRatingChanged(0)
                            } else {
                                onRatingChanged(i)
                            }
                        }
                    )
                    .width(starSize).height(starSize)
            )

            if (i < 5) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}

//@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
//@Composable
//fun tmp() {
//    val item = MapItem("123", "123", LatLng(0.0, 0.0), "Naslov", "Opis", listOf("1", "2"), listOf("", "", "", "", ""))
//
//    MapItemPreview(
//        item,
//        null,
//        {id -> Tag(id, "Waterfall")},
//        {id -> User(id, "Aleksandar", "Prokopovic", "0621715606", "")},
//    )
//}