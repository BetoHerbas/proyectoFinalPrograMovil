package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.fakes.FakeContentListRepository
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RateItemUseCaseTest {

    private lateinit var fakeRepo: FakeContentListRepository
    private lateinit var useCase: RateItemUseCase

    private val item = ContentItem(
        id = ItemId("item-1"),
        listId = ListId("list-1"),
        title = ItemTitle.of("Inception").getOrThrow(),
        type = ContentType.MOVIE,
        seen = true,
        rating = null
    )

    @BeforeTest
    fun setUp() {
        fakeRepo = FakeContentListRepository()
        useCase = RateItemUseCase(fakeRepo)
    }

    @Test
    fun `rating 4 is applied successfully`() = runTest {
        val rating = Rating.of(4).getOrThrow()
        val result = useCase(item, rating)

        assertTrue(result.isSuccess)
        assertEquals(4, result.getOrThrow().rating?.value)
    }

    @Test
    fun `rating 1 is applied successfully`() = runTest {
        val result = useCase(item, Rating.of(1).getOrThrow())
        assertEquals(1, result.getOrThrow().rating?.value)
    }

    @Test
    fun `rating 5 is applied successfully`() = runTest {
        val result = useCase(item, Rating.of(5).getOrThrow())
        assertEquals(5, result.getOrThrow().rating?.value)
    }

    @Test
    fun `rate fails when repository fails`() = runTest {
        fakeRepo.shouldFail = true
        assertTrue(useCase(item, Rating.of(3).getOrThrow()).isFailure)
    }
}
