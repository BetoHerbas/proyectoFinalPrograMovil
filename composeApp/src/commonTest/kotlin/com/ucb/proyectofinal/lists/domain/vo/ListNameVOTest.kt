package com.ucb.proyectofinal.lists.domain.vo

import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListNameVOTest {

    @Test
    fun `valid name creates ListName`() {
        val result = ListName.of("My Movies")
        assertTrue(result.isSuccess)
        assertEquals("My Movies", result.getOrThrow().value)
    }

    @Test
    fun `blank name fails`() {
        assertTrue(ListName.of("   ").isFailure)
    }

    @Test
    fun `empty name fails`() {
        assertTrue(ListName.of("").isFailure)
    }

    @Test
    fun `name of exactly 50 characters is valid`() {
        assertTrue(ListName.of("a".repeat(50)).isSuccess)
    }

    @Test
    fun `name of 51 characters fails`() {
        assertTrue(ListName.of("a".repeat(51)).isFailure)
    }

    @Test
    fun `name of 1 character is valid`() {
        assertTrue(ListName.of("A").isSuccess)
    }

    @Test
    fun `two ListNames with same value are equal`() {
        val n1 = ListName.of("Movies").getOrThrow()
        val n2 = ListName.of("Movies").getOrThrow()
        assertEquals(n1, n2)
    }
}
