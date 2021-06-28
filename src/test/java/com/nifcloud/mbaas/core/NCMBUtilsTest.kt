package com.nifcloud.mbaas.core

import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NCMBUtilsTest {
    @Test
    fun example1() {
        val example = Mockito.mock(NCMBUtils::class.java)

        Mockito.`when`(example.getId()).thenReturn(100)
        Mockito.`when`(example.getUrl(100))
            .thenReturn("https://codechacha.com")

        assertEquals(100, example.getId())
        assertEquals(
            "https://codechacha.com",
            example.getUrl(example.getId())
        )
    }
}
