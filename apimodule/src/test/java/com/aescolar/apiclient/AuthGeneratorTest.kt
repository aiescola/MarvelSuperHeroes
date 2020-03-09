package com.aescolar.apiclient

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AuthGeneratorTest {

    @Test
    fun `known keys generate correct hash`() {
        val authHashGenerator = AuthHashGenerator()
        val hash = authHashGenerator.generateHash(TIME_STAMP, PUBLIC_KEY, PRIVATE_KEY)

        assertEquals(EXPECTED_HASH, hash)
    }

    companion object {
        private const val PUBLIC_KEY = "1234"
        private const val PRIVATE_KEY = "abcd"
        private const val TIME_STAMP = "1"
        private const val EXPECTED_HASH = "ffd275c5130566a2916217b101f26150"
    }
}
