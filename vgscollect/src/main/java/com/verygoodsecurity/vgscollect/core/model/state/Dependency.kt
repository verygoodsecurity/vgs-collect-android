package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.core.storage.DependencyType

/**
 *
 *
 * @param dependencyType
 * @param value
 */
data class Dependency(val dependencyType: DependencyType = DependencyType.LENGTH, val value:Int)