package com.ucb.proyectofinal.lists.presentation.viewmodel

import app.cash.turbine.test
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.ItemDetail
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import com.ucb.proyectofinal.lists.presentation.intent.ItemDetailIntent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailViewModelTest {

    private lateinit var viewModel: ItemDetailViewModel
    private val repository: ContentListRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ItemDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadDetail intent updates state with item detail`() = runTest {
        val fakeTitle = "Test Movie"
        val fakeMovie = ItemDetail.Movie(
            id = ItemId("1"),
            title = ItemTitle.of(fakeTitle).getOrThrow(),
            description = "A great test movie.",
            imageUrl = null,
            rating = 8.5,
            totalReviews = 100,
            tags = listOf("Action"),
            parentsGuide = null,
            cast = emptyList(),
            reviews = emptyList(),
            director = "John Doe",
            duration = "120m",
            genres = listOf("Action"),
            year = "2025"
        )
        
        coEvery { repository.getItemDetails(ContentType.MOVIE, fakeTitle) } returns Result.success(fakeMovie)

        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            
            viewModel.onIntent(ItemDetailIntent.LoadDetail(fakeTitle, "MOVIE"))
            
            // UnconfinedTestDispatcher may skip intermediate loading state in turbine, 
            // but we will definitely get the loaded state.
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertNotNull(loadedState.item)
            assertEquals(fakeTitle, loadedState.item?.title?.value)
        }
    }
    
    @Test
    fun `LoadDetail intent handles failure gracefully`() = runTest {
        val fakeTitle = "Error Movie"
        coEvery { repository.getItemDetails(ContentType.MOVIE, fakeTitle) } returns Result.failure(Exception("Not found"))

        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            
            viewModel.onIntent(ItemDetailIntent.LoadDetail(fakeTitle, "MOVIE"))
            
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(null, loadedState.item)
        }
    }
}
