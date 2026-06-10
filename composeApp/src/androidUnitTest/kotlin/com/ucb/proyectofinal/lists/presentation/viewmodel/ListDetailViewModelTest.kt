package com.ucb.proyectofinal.lists.presentation.viewmodel

import app.cash.turbine.test
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ListEntry
import com.ucb.proyectofinal.lists.domain.model.ListType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import com.ucb.proyectofinal.lists.presentation.intent.ListDetailIntent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ListDetailViewModelTest {

    private lateinit var viewModel: ListDetailViewModel
    private val repository: ContentListRepository = mockk(relaxed = true)
    
    // We mock the update tracking and progress
    private val updateItemStatusUseCase: com.ucb.proyectofinal.lists.domain.usecase.UpdateItemStatusUseCase = mockk(relaxed = true)
    private val updateItemScoreUseCase: com.ucb.proyectofinal.lists.domain.usecase.UpdateItemScoreUseCase = mockk(relaxed = true)
    private val deleteItemUseCase: com.ucb.proyectofinal.lists.domain.usecase.DeleteItemUseCase = mockk(relaxed = true)
    private val calculateListProgressUseCase: com.ucb.proyectofinal.lists.domain.usecase.CalculateListProgressUseCase = mockk(relaxed = true)
    private val isItemInFavoritesUseCase: com.ucb.proyectofinal.lists.domain.usecase.IsItemInFavoritesUseCase = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ListDetailViewModel(
            repository,
            updateItemStatusUseCase,
            updateItemScoreUseCase,
            deleteItemUseCase,
            calculateListProgressUseCase,
            isItemInFavoritesUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadList intent loads list and calculates progress`() = runTest {
        val fakeList = ContentList(
            id = ListId("list1"),
            name = ListName.of("My Watchlist").getOrThrow(),
            description = "Test list",
            isPublic = true,
            type = ListType.MOVIE,
            creatorId = "user1",
            items = listOf(
                ListEntry(
                    itemId = ItemId("item1"),
                    title = ItemTitle.of("Movie 1").getOrThrow(),
                    sourceId = "src1",
                    status = com.ucb.proyectofinal.lists.domain.model.ItemStatus.COMPLETED
                ),
                ListEntry(
                    itemId = ItemId("item2"),
                    title = ItemTitle.of("Movie 2").getOrThrow(),
                    sourceId = "src2",
                    status = com.ucb.proyectofinal.lists.domain.model.ItemStatus.PENDING
                )
            )
        )
        
        coEvery { repository.getListById("list1") } returns flowOf(fakeList)
        coEvery { calculateListProgressUseCase(fakeList) } returns 50f
        coEvery { isItemInFavoritesUseCase(any()) } returns false

        viewModel.state.test {
            val initialState = awaitItem()
            
            viewModel.onIntent(ListDetailIntent.LoadList("list1"))
            
            val loadedState = awaitItem()
            assertEquals(fakeList.name.value, loadedState.list?.name?.value)
            assertEquals(50f, loadedState.progress)
            assertEquals(2, loadedState.items.size)
        }
    }
}
