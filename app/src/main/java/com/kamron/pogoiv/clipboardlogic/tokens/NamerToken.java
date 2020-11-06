package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

import namer.NamerClient;

public class NamerToken extends ClipboardToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     */


    public NamerToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 11;
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        String gender = scanResult.gender.getLetter();
        NamerClient namerClient = new NamerClient();
        String result = namerClient.GetRandomName(gender);
        return result;
    }

    @Override
    public String getPreview() {
        return "Name";
    }

    @Override
    public String getTokenName(Context context) {
        return "Namer";
    }

    @Override
    public String getLongDescription(Context context) {
        return "Namer";
    }

    @Override
    public Category getCategory() {
        return Category.BASIC_STATS;
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
