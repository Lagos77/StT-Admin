package com.example.stadmin.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.stadmin.translation.presentation.TranslationLanguage
import com.example.stadmin.util.Constants

@Composable
fun BibleBookSelector(
    selectedBook: String,
    onBookSelected: (String) -> Unit,
    language: TranslationLanguage?,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val books = when (language) {
        TranslationLanguage.ES -> Constants.BIBLE_BOOKS_ES
        TranslationLanguage.PT -> Constants.BIBLE_BOOKS_PT
        TranslationLanguage.SV -> Constants.BIBLE_BOOKS_SV
        null -> Constants.BIBLE_BOOKS_EN
    }

    if (showBottomSheet) {
        SelectionBottomSheet(
            title = "Select Book",
            items = books,
            selectedItem = selectedBook,
            onItemSelected = onBookSelected,
            onDismiss = { showBottomSheet = false },
            searchable = true
        )
    }

    BasicSelectorField(
        label = "Book",
        value = selectedBook.ifBlank { "Select book" },
        onClick = { showBottomSheet = true },
        modifier = modifier
    )
}