package com.verygoodsecurity.vgscollect.core.storage

import android.content.Context
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.content.field.FieldStateContractor
import com.verygoodsecurity.vgscollect.core.storage.content.field.TemporaryFieldsStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.FileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageErrorListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.util.merge
import com.verygoodsecurity.vgscollect.util.toAssociatedList
import com.verygoodsecurity.vgscollect.view.InputFieldView

/** @suppress */
internal class InternalStorage(
    context: Context,
    private val errorListener: StorageErrorListener? = null
) {
    private val fieldsDependencyDispatcher: DependencyDispatcher

    private val fileProvider: VGSFileProvider
    private val fileStorage: FileStorage
    private val fieldsStorage: VgsStore<Int, VGSFieldState>
    private val emitter: IStateEmitter

    init {
        fieldsDependencyDispatcher = Notifier()

        with(TemporaryFileStorage(context, errorListener)) {
            fileProvider = this
            fileStorage = this
        }

        val fieldStateContractor = FieldStateContractor(context)
        with(TemporaryFieldsStorage(fieldStateContractor)) {
            attachFieldDependencyObserver(fieldsDependencyDispatcher)

            fieldsStorage = this
            emitter = this
        }
    }

    fun getFileProvider() = fileProvider
    fun getAttachedFiles() = fileProvider.getAttachedFiles()
    fun getFileStorage() = fileStorage
    fun getFieldsStorage() = fieldsStorage

    fun getFieldsStates(): MutableCollection<VGSFieldState> = fieldsStorage.getItems()

    fun getAssociatedList(
        fieldsIgnore: Boolean = false, fileIgnore: Boolean = false
    ): MutableCollection<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()

        if (fieldsIgnore.not()) {
            list.addAll(fieldsStorage.getItems().toAssociatedList())
        }

        if (fileIgnore.not()) {
            list.merge(fileStorage.getAssociatedList())
        }

        return list
    }

    fun clear() {
        fileStorage.clear()
        fieldsStorage.clear()
    }

    fun attachStateChangeListener(fieldStateListener: OnFieldStateChangeListener?) {
        emitter.attachStateChangeListener(fieldStateListener)
    }

    fun performSubscription(view: InputFieldView?) {
        view?.let {
            fieldsDependencyDispatcher.addDependencyListener(
                it.getFieldType(),
                it.statePreparer.getDependencyListener()
            )
            it.addStateListener(emitter.performSubscription())
        }
    }

    fun unsubscribe(view: InputFieldView?) {
        view?.let {
            it.statePreparer.unsubscribe()
            fieldsStorage.remove(it.statePreparer.getView().id)
        }
    }

    fun getFileSizeLimit(): Int {
        return 19 * 1024 * 1024
    }
}