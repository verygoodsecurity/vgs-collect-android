package com.verygoodsecurity.vgscollect.core.storage

import android.content.Context
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.content.field.FieldStateContractor
import com.verygoodsecurity.vgscollect.core.storage.content.field.TemporaryFieldsStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.FileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.util.extension.DATA_KEY
import com.verygoodsecurity.vgscollect.util.extension.allowParseArrays
import com.verygoodsecurity.vgscollect.util.extension.deepMerge
import com.verygoodsecurity.vgscollect.util.extension.isArraysIgnored
import com.verygoodsecurity.vgscollect.util.extension.mapToTokenizationData
import com.verygoodsecurity.vgscollect.util.extension.toArrayMergePolicy
import com.verygoodsecurity.vgscollect.util.extension.toFlatMap
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState

/** @suppress */
internal class InternalStorage(
    context: Context,
    val listener: StorageListener
) {

    val fileProvider: VGSFileProvider

    val fileStorage: FileStorage

    val fieldsStorage: VgsStore<Int, VGSFieldState>

    private val emitter: IStateEmitter

    private val fieldsDependencyDispatcher = Notifier()

    init {

        with(TemporaryFileStorage(context, listener)) {
            fileProvider = this
            fileStorage = this
        }

        with(TemporaryFieldsStorage(FieldStateContractor())) {
            attachFieldDependencyObserver(fieldsDependencyDispatcher)
            fieldsStorage = this
            emitter = this
        }
    }

    // TODO: Rename
    fun getDataForCollecting(
        request: VGSBaseRequest,
        staticData: Map<String, Any>,
        fieldsStates: List<BaseFieldState>? = null
    ): Map<String, Any>? {
        val nameMappingPolicy = request.fieldNameMappingPolicy
        val arrayMergePolicy = nameMappingPolicy.toArrayMergePolicy()
        val fieldsIgnore = request.fieldsIgnore
        val fileIgnore = request.fileIgnore
        val fieldsData = with(getData(fieldsIgnore, fileIgnore, fieldsStates, request.upstream)) {
            if (nameMappingPolicy.isArraysIgnored()) {
                this
            } else {
                this?.toFlatMap(nameMappingPolicy.allowParseArrays())?.structuredData
            }
        }

        return fieldsData?.let {
            staticData
                .toMutableMap()
                .deepMerge(request.customData, arrayMergePolicy)
                .toMutableMap()
                .deepMerge(it, arrayMergePolicy)
        }
    }

    fun getDataForTokenization(
        fieldsIgnore: Boolean,
        fieldsStates: List<BaseFieldState>? = null,
        upstream: VGSAnalyticsUpstream = VGSAnalyticsUpstream.CUSTOM
    ): Map<String, Any>? {
        if (getFieldsData(fieldsIgnore, fieldsStates, upstream) == null) {
            return null
        }
        val data = fieldsStates?.mapToTokenizationData() ?: fieldsStorage.getItems().mapToTokenizationData()
        return mutableMapOf(DATA_KEY to data)
    }

    fun getFieldsStates(): MutableCollection<VGSFieldState> = fieldsStorage.getItems()

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

    private fun getData(
        fieldsIgnore: Boolean,
        fileIgnore: Boolean,
        fieldsStates: List<BaseFieldState>?,
        upstream: VGSAnalyticsUpstream
    ): Map<String, String>? {
        val result = mutableMapOf<String, String>()
        getFilesData(fileIgnore, upstream)?.let { result.putAll(it) } ?: return null
        getFieldsData(fieldsIgnore, fieldsStates, upstream)?.let { result.putAll(it) } ?: return null
        return result
    }

    private fun getFieldsData(
        fieldsIgnore: Boolean,
        fieldsStates: List<BaseFieldState>?,
        upstream: VGSAnalyticsUpstream
    ): Map<String, String>? {
        return if (!fieldsIgnore) {
            // Try to process passed states(Compose) and if no passed check internal storage
            val states = fieldsStates?.mapToStorageFieldState() ?: fieldsStorage.getItems().mapToStorageFieldState()
            val invalidFields = states.filter { !it.isValid }
            if (invalidFields.isEmpty()) {
                states.associate { it.fieldName to it.data }
            } else {
                invalidFields.forEach {
                    listener.onStorageError(VGSError.INPUT_DATA_NOT_VALID, upstream, it.fieldName)
                }
                null
            }
        } else {
            emptyMap()
        }
    }

    private fun getFilesData(fileIgnore: Boolean, upstream: VGSAnalyticsUpstream): Map<String, String>? {
        return if (!fileIgnore) {
            val filesStates = fileProvider.getAttachedFiles()
            val invalidFiles = filesStates.filter { it.size > getFileSizeLimit() }
            if (invalidFiles.isEmpty()) {
                fileStorage.getAssociatedList().toMap()
            } else {
                invalidFiles.forEach {
                    listener.onStorageError(VGSError.FILE_SIZE_OVER_LIMIT, upstream, it.name)
                }
                null
            }
        } else {
            emptyMap()
        }
    }
}