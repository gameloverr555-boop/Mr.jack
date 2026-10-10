package com.example

import com.example.adblock.AdBlockEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrowserCoreTest {

    @Test
    fun testAdBlockEngineDetection() {
        assertTrue(AdBlockEngine.isAdOrTracker("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"))
        assertTrue(AdBlockEngine.isAdOrTracker("https://securepubads.g.doubleclick.net/gampad/ads"))
        assertTrue(AdBlockEngine.isAdOrTracker("https://c.amazon-adsystem.com/aax2/apstag.js"))
        assertTrue(AdBlockEngine.isAdOrTracker("https://analytics.google.com/analytics/v2/collect"))
        assertTrue(AdBlockEngine.isAdOrTracker("https://connect.facebook.net/en_US/fbevents.js"))

        // Legitimate site
        assertFalse(AdBlockEngine.isAdOrTracker("https://en.wikipedia.org/wiki/Main_Page"))
        assertFalse(AdBlockEngine.isAdOrTracker("https://duckduckgo.com"))
    }

    @Test
    fun testAdBlockCosmeticScript() {
        val script = AdBlockEngine.getCosmeticFilterScript()
        assertTrue(script.contains("ghostshield-adblock-css"))
        assertTrue(script.contains("display: none !important"))
    }
}
