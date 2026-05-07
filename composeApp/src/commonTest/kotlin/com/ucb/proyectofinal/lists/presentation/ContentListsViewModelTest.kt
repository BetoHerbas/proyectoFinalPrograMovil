package com.ucb.proyectofinal.lists.presentation

import app.cash.turbine.test
import com.ucb.proyectofinal.fakes.FakeContentListRepository
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.usecase.CreateContentListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.DeleteListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetContentListsUseCase
import com.ucb.proyectofinal.lists.presentation.effect.ContentListsEffect
import com.ucb.proyectofinal.lists.presentation.intent.ContentListsIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.ContentListsViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ContentListsViewModelTest {

    private lateinit var fakeRepo: FakeContentListRepository
    private lateinit var viewModel: ContentListsViewModel

    private fun buildViewModel() = ContentListsViewModel(
        getContentListsUseCase = GetContentListsUseCase(fakeRepo),
        createContentListUseCase = CreateContentListUseCase(fakeRepo),
        deleteListUseCase = DeleteListUseCase(fakeRepo)
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepo = FakeContentListRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads lists from repository`() = runTest {
        val existing = listOf(
            ContentList(ListId("1"), ListName.of("Movies").getOrThrow(), ContentType.MOVIE, 0)
        )
        fakeRepo.setLists(existing)
        viewModel = buildViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.lists.size)
            assertEquals("Movies", state.lists.first().name.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `CreateList with valid name adds list`() = runTest {
        viewModel = buildViewModel()
        viewModel.onIntent(ContentListsIntent.CreateList("New Series", ContentType.SERIES))

        assertEquals(1, fakeRepo.listsSnapshot.size)
    }

    @Test
    fun `CreateList with empty name emits ShowError effect`() = runTest {
        viewModel = buildViewModel()

        viewModel.effects.test {
            viewModel.onIntent(ContentListsIntent.CreateList("", ContentType.MOVIE))
            val effect = awaitItem()
            assertTrue(effect is ContentListsEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateToDetail emits NavigateToDetail effect`() = runTest {
        viewModel = buildViewModel()

        viewModel.effects.test {
            viewModel.onIntent(ContentListsIntent.NavigateToDetail("list-1", "My List"))
            val effect = awaitItem()
            assertTrue(effect is ContentListsEffect.NavigateToDetail)
            assertEquals("list-1", (effect as ContentListsEffect.NavigateToDetail).listId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
