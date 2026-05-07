package com.ucb.proyectofinal.lists.domain.vo

import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RatingVOTest {

    @Test
    fun `rating 1 is valid`() {
        val result = Rating.of(1)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().value)
    }

    @Test
    fun `rating 5 is valid`() {
        assertTrue(Rating.of(5).isSuccess)
    }

    @Test
    fun `rating 3 is valid`() {
        assertEquals(3, Rating.of(3).getOrThrow().value)
    }

    @Test
    fun `rating 0 fails`() {
        assertTrue(Rating.of(0).isFailure)
    }

    @Test
    fun `rating 6 fails`() {
        assertTrue(Rating.of(6).isFailure)
    }

    @Test
    fun `negative rating fails`() {
        assertTrue(Rating.of(-1).isFailure)
    }

    @Test
    fun `two Ratings with same value are equal`() {
        assertEquals(Rating.of(4).getOrThrow(), Rating.of(4).getOrThrow())
    }
}
