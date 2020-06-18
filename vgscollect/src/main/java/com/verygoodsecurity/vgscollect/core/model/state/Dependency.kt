package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.core.storage.DependencyType

/** @suppress */
data class Dependency(val dependencyType: DependencyType = DependencyType.RANGE, val value:Any)