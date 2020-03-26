package com.verygoodsecurity.vgscollect.core.storage

import android.content.Context
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.content.file.FileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.util.merge
import com.verygoodsecurity.vgscollect.util.toAssociatedList
import com.verygoodsecurity.vgscollect.view.InputFieldView

internal class InternalStorage(
    context: Context,
    fieldsDependencyDispatcher: Notifier? = null
) {

    private val fileProvider: VGSFileProvider
    private val fileStorage: FileStorage
    private val fieldsStorage:VgsStore<Int, VGSFieldState>
    private val emitter: IStateEmitter

    init {
        with(TemporaryFileStorage(context)) {
            fileProvider = this
            fileStorage = this
        }

        with(TemporaryFieldsStorage()) {
            attachFieldDependencyObserver(fieldsDependencyDispatcher)

            fieldsStorage = this
            emitter = this
        }
    }

    fun getFileProvider() = fileProvider
    fun getAttachedFiles() =  fileProvider.getAttachedFiles()
    fun getFileStorage() = fileStorage
    fun getFieldsStorage() = fieldsStorage

    fun getFieldsStates():MutableCollection<VGSFieldState> = fieldsStorage.getItems()

    fun getAssociatedList(
        fieldsIgnore: Boolean = false, fileIgnore: Boolean = false
    ):MutableCollection<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        if(fieldsIgnore.not()) {
            list.addAll(fieldsStorage.getItems().toAssociatedList())
        }
        if(fileIgnore.not()) {
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
        view?.addStateListener(emitter.performSubscription())
    }

    fun getFileSizeLimit(): Int {
        return 19 * 1024 * 1024
    }
}