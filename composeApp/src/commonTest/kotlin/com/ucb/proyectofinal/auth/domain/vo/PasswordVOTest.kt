package com.ucb.proyectofinal.auth.domain.vo

import com.ucb.proyectofinal.auth.domain.model.vo.Password
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PasswordVOTest {

    @Test
    fun `password with exactly 6 characters is valid`() {
        assertTrue(Password.of("abc123").isSuccess)
    }

    @Test
    fun `password longer than 6 characters is valid`() {
        assertTrue(Password.of("securePassword!").isSuccess)
    }

    @Test
    fun `password with 5 characters fails`() {
        val result = Password.of("abc12")
        assertTrue(result.isFailure)
    }

    @Test
    fun `empty password fails`() {
        assertTrue(Password.of("").isFailure)
    }

    @Test
    fun `password of 1 character fails`() {
        assertTrue(Password.of("a").isFailure)
    }

    @Test
    fun `password value is preserved`() {
        val password = Password.of("myPass1").getOrThrow()
        assertEquals("myPass1", password.value)
    }

    @Test
    fun `two passwords with same value are equal`() {
        val p1 = Password.of("samePass").getOrThrow()
        val p2 = Password.of("samePass").getOrThrow()
        assertEquals(p1, p2)
    }
}
