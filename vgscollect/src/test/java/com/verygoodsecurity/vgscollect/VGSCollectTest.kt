package com.verygoodsecurity.vgscollect

import android.Manifest
import android.app.Activity
import android.content.Intent
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorageImpl
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.storage.InternalStorage
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.widget.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@Ignore
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class VGSCollectTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var collect: VGSCollect

    private var testViewCounter = 0

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        collect = VGSCollect(activity, "tnts")

        val activityShadow = Shadows.shadowOf(activity)
        activityShadow.grantPermissions(
            Manifest.permission.INTERNET
        )
    }

    @Test
    fun test_add_response_listener() {
        applyResponseListener()
        applyResponseListener()

        assertEquals(2, collect.getResponseListeners().size)
    }

    @Test
    fun test_add_state_change_listener() {
        val storage = applyStorage()

        applyStateChangeListener()
        applyStateChangeListener()

        Mockito.verify(storage, Mockito.times(2)).attachStateChangeListener(any())
    }

    @Test
    fun test_bind_view() {
        val view = applyEditText(FieldType.INFO)

        Mockito.verify(view).getFieldType()
        Mockito.verify(view).getFieldName()
        Mockito.verify(view).addStateListener(any())
    }

    @Test
    fun test_on_destroy() {
        applyResponseListener()

        val storage = applyStorage()

        collect.onDestroy()

        assertEquals(0, collect.getResponseListeners().size)
        Mockito.verify(storage).clear()
    }

    @Test
    fun test_get_all_states() {
        applyEditText(FieldType.CVC)
        applyEditText(FieldType.CARD_NUMBER)

        assertEquals(2, collect.getAllStates().size)
    }

    @Test
    fun test_get_all_states_4_fields() {
        applyEditText(FieldType.CVC)
        applyEditText(FieldType.CARD_NUMBER)
        applyEditText(FieldType.CARD_EXPIRATION_DATE)
        applyEditText(FieldType.CARD_HOLDER_NAME)

        assertEquals(4, collect.getAllStates().size)
    }

    @Test
    fun test_submit_path_method() {
        val client = applyApiClient()

        collect.submit("/path", HTTPMethod.POST)

        Mockito.verify(client).call(any(), any(), any(), any())
    }

    @Test
    fun test_submit_request_builder() {
        val client = applyApiClient()

        val request = VGSRequest.VGSRequestBuilder()
            .setPath("/path")
            .setMethod(HTTPMethod.POST)
            .build()
        collect.submit(request)

        Mockito.verify(client).call(any(), any(), any(), any())
    }

    @Test
    fun test_asyncsubmit_path_method() {
        val client = applyApiClient()

        collect.asyncSubmit("/path", HTTPMethod.POST)

        Mockito.verify(client).call(any(), any(), any(), any())
    }

    @Test
    fun test_asyncsubmit_request_builder() {
        val client = applyApiClient()

        val request = VGSRequest.VGSRequestBuilder()
            .setPath("/path")
            .setMethod(HTTPMethod.POST)
            .build()
        collect.asyncSubmit(request)

        Mockito.verify(client).call(any(), any(), any(), any())
    }

    @Test
    fun test_on_activity_result_pick_file() {
        val storage = applyStorage()

        val intent = Intent()
        val map = VGSHashMapWrapper<String, Any?>()
        intent.putExtra(BaseTransmitActivity.RESULT_DATA, map)

        collect.onActivityResult(TemporaryFileStorage.REQUEST_CODE, Activity.RESULT_OK, intent)

        Mockito.verify(storage).getFileStorage()
    }

    @Test
    fun test_set_custom_headers() {
        val client = applyApiClient()

        val data = HashMap<String, String>()
        data["key"] = "value"
        collect.setCustomHeaders(data)

        Mockito.verify(client).getTemporaryStorage()
        assertEquals(1, client.getTemporaryStorage().getCustomHeaders().size)
    }

    @Test
    fun test_reset_custom_headers() {
        val client = applyApiClient()

        collect.resetCustomHeaders()

        Mockito.verify(client).getTemporaryStorage()
        assertEquals(0, client.getTemporaryStorage().getCustomHeaders().size)
    }

    @Test
    fun test_set_custom_data() {
        val client = applyApiClient()

        val data = HashMap<String, String>()
        data["key"] = "value"
        collect.setCustomData(data)

        Mockito.verify(client).getTemporaryStorage()
        assertEquals(1, client.getTemporaryStorage().getCustomData().size)
    }

    @Test
    fun testResetCustomData() {
        val client = applyApiClient()

        collect.resetCustomData()

        Mockito.verify(client).getTemporaryStorage()
        assertEquals(0, client.getTemporaryStorage().getCustomData().size)
    }

    @Test
    fun test_get_file_provider() {
        val storage = applyStorage()
        collect.getFileProvider()
        Mockito.verify(storage).getFileProvider()
    }

    private fun <T> any(): T = Mockito.any<T>()

    private fun applyStorage(): InternalStorage {
        val storage = spy( InternalStorage(activity) )
        collect.setStorage(storage)

        return storage
    }

    private fun applyEditText(typeField: FieldType):InputFieldView {
        val view = when(typeField) {
            FieldType.CARD_NUMBER -> createCardNumber()
            FieldType.CVC -> createCardCVC()
            FieldType.CARD_EXPIRATION_DATE -> createCardHolder()
            else -> createCardExpDate()
        }

        (view.getView() as? BaseInputField)?.prepareFieldTypeConnection()

        collect.bindView(view)


        return view
    }

    private fun createCardNumber():VGSCardNumberEditText {
        return  spy( VGSCardNumberEditText(activity) ).apply {
            setFieldName("createCardNumber")
        }
    }

    private fun createCardCVC():CardVerificationCodeEditText {
        return  spy( CardVerificationCodeEditText(activity) ).apply {
            setFieldName("createCardCVC")
        }
    }

    private fun createCardHolder():PersonNameEditText {
        return  spy( PersonNameEditText(activity) ).apply {
            setFieldName("createCardHolder")
        }
    }

    private fun createCardExpDate():ExpirationDateEditText {
        return  spy( ExpirationDateEditText(activity) ).apply {
            setFieldName("createCardExpDate")
        }
    }

    private fun applyResponseListener(): VgsCollectResponseListener {
        val listener = Mockito.mock(VgsCollectResponseListener::class.java)
        collect.addOnResponseListeners(listener)

        return listener
    }

    private fun applyStateChangeListener(): OnFieldStateChangeListener {
        val listener = Mockito.mock(OnFieldStateChangeListener::class.java)
        collect.addOnFieldStateChangeListener(listener)

        return listener
    }

    private fun applyApiClient(): ApiClient {
        val client = Mockito.mock(ApiClient::class.java)
        Mockito.doReturn(VgsApiTemporaryStorageImpl())
            .`when`(client).getTemporaryStorage()

        collect.setClient(client)

        return client
    }
}