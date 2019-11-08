package com.verygoodsecurity.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.verygoodsecurity.vgscollect.core.Environment;
import com.verygoodsecurity.vgscollect.core.HTTPMethod;
import com.verygoodsecurity.vgscollect.core.VGSCollect;
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener;
import com.verygoodsecurity.vgscollect.core.model.VGSResponse;
import com.verygoodsecurity.vgscollect.core.model.state.FieldState;
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener;
import com.verygoodsecurity.vgscollect.widget.VGSEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class JavaActivity extends Activity implements View.OnClickListener, VgsCollectResponseListener, OnFieldStateChangeListener {

    private VGSCollect vgsForm = new VGSCollect("tntxrsfgxcn", Environment.SANDBOX);

    private TextView responseView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sendGet).setOnClickListener(this);
        findViewById(R.id.sendPost).setOnClickListener(this);

        vgsForm.setOnResponseListener(this);

        vgsForm.setOnFieldStateChangeListener(this);

        responseView = findViewById(R.id.responseView);

        View cardNumberField = findViewById(R.id.cardNumberField);
        vgsForm.bindView((VGSEditText) cardNumberField);
        View cardCVVField = findViewById(R.id.cardCVVField);
        vgsForm.bindView((VGSEditText) cardCVVField);
        View cardHolderField = findViewById(R.id.cardHolderField);
        vgsForm.bindView((VGSEditText) cardHolderField);
        View cardExpDateField = findViewById(R.id.cardExpDateField);
        vgsForm.bindView((VGSEditText) cardExpDateField);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendGet: vgsForm.asyncSubmit(this, "/get", HTTPMethod.GET, null);
            case R.id.sendPost: vgsForm.asyncSubmit(this, "/post", HTTPMethod.POST, null);
        }
    }

    @Override
    public void onResponse(@org.jetbrains.annotations.Nullable VGSResponse response) {
        if(response.getCode() >= 200 && response.getCode() <=300 ) {
            Map<String, String> m = ((VGSResponse.SuccessResponse)response).getResponse();
            int c = ((VGSResponse.SuccessResponse)response).getSuccessCode();
            StringBuilder builder = new StringBuilder("CODE: ")
                    .append(response.getCode()).append("\n\n");
            for (Map.Entry<String, String> entry : m.entrySet()) {
                builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            responseView.setText(builder.toString());
        }
    }

    @Override
    public void onStateChange(@NotNull FieldState state) { }

    //todo remove for release
    @Override
    public void onStateChange(@NotNull Collection<? extends FieldState> states) {
        StringBuilder builder = new StringBuilder();
        for (FieldState state : states) {
            builder.append(state.getAlias()).append("\n")
                    .append("   hasFocus: ").append(state.getHasFocus()).append("\n")
                    .append("   isValid: ").append(state.isValid()).append("\n")
                    .append("   isEmpty: ").append(state.isEmpty()).append("\n")
                    .append("   isRequired: ").append(state.isRequired()).append("\n");
            if(state instanceof FieldState.CardNumberState) {
                builder.append("    type: ").append(((FieldState.CardNumberState) state).getCardType()).append("\n")
                        .append("       last4: ").append(((FieldState.CardNumberState) state).getLast4()).append("\n")
                        .append("       bin: ").append(((FieldState.CardNumberState) state).getBin()).append("\n");
            }
        }

        ((TextView)findViewById(R.id.responseView)).setText(builder.toString());
    }
}
