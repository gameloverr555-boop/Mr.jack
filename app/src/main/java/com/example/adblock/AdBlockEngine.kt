package com.example.adblock

import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream
import java.net.URI
import java.util.Locale

object AdBlockEngine {

    private val blockedHostSuffixes = hashSetOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adclick.g.doubleclick.net",
        "taboola.com",
        "outbrain.com",
        "criteo.com",
        "criteo.net",
        "amazon-adsystem.com",
        "rubiconproject.com",
        "pubmatic.com",
        "openx.net",
        "adnxs.com",
        "appnexus.com",
        "smartadserver.com",
        "scorecardresearch.com",
        "quantserve.com",
        "moatads.com",
        "casalemedia.com",
        "contextweb.com",
        "bidswitch.net",
        "sovrn.com",
        "media.net",
        "inmobi.com",
        "adroll.com",
        "chartbeat.com",
        "hotjar.com",
        "crazyegg.com",
        "segment.io",
        "mixpanel.com",
        "optimizely.com",
        "amplitude.com",
        "fullstory.com",
        "mouseflow.com",
        "yieldmo.com",
        "sharethrough.com",
        "lijit.com",
        "revcontent.com",
        "mgid.com",
        "popads.net",
        "popcash.net",
        "propellerads.com",
        "exoclick.com",
        "juicyads.com",
        "zergnet.com",
        "adnxs-simple.com",
        "ad-delivery.net",
        "adcolony.com",
        "unityads.unity3d.com",
        "adsafeprotected.com",
        "innovid.com",
        "advertising.com",
        "adtechus.com",
        "trafficjunky.com",
        "ero-advertising.com",
        "imrworldwide.com",
        "newrelic.com",
        "bugsnag.com",
        "clarity.ms",
        "connect.facebook.net"
    )

    private val trackingKeywords = listOf(
        "/ads/", "/ad/", "/advert/", "google-analytics.com", "analytics.google.com",
        "facebook.com/tr/", "connect.facebook.net", "fbevents.js",
        "pixel.wp.com", "ads-twitter.com", "static.ads-twitter.com",
        "tr.snapchat.com", "analytics.tiktok.com", "stats.wp.com"
    )

    fun isAdOrTracker(url: String): Boolean {
        val lowerUrl = url.lowercase(Locale.ROOT)

        // Check common path keywords
        for (kw in trackingKeywords) {
            if (lowerUrl.contains(kw)) return true
        }

        // Check host
        val host = try {
            val uri = URI(url)
            uri.host?.lowercase(Locale.ROOT) ?: return false
        } catch (_: Exception) {
            return false
        }

        for (blockedSuffix in blockedHostSuffixes) {
            if (host == blockedSuffix || host.endsWith(".$blockedSuffix")) {
                return true
            }
        }

        return false
    }

    fun isTrackerOnly(url: String): Boolean {
        val lowerUrl = url.lowercase(Locale.ROOT)
        return lowerUrl.contains("analytics") ||
                lowerUrl.contains("telemetry") ||
                lowerUrl.contains("clarity.ms") ||
                lowerUrl.contains("scorecardresearch") ||
                lowerUrl.contains("fbevents.js") ||
                lowerUrl.contains("hotjar") ||
                lowerUrl.contains("mixpanel")
    }

    fun createBlockedResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    fun getCosmeticFilterScript(): String {
        return """
            (function() {
                var css = '.ad, .ads, .advert, .advertisement, .ad-banner, .ad-unit, .adsbygoogle, ' +
                          '[id^="google_ads_"], [id^="ad-"], [class*="ad-container"], ' +
                          '[class*="sponsored-post"], [data-ad-client], [aria-label="advertisement"], ' +
                          '.taboola-placeholder, .outbrain_widget, #carbonads, .banner-ad, ' +
                          '.trc_rbox_outer, .ad-wrapper, .ad-slot, ins.adsbygoogle { ' +
                          'display: none !important; visibility: hidden !important; ' +
                          'height: 0 !important; max-height: 0 !important; opacity: 0 !important; ' +
                          'pointer-events: none !important; }';
                var head = document.head || document.getElementsByTagName('head')[0];
                if (head && !document.getElementById('ghostshield-adblock-css')) {
                    var style = document.createElement('style');
                    style.type = 'text/css';
                    style.id = 'ghostshield-adblock-css';
                    style.appendChild(document.createTextNode(css));
                    head.appendChild(style);
                }
            })();
        """.trimIndent()
    }
}
