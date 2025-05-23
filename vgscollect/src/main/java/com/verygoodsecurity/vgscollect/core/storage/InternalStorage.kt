package com.verygoodsecurity.vgscollect.core.storage

import android.content.Context
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.CardNumberContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.DateContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.SSNContent
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
import com.verygoodsecurity.vgscollect.util.extension.merge
import com.verygoodsecurity.vgscollect.util.extension.toArrayMergePolicy
import com.verygoodsecurity.vgscollect.util.extension.toFlatMap
import com.verygoodsecurity.vgscollect.util.extension.toTokenizationData
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSDateRangeSeparateSerializer
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer

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

    fun getDataForCollecting(
        request: VGSRequest,
        staticData: Map<String, Any>,
    ): Map<String, Any>? {
        val nameMappingPolicy = request.fieldNameMappingPolicy
        val arrayMergePolicy = nameMappingPolicy.toArrayMergePolicy()
        val fieldsIgnore = request.fieldsIgnore
        val fileIgnore = request.fileIgnore
        val fieldsData = with(getData(fieldsIgnore, fileIgnore)) {
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

    fun getDataForTokenization(): Map<String, Any>? {
        val items = fieldsStorage.getItems()
        return mutableMapOf(DATA_KEY to items.map { it.toTokenizationData() })
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
        fileIgnore: Boolean
    ): Map<String, String>? {
        val result = mutableListOf<Pair<String, String>>()
        getFieldsData(fieldsIgnore)?.let { result.addAll(it) } ?: return null
        getFilesData(fileIgnore)?.let { result.merge(it) } ?: return null
        return result.toMap()
    }

    private fun getFieldsData(fieldsIgnore: Boolean): MutableCollection<Pair<String, String>>? {
        return if (!fieldsIgnore) {
            val fieldsStates = fieldsStorage.getItems()
            val invalidFields = fieldsStates.filter { !it.isValid }
            if (invalidFields.isEmpty()) {
                stateToAssociatedList(fieldsStates)
            } else {
                invalidFields.forEach {
                    listener.onStorageError(VGSError.INPUT_DATA_NOT_VALID, it.fieldName)
                }
                null
            }
        } else {
            mutableListOf()
        }
    }

    private fun getFilesData(fileIgnore: Boolean): MutableCollection<Pair<String, String>>? {
        return if (!fileIgnore) {
            val filesStates = fileProvider.getAttachedFiles()
            val invalidFiles = filesStates.filter { it.size > getFileSizeLimit() }
            if (invalidFiles.isEmpty()) {
                fileStorage.getAssociatedList()
            } else {
                invalidFiles.forEach {
                    listener.onStorageError(VGSError.FILE_SIZE_OVER_LIMIT, it.name)
                }
                return null
            }
        } else {
            mutableListOf()
        }
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
                        it.serialize(
                            VGSDateRangeSeparateSerializer.Params(
                                data,
                                content.dateFormat
                            )
                        )
                    )
                }
            }
        } else {
            result.add(fieldName to data)
        }
        return result
    }
}