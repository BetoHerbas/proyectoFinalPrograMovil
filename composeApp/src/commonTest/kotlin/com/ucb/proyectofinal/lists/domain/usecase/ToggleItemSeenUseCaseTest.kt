package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.fakes.FakeContentListRepository
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ToggleItemSeenUseCaseTest {

    private lateinit var fakeRepo: FakeContentListRepository
    private lateinit var useCase: ToggleItemSeenUseCase

    private val unseen = ContentItem(
        id = ItemId("item-1"),
        listId = ListId("list-1"),
        title = ItemTitle.of("Breaking Bad").getOrThrow(),
        type = ContentType.SERIES,
        seen = false,
        rating = null
    )

    private val seen = ContentItem(
        id = ItemId("item-2"),
        listId = ListId("list-1"),
        title = ItemTitle.of("Lost").getOrThrow(),
        type = ContentType.SERIES,
        seen = true,
        rating = null
    )

    @BeforeTest
    fun setUp() {
        fakeRepo = FakeContentListRepository()
        useCase = ToggleItemSeenUseCase(fakeRepo)
    }

    @Test
    fun `unseen item becomes seen after toggle`() = runTest {
        val result = useCase(unseen)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().seen)
    }

    @Test
    fun `seen item becomes unseen after toggle`() = runTest {
        val result = useCase(seen)
        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow().seen)
    }

    @Test
    fun `toggle fails when repository fails`() = runTest {
        fakeRepo.shouldFail = true
        assertTrue(useCase(unseen).isFailure)
    }
}
