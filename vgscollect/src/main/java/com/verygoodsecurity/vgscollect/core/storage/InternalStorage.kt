package com.verygoodsecurity.vgscollect.core.storage

import android.content.Context
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.CardNumberContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.DateContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.SSNContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.content.field.FieldStateContractor
import com.verygoodsecurity.vgscollect.core.storage.content.field.TemporaryFieldsStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.FileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageErrorListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.util.extension.allowParseArrays
import com.verygoodsecurity.vgscollect.util.extension.isArraysIgnored
import com.verygoodsecurity.vgscollect.util.extension.merge
import com.verygoodsecurity.vgscollect.util.extension.toFlatMap
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSDateRangeSeparateSerializer
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer

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

    fun getData(
        fieldNameMappingPolicy: VGSCollectFieldNameMappingPolicy,
        fieldsIgnore: Boolean,
        fileIgnore: Boolean
    ): MutableMap<String, Any> {
        return if (!fieldNameMappingPolicy.isArraysIgnored()) {
            getAssociatedList(fieldsIgnore, fileIgnore)
                .toFlatMap(
                    fieldNameMappingPolicy.allowParseArrays()
                ).structuredData
        } else {
            getAssociatedList(fieldsIgnore, fileIgnore).toMap().toMutableMap()
        }
    }

    fun getFieldsStates(): MutableCollection<VGSFieldState> = fieldsStorage.getItems()

    fun getAssociatedList(
        fieldsIgnore: Boolean = false, fileIgnore: Boolean = false
    ): MutableCollection<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()

        if (fieldsIgnore.not()) {
            list.addAll(stateToAssociatedList(fieldsStorage.getItems()))
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

    fun performSubscription(view: InputFieldView) {
        fieldsDependencyDispatcher.addDependencyListener(
            view.getFieldType(),
            view.statePreparer.getDependencyListener()
        )
        view.addStateListener(emitter.performSubscription())
    }

    fun unsubscribe(view: InputFieldView) {
        view.statePreparer.unsubscribe()
        fieldsStorage.remove(view.statePreparer.getView().id)
    }

    fun getFileSizeLimit(): Int {
        return 19 * 1024 * 1024
    }

    private fun stateToAssociatedList(items: MutableCollection<VGSFieldState>): MutableCollection<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        items.filter { state -> state.isNotNullOrEmpty() }.forEach { state ->
            with(state.content!!) {
                when (this) {
                    is CardNumberContent -> result.add(state.fieldName!! to (rawData ?: data!!))
                    is SSNContent -> result.add(state.fieldName!! to (rawData ?: data!!))
                    is DateContent -> {
                        result.addAll(handleDateContent(state.fieldName!!, this))
                    }
                    else -> result.add(state.fieldName!! to data!!)
                }
            }
        }
        return result
    }

    private fun handleDateContent(
        fieldName: String,
        content: DateContent
    ): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        val data = (content.rawData ?: content.data!!)
        if (content.serializers != null) {
            content.serializers?.forEach {
                if (it is VGSExpDateSeparateSerializer) {
                    result.addAll(
                        it.serialize(VGSExpDateSeparateSerializer.Params(data, content.dateFormat))
                    )
                }
                if (it is VGSDateRangeSeparateSerializer) {
                    result.addAll(
                        it.serialize(VGSDateRangeSeparateSerializer.Params(data, content.dateFormat))
                    )
                }
            }
        } else {
            result.add(fieldName to data)
        }
        return result
    }
}