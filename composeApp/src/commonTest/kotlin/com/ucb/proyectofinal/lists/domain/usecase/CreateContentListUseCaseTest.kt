package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.fakes.FakeContentListRepository
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateContentListUseCaseTest {

    private lateinit var fakeRepo: FakeContentListRepository
    private lateinit var useCase: CreateContentListUseCase

    @BeforeTest
    fun setUp() {
        fakeRepo = FakeContentListRepository()
        useCase = CreateContentListUseCase(fakeRepo)
    }

    @Test
    fun `create list with valid name returns success`() = runTest {
        val result = useCase(ListName.of("My Series").getOrThrow(), ContentType.SERIES)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `created list has correct name`() = runTest {
        val name = ListName.of("Books 2025").getOrThrow()
        val result = useCase(name, ContentType.BOOK)
        assertEquals("Books 2025", result.getOrThrow().name.value)
    }

    @Test
    fun `created list has correct type`() = runTest {
        val result = useCase(ListName.of("Series 2025").getOrThrow(), ContentType.SERIES)
        assertEquals(ContentType.SERIES, result.getOrThrow().type)
    }

    @Test
    fun `create list when repo fails returns failure`() = runTest {
        fakeRepo.shouldFail = true
        val result = useCase(ListName.of("Test").getOrThrow(), ContentType.MOVIE)
        assertTrue(result.isFailure)
    }

    @Test
    fun `created list is added to repository`() = runTest {
        useCase(ListName.of("My List").getOrThrow(), ContentType.BOOK)
        assertEquals(1, fakeRepo.listsSnapshot.size)
    }
}
