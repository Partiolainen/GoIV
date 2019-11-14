package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.ArrayList;

import static java.lang.Math.round;

/**
 * Created by Partiolainen on 2019-11-14.
 * <p>
 * This token shows the index based on max possible cp and cost to reach this cp
 */

public class PartIndexToken extends ClipboardToken {

    //String half = "½";
    private String[] unicodes = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪",
            "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲", "⑳", "㉑", "㉒", "㉓", "㉔", "㉕", "㉖", "㉗", "㉘", "㉙",
            "㉚", "㉛", "㉜", "㉝", "㉞", "㉟", "㊱", "㊲", "㊳", "㊴", "㊵", "㊶", "㊷", "㊸", "㊹", "㊺", "㊻", "㊼",
            "㊽", "㊾", "㊿"};

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     **/
    public PartIndexToken(boolean maxEv) {
        super(maxEv);
    }

    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        Pokemon pokemon = scanResult.pokemon;
        ArrayList<Pokemon> evolutionLine = pokeInfoCalculator.getEvolutionLine(pokemon);
        Pokemon evolvedPokemon = evolutionLine.get(evolutionLine.size()-1);

        int aecp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, scanResult.getLowestIVCombination(),
                scanResult.getHighestIVCombination(), scanResult.levelRange.min).high;
        int clcp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, scanResult.getLowestIVCombination(),
                scanResult.getHighestIVCombination(), Data.maximumPokemonCurrentLevel).high;
        int mlcp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, scanResult.getLowestIVCombination(),
                scanResult.getHighestIVCombination(), 40).high;
        String returner = "" + unicodes[(int) round(aecp/100)]
                + unicodes[(int) round(clcp/100)]
                + unicodes[(int) round(mlcp/100)];

        return returner;
    }

    @Override
    public String getPreview() {
        return "㊿㊹";
    }

    @Override
    public String getTokenName(Context context) {
        return "PartIndex";
    }

    @Override
    public String getLongDescription(Context context) {
        return context.getString(R.string.token_msg_PartIndex);
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
