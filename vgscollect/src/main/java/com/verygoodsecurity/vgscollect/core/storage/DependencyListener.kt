package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.Dependency

/**
 *
 */
interface DependencyListener {

    /**
     *
     * @param dependency
     */
    fun dispatchDependencySetting(dependency: Dependency)
}