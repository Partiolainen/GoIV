package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import android.widget.TextView;
import butterknife.OnCheckedChanged;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.AppraisalManager;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.utils.GUIColorFromPokeType;
import com.kamron.pogoiv.utils.ReactiveColorListener;
import com.kamron.pogoiv.utils.fractions.MovableFraction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.kamron.pogoiv.GoIVSettings.APPRAISAL_WINDOW_POSITION;


public class AppraisalFraction extends MovableFraction implements AppraisalManager.OnAppraisalEventListener,
        ReactiveColorListener {

    @BindView(R.id.btnCheckIv)
    Button btnCheckIv;
    @BindView(R.id.statsButton)
    Button statsButton;

    @BindView(R.id.headerAppraisal)
    LinearLayout headerAppraisal;

    @BindView(R.id.valueLayout)
    ConstraintLayout spinnerLayout;

    @BindView(R.id.atkEnabled)
    CheckBox atkEnabled;
    @BindView(R.id.atkSeek)
    SeekBar atkSeek;
    @BindView(R.id.atkValue)
    TextView atkValue;
    @BindView(R.id.defEnabled)
    CheckBox defEnabled;
    @BindView(R.id.defSeek)
    SeekBar defSeek;
    @BindView(R.id.defValue)
    TextView defValue;
    @BindView(R.id.staEnabled)
    CheckBox staEnabled;
    @BindView(R.id.staSeek)
    SeekBar staSeek;
    @BindView(R.id.staValue)
    TextView staValue;


    private Pokefly pokefly;
    private AppraisalManager appraisalManager;
    private boolean insideUpdate = false;


    public AppraisalFraction(@NonNull Pokefly pokefly,
                             @NonNull SharedPreferences sharedPrefs,
                             @NonNull AppraisalManager appraisalManager) {
        super(sharedPrefs);
        this.pokefly = pokefly;
        this.appraisalManager = appraisalManager;
    }

    @Override
    protected @Nullable String getVerticalOffsetSharedPreferencesKey() {
        return APPRAISAL_WINDOW_POSITION;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fraction_appraisal;
    }

    @Override
    public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!insideUpdate) {
                    if (appraisalManager.attack != atkSeek.getProgress()) {
                        appraisalManager.attackValid = true;
                    }
                    appraisalManager.attack = atkSeek.getProgress();
                    if (appraisalManager.defense != defSeek.getProgress()) {
                        appraisalManager.defenseValid = true;
                    }
                    appraisalManager.defense = defSeek.getProgress();
                    if (appraisalManager.stamina != staSeek.getProgress()) {
                        appraisalManager.staminaValid = true;
                    }
                    appraisalManager.stamina = staSeek.getProgress();
                    updateValueTexts();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
            }
        };

        atkSeek.setOnSeekBarChangeListener(listener);
        defSeek.setOnSeekBarChangeListener(listener);
        staSeek.setOnSeekBarChangeListener(listener);
        setSpinnerSelection();

        // Listen for new appraisal info
        appraisalManager.addOnAppraisalEventListener(this);

        GUIColorFromPokeType.getInstance().setListenTo(this);
        updateGuiColors();
    }

    @Override
    public void onDestroy() {
        appraisalManager.removeOnAppraisalEventListener(this);
        GUIColorFromPokeType.getInstance().removeListener(this);
    }

    @Override
    public Anchor getAnchor() {
        return Anchor.TOP;
    }

    @Override
    public int getDefaultVerticalOffset(DisplayMetrics displayMetrics) {
        return 0;
    }

    /**
     * Sets the background for the appropriate checkbox group depending on where we are at in the appraisal process.
     */
    @Override
    public void highlightActiveUserInterface() {
        spinnerLayout.setBackgroundResource(R.drawable.highlight_rectangle);
    }


    /**
     * Update the text on the 'next' button to indicate quick IV overview
     */
    private void updateIVPreviewInButton() {

        try {
            ScanResult scanResult = pokefly.computeIVWithoutUIChange();

            int possibleIVs = scanResult.getIVCombinations().size();
            //btnCheckIv.setEnabled(possibleIVs != 0);
            if (possibleIVs == 0) {
                btnCheckIv.setText("?");
            } else {
                if (scanResult.getIVCombinations().size() == 1) {
                    IVCombination result = scanResult.getIVCombinations().get(0);
                    btnCheckIv.setText(result.percentPerfect + "% (" + result.att + ":" + result.def + ":" + result.sta + ") | More info");
                } else if (scanResult.getLowestIVCombination().percentPerfect == scanResult
                        .getHighestIVCombination().percentPerfect) {
                    btnCheckIv.setText(scanResult.getLowestIVCombination().percentPerfect + "% | More info");
                } else {
                    btnCheckIv.setText(scanResult.getLowestIVCombination().percentPerfect + "% - " + scanResult
                            .getHighestIVCombination().percentPerfect + "% | More info");
                }

            }
        } catch (IllegalStateException e) {
            //Couldnt compute a valid scanresult. This is most likely due to missing HP / CP values
        }


    }

    @OnCheckedChanged({R.id.atkEnabled, R.id.defEnabled, R.id.staEnabled})
    public void onEnabled() {
        if (!insideUpdate) {
            appraisalManager.attackValid = atkEnabled.isChecked();
            appraisalManager.defenseValid = defEnabled.isChecked();
            appraisalManager.staminaValid = staEnabled.isChecked();
            updateIVPreviewInButton();
        }
    }

    @Override
    public void refreshSelection() {
        setSpinnerSelection();
        spinnerLayout.setBackground(null);
    }

    /**
     * Updates the checkboxes, labels and IV preview in the button.
     */
    private void updateValueTexts() {
        atkValue.setText(String.valueOf(appraisalManager.attack));
        defValue.setText(String.valueOf(appraisalManager.defense));
        staValue.setText(String.valueOf(appraisalManager.stamina));
        atkEnabled.setChecked(appraisalManager.attackValid);
        defEnabled.setChecked(appraisalManager.defenseValid);
        staEnabled.setChecked(appraisalManager.staminaValid);
        updateIVPreviewInButton();
    }

    /**
     * Sets the progress bars and calls {@code updateValueTexts()}, mainly used to update it from the outside.
     */
    private void setSpinnerSelection() {
        insideUpdate = true;
        updateValueTexts();
        atkSeek.setProgress(appraisalManager.attack);
        defSeek.setProgress(appraisalManager.defense);
        staSeek.setProgress(appraisalManager.stamina);
        insideUpdate = false;
    }

    @OnTouch({R.id.positionHandler, R.id.additionalRefiningHeader})
    boolean positionHandlerTouchEvent(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }

    @OnClick({R.id.statsButton})
    void onStats() {
        pokefly.navigateToInputFraction();
    }

    @OnClick(R.id.btnClose)
    void onClose() {
        pokefly.closeInfoDialog();
    }

    @OnClick(R.id.btnCheckIv)
    void checkIv() {
        pokefly.computeIv();
    }

    @Override public void updateGuiColors() {

        int c = GUIColorFromPokeType.getInstance().getColor();
        btnCheckIv.setBackgroundColor(c);
        statsButton.setBackgroundColor(c);
        headerAppraisal.setBackgroundColor(c);
    }
}
