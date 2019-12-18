package com.verygoodsecurity.vgscollect.storage

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.core.storage.Notifier
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class NotifierTest {

    @Test
    fun cvc_3_length_dependency() {
        val listener = mock(DependencyListener::class.java)
        val listenerCVC = InputFieldView.DependencyNotifier(listener)

        val notifier = Notifier()
        notifier.addDependencyListener(FieldType.CVC, listenerCVC)

        val testDependency = Dependency(DependencyType.LENGTH, 3)
        notifier.onDependencyDetected(FieldType.CVC, testDependency)

        Mockito.verify(listener).dispatchDependencySetting(Dependency(DependencyType.LENGTH, 3))
    }
}