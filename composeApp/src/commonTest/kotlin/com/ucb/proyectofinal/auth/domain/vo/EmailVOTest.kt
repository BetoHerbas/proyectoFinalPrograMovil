package com.ucb.proyectofinal.auth.domain.vo

import com.ucb.proyectofinal.auth.domain.model.vo.Email
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EmailVOTest {

    @Test
    fun `valid email creates Email successfully`() {
        val result = Email.of("user@example.com")
        assertTrue(result.isSuccess)
        assertEquals("user@example.com", result.getOrThrow().value)
    }

    @Test
    fun `email with subdomain is valid`() {
        assertTrue(Email.of("user@mail.example.com").isSuccess)
    }

    @Test
    fun `invalid email without at-sign fails`() {
        assertTrue(Email.of("notanemail").isFailure)
    }

    @Test
    fun `empty email fails`() {
        assertTrue(Email.of("").isFailure)
    }

    @Test
    fun `email without domain extension fails`() {
        assertTrue(Email.of("user@domain").isFailure)
    }

    @Test
    fun `email equals another email with same value`() {
        val e1 = Email.of("a@b.com").getOrThrow()
        val e2 = Email.of("a@b.com").getOrThrow()
        assertEquals(e1, e2)
    }

    @Test
    fun `failure contains exception message`() {
        val result = Email.of("bad")
        assertNotNull(result.exceptionOrNull())
    }
}
