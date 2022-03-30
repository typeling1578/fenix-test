/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.components.metrics

import android.content.Context
import android.util.Base64
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mozilla.components.browser.state.search.SearchEngine
import mozilla.components.browser.state.state.selectedOrDefaultSearchEngine
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.components.metrics.Event.PerformedSearch.SearchAccessPoint
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object MetricsUtils {
    fun createSearchEvent(
        engine: SearchEngine,
        store: BrowserStore,
        searchAccessPoint: SearchAccessPoint
    ): Event.PerformedSearch? {
        val isShortcut = engine != store.state.search.selectedOrDefaultSearchEngine
        val isCustom = engine.type == SearchEngine.Type.CUSTOM

        val engineSource =
            if (isShortcut) Event.PerformedSearch.EngineSource.Shortcut(engine, isCustom)
            else Event.PerformedSearch.EngineSource.Default(engine, isCustom)

        return when (searchAccessPoint) {
            SearchAccessPoint.SUGGESTION -> Event.PerformedSearch(
                Event.PerformedSearch.EventSource.Suggestion(
                    engineSource
                )
            )
            SearchAccessPoint.ACTION -> Event.PerformedSearch(
                Event.PerformedSearch.EventSource.Action(
                    engineSource
                )
            )
            SearchAccessPoint.WIDGET -> Event.PerformedSearch(
                Event.PerformedSearch.EventSource.Widget(
                    engineSource
                )
            )
            SearchAccessPoint.SHORTCUT -> Event.PerformedSearch(
                Event.PerformedSearch.EventSource.Shortcut(
                    engineSource
                )
            )
            SearchAccessPoint.TOPSITE -> Event.PerformedSearch(
                Event.PerformedSearch.EventSource.TopSite(
                    engineSource
                )
            )
            SearchAccessPoint.NONE -> Event.PerformedSearch(
                Event.PerformedSearch.EventSource.Other(
                    engineSource
                )
            )
        }
    }

    /**
     * Get the salt to use for hashing. This is a convenience
     * function to help with unit tests.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getHashingSalt(): String = "org.mozilla.fenix-salt"
}
