package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.ui.test.junit4.createComposeRule
import com.ucb.proyectofinal.lists.presentation.viewmodel.ListDetailViewModel
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class ListDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository: ContentListRepository = mockk(relaxed = true)
    
    private val updateItemStatusUseCase: com.ucb.proyectofinal.lists.domain.usecase.UpdateItemStatusUseCase = mockk(relaxed = true)
    private val updateItemScoreUseCase: com.ucb.proyectofinal.lists.domain.usecase.UpdateItemScoreUseCase = mockk(relaxed = true)
    private val deleteItemUseCase: com.ucb.proyectofinal.lists.domain.usecase.DeleteItemUseCase = mockk(relaxed = true)
    private val calculateListProgressUseCase: com.ucb.proyectofinal.lists.domain.usecase.CalculateListProgressUseCase = mockk(relaxed = true)
    private val isItemInFavoritesUseCase: com.ucb.proyectofinal.lists.domain.usecase.IsItemInFavoritesUseCase = mockk(relaxed = true)

    private val viewModel = ListDetailViewModel(
        repository,
        updateItemStatusUseCase,
        updateItemScoreUseCase,
        deleteItemUseCase,
        calculateListProgressUseCase,
        isItemInFavoritesUseCase
    )

    @Test
    fun `ListDetailScreen composes without crashing`() {
        composeTestRule.setContent {
            ListDetailScreen(
                listId = "list1",
                onNavigateBack = {},
                onNavigateToItemDetail = { _, _ -> },
                onNavigateToSearch = {},
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()
        assertTrue(true)
    }
}
