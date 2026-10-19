package com.verygoodsecurity.demoapp.cmp

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_TENANT_ID
import com.verygoodsecurity.demoapp.ui.DemoButton
import com.verygoodsecurity.demoapp.ui.DemoChip
import com.verygoodsecurity.demoapp.ui.DemoDebugPanel
import com.verygoodsecurity.demoapp.ui.DemoErrorBanner
import com.verygoodsecurity.demoapp.ui.DemoField
import com.verygoodsecurity.demoapp.ui.DemoFieldDefaults
import com.verygoodsecurity.demoapp.ui.DemoFormCard
import com.verygoodsecurity.demoapp.ui.DemoFormSkeleton
import com.verygoodsecurity.demoapp.ui.DemoPlaceholder
import com.verygoodsecurity.demoapp.ui.DemoProgressCard
import com.verygoodsecurity.demoapp.ui.DemoProgressStep
import com.verygoodsecurity.demoapp.ui.ProgressStepState
import com.verygoodsecurity.demoapp.ui.DemoResponse
import com.verygoodsecurity.demoapp.ui.DemoScreen
import com.verygoodsecurity.demoapp.ui.DemoTheme
import com.verygoodsecurity.demoapp.ui.errorMessage
import com.verygoodsecurity.demoapp.utils.NetworkHelper
import com.verygoodsecurity.demoapp.utils.accessToken
import com.verygoodsecurity.demoapp.utils.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCardAttributesLookupListener
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardNumberOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCvcOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsExpiryOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCvcTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsExpiryTextFieldState

private const val TAG = "CMPActivity"

/** Progress of the card attributes lookup that starts once the card number has a full BIN. */
private enum class LookupStatus { IDLE, LOADING, SUCCESS, FAILURE }

/**
 * Demonstrates CMP card creation flow using [VGSCollect] with Jetpack Compose.
 *
 * This screen shows:
 * - How to start a [VGSCollect] session with authorization via [VGSCollect.session]
 * - How to request an access token before CMP operations
 * - How to declare Compose field states (PAN, expiration date, CVC)
 * - How to call CMP [VGSCollect.createCard] and handle the response
 * - How to listen for card attributes lookup events
 *
 * Unlike other demo activities that use the synchronous [VGSCollect] constructor,
 * this activity uses [VGSCollect.session] for async initialization with auth, so the
 * form is only shown once the session is ready.
 *
 * Layout, styling and the debug panel come from the shared demo components in
 * `com.verygoodsecurity.demoapp.ui`, so this file only contains SDK usage.
 *
 * 📘 Official documentation:
 * https://docs.verygoodsecurity.com/vault/developer-tools/vgs-collect/android-sdk/index
 *
 * @see VGSCollect
 * @see VgsCollectResponseListener
 * @see VgsCardAttributesLookupListener
 */
class CMPActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val tenantId: String by lazy { getStringExtra(KEY_BUNDLE_TENANT_ID) }
    private val env: String by lazy { getStringExtra(KEY_BUNDLE_ENVIRONMENT) }

    /** `null` until the session is ready. */
    private var collect by mutableStateOf<VGSCollect?>(null)

    /** Set once the auth handler got a token; the session configuration is loaded next. */
    private var isTokenReady by mutableStateOf(false)

    /** Whether a create card request is in flight. */
    private var isCreating by mutableStateOf(false)

    private var lookupStatus by mutableStateOf(LookupStatus.IDLE)
    private var lookupCode by mutableStateOf(0)

    /** Last result, set from [onResponse] and displayed by the debug panel. */
    private var response by mutableStateOf<DemoResponse?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The top bar is a dark gradient, so the status bar needs light icons.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT))
        VGSCollectLogger.isEnabled = true
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        setContent {
            DemoTheme {
                CmpScreen(
                    collect = collect,
                    environment = env,
                    isTokenReady = isTokenReady,
                    isCreating = isCreating,
                    lookup = lookupStatus,
                    lookupCode = lookupCode,
                    response = response,
                    onBack = ::finish,
                    onCreateCard = { states ->
                        isCreating = true
                        collect?.createCard(fieldsStates = states)
                    }
                )
            }
        }
        initSession()
    }

    /**
     * Releases VGSCollect resources.
     *
     * Must be called to:
     * - Clear internal references
     * - Prevent memory leaks
     * - Properly dispose field bindings
     */
    override fun onDestroy() {
        super.onDestroy()
        collect?.onDestroy()
    }

    /**
     * Initializes a [VGSCollect] session with authorization.
     *
     * This handles the async session creation flow:
     * 1. Request an access token via [NetworkHelper]
     * 2. Initialize [VGSCollect] session with the token
     * 3. On success, listen for responses and card attributes lookup events
     */
    private fun initSession() {
        VGSCollect.session(
            this,
            tenantId,
            "test-a-1-collect-form",
            env,
            authHandler = { onComplete ->
                NetworkHelper.accessToken(
                    onSuccess = {
                        isTokenReady = true
                        onComplete(it)
                    },
                    onError = {
                        Log.e(TAG, "Access token error: $it")
                        response = DemoResponse(isSuccess = false, body = it)
                    }
                )
            },
            onSuccess = {
                Log.d(TAG, "Session initialized successfully")
                it.addOnResponseListeners(this)
                it.setCardAttributesLookupListener(lookupListener)
                collect = it
            },
            onError = { code, message ->
                Log.e(TAG, "Session error: code=$code, message=$message")
                response = DemoResponse(isSuccess = false, code = code, body = message)
            }
        )
    }

    override fun onResponse(response: VGSResponse?) {
        isCreating = false
        when (response) {
            is VGSResponse.SuccessResponse -> {
                Log.d(TAG, "Create card success: ${response.body}")
                this.response = DemoResponse(isSuccess = true, code = response.successCode, body = response.body)
            }

            is VGSResponse.ErrorResponse -> {
                Log.e(TAG, "Create card error: ${response.errorCode}")
                this.response = DemoResponse(
                    isSuccess = false,
                    code = response.errorCode,
                    body = response.localizeMessage
                )
            }

            else -> Unit
        }
    }

    private val lookupListener = object : VgsCardAttributesLookupListener {

        override fun onStart() {
            Log.d(TAG, "Card attributes lookup started")
            lookupStatus = LookupStatus.LOADING
        }

        override fun onSuccess(code: Int, body: String) {
            Log.d(TAG, "Card attributes lookup success: code=$code, body=$body")
            lookupStatus = LookupStatus.SUCCESS
        }

        override fun onFailure(code: Int, body: String?, message: String?) {
            Log.e(TAG, "Card attributes lookup failure: code=$code, body=$body, message=$message")
            lookupCode = code
            lookupStatus = LookupStatus.FAILURE
        }
    }
}

/**
 * Shows the initialization progress until [collect] is ready, then the card form.
 * If the session fails to start, [response] holds the error and replaces the progress.
 */
@Composable
private fun CmpScreen(
    collect: VGSCollect?,
    environment: String,
    isTokenReady: Boolean,
    isCreating: Boolean,
    lookup: LookupStatus,
    lookupCode: Int,
    response: DemoResponse?,
    onBack: () -> Unit,
    onCreateCard: (List<BaseFieldState>) -> Unit,
) {
    val isInitializing = collect == null && response == null
    DemoScreen(
        title = stringResource(R.string.cmp_title),
        onBack = onBack,
        loading = isInitializing || isCreating
    ) {
        when {
            collect != null -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DemoChip(stringResource(R.string.cmp_session_ready), DemoTheme.colors.success, showDot = true)
                    Text(environment, style = DemoTheme.typography.code, color = DemoTheme.colors.muted)
                }
                CardForm(collect, isCreating, lookup, lookupCode, response, onCreateCard)
            }

            isInitializing -> InitializingSession(isTokenReady)
            else -> DemoErrorBanner(response?.body.orEmpty())
        }
    }
}

@Composable
private fun InitializingSession(isTokenReady: Boolean) {
    DemoProgressCard(
        title = stringResource(R.string.cmp_init_title),
        subtitle = stringResource(R.string.cmp_init_subtitle),
        description = stringResource(R.string.cmp_init_description),
        steps = listOf(
            DemoProgressStep(
                stringResource(R.string.cmp_init_step_auth_title),
                stringResource(R.string.cmp_init_step_auth_subtitle),
                ProgressStepState.DONE
            ),
            DemoProgressStep(
                stringResource(R.string.cmp_init_step_token_title),
                stringResource(R.string.cmp_init_step_token_subtitle),
                if (isTokenReady) ProgressStepState.DONE else ProgressStepState.ACTIVE
            ),
            DemoProgressStep(
                stringResource(R.string.cmp_init_step_config_title),
                stringResource(R.string.cmp_init_step_config_subtitle),
                if (isTokenReady) ProgressStepState.ACTIVE else ProgressStepState.PENDING
            ),
        )
    )
    DemoFormSkeleton()
    Text(
        stringResource(R.string.cmp_init_footnote),
        Modifier.fillMaxWidth(),
        style = DemoTheme.typography.overline,
        color = DemoTheme.colors.subtle,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun CardForm(
    collect: VGSCollect,
    isCreating: Boolean,
    lookup: LookupStatus,
    lookupCode: Int,
    response: DemoResponse?,
    onCreateCard: (List<BaseFieldState>) -> Unit,
) {
    // ==========================================================
    // STEP 1: Declare field states
    // ==========================================================
    // Only fields included in the list passed to createCard are sent.
    var cardNumberState by rememberVgsCardNumberTextFieldState(collect = collect, fieldName = "card.number")
    var cvcState by rememberVgsCvcTextFieldState(collect = collect, fieldName = "card.cvc")
    var expiryState by rememberVgsExpiryTextFieldState(
        collect = collect,
        fieldName = "card.expiry",
        inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
        outputDateFormat = VgsExpiryDateFormat.MonthShortYear,
    )
    val states = listOf(cardNumberState, cvcState, expiryState)

    // ==========================================================
    // STEP 2: Validate and create the card
    // ==========================================================
    var showErrors by remember { mutableStateOf(false) }
    fun invalid(state: BaseFieldState) = showErrors && !state.isValid
    fun createCard() {
        showErrors = true
        if (!isCreating && states.all { it.isValid }) onCreateCard(states)
    }

    val fieldColors = DemoFieldDefaults.colors()
    val fieldShape = DemoFieldDefaults.shape

    // ==========================================================
    // STEP 3: Render fields
    // ==========================================================
    DemoFormCard {
        DemoField(
            label = stringResource(R.string.cmp_pan_title),
            error = cardNumberState.errorMessage().takeIf { invalid(cardNumberState) }
        ) {
            VgsCardNumberOutlinedTextField(
                state = cardNumberState,
                onStateChange = { cardNumberState = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { DemoPlaceholder(stringResource(R.string.collect_card_number_hint)) },
                isError = invalid(cardNumberState),
                trailingIcon = {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = cardNumberState.cardBrand.cardIcon),
                        contentDescription = cardNumberState.cardBrand.name,
                    )
                },
                colors = fieldColors,
                shape = fieldShape,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            // STEP 4: Card attributes lookup runs by itself once the PAN has a full BIN.
            LookupMessage(lookup, lookupCode)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
            DemoField(
                label = stringResource(R.string.cvc_title),
                modifier = Modifier.weight(1f),
                error = cvcState.errorMessage().takeIf { invalid(cvcState) }
            ) {
                VgsCvcOutlinedTextField(
                    state = cvcState,
                    onStateChange = { cvcState = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { DemoPlaceholder(stringResource(R.string.collect_cvc_hint)) },
                    isError = invalid(cvcState),
                    colors = fieldColors,
                    shape = fieldShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
            DemoField(
                label = stringResource(R.string.expiry_title),
                modifier = Modifier.weight(1f),
                error = expiryState.errorMessage().takeIf { invalid(expiryState) }
            ) {
                VgsExpiryOutlinedTextField(
                    state = expiryState,
                    onStateChange = { expiryState = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { DemoPlaceholder(stringResource(R.string.collect_expiry_hint)) },
                    isError = invalid(expiryState),
                    colors = fieldColors,
                    shape = fieldShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { createCard() })
                )
            }
        }
    }

    val invalidCount = states.count { !it.isValid }
    if (showErrors && invalidCount > 0) {
        DemoErrorBanner(pluralStringResource(R.plurals.collect_invalid_fields_banner, invalidCount, invalidCount))
    }

    // ==========================================================
    // STEP 5: Create card
    // ==========================================================
    DemoButton(
        text = stringResource(R.string.cmp_create_card_btn_title),
        onClick = ::createCard,
        modifier = Modifier.fillMaxWidth(),
        loading = isCreating
    )

    DemoDebugPanel(states = states, response = response)
}

@Composable
private fun LookupMessage(lookup: LookupStatus, code: Int) {
    val text = when (lookup) {
        LookupStatus.IDLE -> return
        LookupStatus.LOADING -> stringResource(R.string.cmp_lookup_loading)
        LookupStatus.SUCCESS -> stringResource(R.string.cmp_lookup_success)
        LookupStatus.FAILURE -> stringResource(R.string.cmp_lookup_failure, code)
    }
    Text(
        text,
        style = DemoTheme.typography.caption,
        color = if (lookup == LookupStatus.FAILURE) DemoTheme.colors.error else DemoTheme.colors.muted
    )
}
