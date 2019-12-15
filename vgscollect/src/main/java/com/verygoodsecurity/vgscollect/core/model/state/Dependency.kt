package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.core.storage.DependencyType

data class Dependency(val dependencyType: DependencyType = DependencyType.LENGTH, val value:Int)