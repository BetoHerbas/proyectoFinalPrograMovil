package com.ucb.proyectofinal.lists.presentation

import app.cash.turbine.test
import com.ucb.proyectofinal.fakes.FakeContentListRepository
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.usecase.AddItemToListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.DeleteItemUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetContentListsUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetListItemsUseCase
import com.ucb.proyectofinal.lists.domain.usecase.RateItemUseCase
import com.ucb.proyectofinal.lists.domain.usecase.ToggleItemSeenUseCase
import com.ucb.proyectofinal.lists.presentation.effect.ListDetailEffect
import com.ucb.proyectofinal.lists.presentation.intent.ListDetailIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.ListDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ListDetailViewModelTest {

    private lateinit var fakeRepo: FakeContentListRepository
    private lateinit var viewModel: ListDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepo = FakeContentListRepository()
        viewModel = ListDetailViewModel(
            getListItemsUseCase = GetListItemsUseCase(fakeRepo),
            addItemToListUseCase = AddItemToListUseCase(fakeRepo),
            toggleItemSeenUseCase = ToggleItemSeenUseCase(fakeRepo),
            rateItemUseCase = RateItemUseCase(fakeRepo),
            deleteItemUseCase = DeleteItemUseCase(fakeRepo),
            getContentListsUseCase = GetContentListsUseCase(fakeRepo)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadDetail updates listId and listName in state`() = runTest {
        viewModel.onIntent(ListDetailIntent.LoadDetail("list-1", "My Movies"))

        assertEquals("list-1", viewModel.state.value.listId)
        assertEquals("My Movies", viewModel.state.value.listName)
    }

    @Test
    fun `LoadDetail triggers item loading from repository`() = runTest {
        val item = ContentItem(
            id = ItemId("item-1"),
            listId = ListId("list-1"),
            title = ItemTitle.of("Inception").getOrThrow(),
            type = ContentType.MOVIE,
            seen = false,
            rating = null
        )
        fakeRepo.setItems("list-1", listOf(item))

        viewModel.state.test {
            viewModel.onIntent(ListDetailIntent.LoadDetail("list-1", "Movies"))
            skipItems(1) // initial empty state
            val state = awaitItem()
            assertEquals(1, state.items.size)
            assertEquals("Inception", state.items.first().title.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ToggleSeen updates item seen status without error`() = runTest {
        val item = ContentItem(
            id = ItemId("item-1"),
            listId = ListId("list-1"),
            title = ItemTitle.of("Dune").getOrThrow(),
            type = ContentType.MOVIE,
            seen = false,
            rating = null
        )
        fakeRepo.setItems("list-1", listOf(item))
        viewModel.onIntent(ListDetailIntent.LoadDetail("list-1", "Movies"))

        viewModel.onIntent(ListDetailIntent.ToggleSeen(item))

        val updatedItem = fakeRepo.itemsSnapshot["list-1"]?.first()
        assertTrue(updatedItem?.seen == true)
    }

    @Test
    fun `AddItem with blank title emits ShowError effect`() = runTest {
        viewModel.onIntent(ListDetailIntent.LoadDetail("list-1", "Movies"))

        viewModel.effects.test {
            viewModel.onIntent(ListDetailIntent.AddItem("   ", ContentType.MOVIE))
            val effect = awaitItem()
            assertTrue(effect is ListDetailEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ShowAddDialog sets showAddDialog true in state`() {
        assertFalse(viewModel.state.value.showAddDialog)
        viewModel.onIntent(ListDetailIntent.ShowAddDialog)
        assertTrue(viewModel.state.value.showAddDialog)
    }

    @Test
    fun `HideAddDialog sets showAddDialog false in state`() {
        viewModel.onIntent(ListDetailIntent.ShowAddDialog)
        viewModel.onIntent(ListDetailIntent.HideAddDialog)
        assertFalse(viewModel.state.value.showAddDialog)
    }
}
