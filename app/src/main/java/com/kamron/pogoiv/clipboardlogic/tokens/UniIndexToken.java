package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.clipboardlogic.GymType;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.scanlogic.UpgradeCost;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

/**
 * Created by Partiolainen on 2019-11-14.
 * <p>
 * This token shows the index based on max possible cp and cost to reach this cp
 */

public class UniIndexToken extends ClipboardToken {

    //protected String string = "";
    private String[] whiteDigits = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪",
            "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲", "⑳", "㉑", "㉒", "㉓", "㉔", "㉕", "㉖", "㉗", "㉘", "㉙",
            "㉚", "㉛", "㉜", "㉝", "㉞", "㉟", "㊱", "㊲", "㊳", "㊴", "㊵", "㊶", "㊷", "㊸", "㊹", "㊺", "㊻", "㊼",
            "㊽", "㊾", "㊿"};
    private String[] blackDigits = {"⓿", "❶", "❷", "❸", "❹", "❺", "❻", "❼", "❽", "❾", "❿",
                                    "⓫", "⓬", "⓭", "⓮", "⓯", "⓰", "⓱", "⓲", "⓳", "⓴"};
    private String[] whiteLetters = {"Ⓐ", "Ⓑ", "Ⓒ", "Ⓓ", "Ⓔ", "Ⓕ", "Ⓖ", "Ⓗ", "Ⓘ", "Ⓙ", "Ⓚ", "Ⓛ",
            "Ⓜ", "Ⓝ", "Ⓞ", "Ⓟ", "Ⓠ", "Ⓡ", "Ⓢ", "Ⓣ", "Ⓤ", "Ⓥ", "Ⓦ", "Ⓧ", "Ⓨ", "Ⓩ"};

    /*private String[] blackLetters ={"🅐", "🅑", "🅒", "🅓", "🅔", "🅕", "🅖", "🅗", "🅘", "🅙", "🅚", "🅛",
            "🅜", "🅝", "🅞", "🅟", "🅠", "🅡", "🅢", "🅣", "🅤", "🅥", "🅦", "🅧", "🅨", "🅩"};*/

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     **/
    public UniIndexToken(boolean maxEv, String sep) {
        super(maxEv);
        _sep = sep;
    }

    private String _sep;

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        try {

            Pokemon pokemon = scanResult.pokemon;
            List<Pokemon> evolutionLine = pokemon.getEvolutions(); //pokeInfoCalculator.getEvolutionLine(pokemon);
            Pokemon evolvedPokemon = evolutionLine.size() == 0 ? pokemon : evolutionLine.get(evolutionLine.size() - 1);
            evolutionLine = evolvedPokemon.getEvolutions();
            evolvedPokemon = evolutionLine.size() == 0 ? evolvedPokemon : evolutionLine.get(evolutionLine.size() - 1);
            Pokemon initialPokemon =  pokeInfoCalculator.getEvolutionLine(pokemon).get(0); // evolutionLine.size() == 0 ? pokemon : evolutionLine.get(0);

            IVCombination iv = scanResult.getHighestIVCombination();

            double aecp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, iv, iv, scanResult.levelRange.min).high;
            String aecp_mark = whiteDigits[(int) Math.floor(aecp / 100)];

            boolean isFinalForm = maxEv && pokemon.getEvolutions().isEmpty() && scanResult.selectedMoveset != null;

            int evoUsedCandies = pokeInfoCalculator.getCandyCostForEvolution(initialPokemon, evolvedPokemon);
            UpgradeCost costUsed = pokeInfoCalculator.getUpgradeCost(scanResult.levelRange.max, 1, scanResult.isLucky);
            costUsed = new UpgradeCost(costUsed.dust, costUsed.candy + evoUsedCandies);

            UpgradeCost costTo40 = pokeInfoCalculator.getUpgradeCost(40, scanResult.levelRange.max, scanResult.isLucky);
            int evoTo40Candies = pokeInfoCalculator.getCandyCostForEvolution(pokemon, evolvedPokemon);

            double сostInvestedCandiesPercentage = (double) costUsed.candy / (costTo40.candy + evoTo40Candies);
            double сostInvestedStardustPercentage = (double) costUsed.dust / costUsed.dust;

            GymType gymType = GymType.UNIVERSAL;
            int baseDefSta = (int) Math.floor(Math.sqrt(evolvedPokemon.baseDefense) * Math.sqrt(evolvedPokemon.baseStamina));
            if (evolvedPokemon.baseAttack >= 199 && baseDefSta < 180) gymType = GymType.OFFENSIVE;
            if (evolvedPokemon.baseAttack < 199 && baseDefSta >= 180) gymType = GymType.DEFENSIVE;

            double rate = isFinalForm
                    ? 0.3 * iv.percentPerfect
                    + 0.35 / 2 * сostInvestedCandiesPercentage
                    + 0.35 / 2 * сostInvestedStardustPercentage
                    + (gymType == GymType.OFFENSIVE ? 0.35 : 0) * (scanResult.selectedMoveset.getAtkScore())
                    + (gymType == GymType.DEFENSIVE ? 0.35 : 0) * (scanResult.selectedMoveset.getDefScore())
                    + (gymType == GymType.UNIVERSAL ? 0.35 / 2 : 0) * (scanResult.selectedMoveset.getAtkScore())
                    + (gymType == GymType.UNIVERSAL ? 0.35 / 2 : 0) * (scanResult.selectedMoveset.getDefScore())
                    : (0.3 + (0.35 * 6.0 / 13.0)) * iv.percentPerfect
                    + (0.35 / 2 + ((0.35 / 2) * 7.0 / 13.0)) * сostInvestedCandiesPercentage
                    + (0.35 / 2 + ((0.35 / 2) * 7.0 / 13.0)) * сostInvestedStardustPercentage;

            String rate_mark = whiteLetters[(int) (25 * rate)];

            boolean isBestMoveset =
                    (gymType == GymType.OFFENSIVE && scanResult.selectedMoveset.getAtkScore() == 1.0) ||
                            (gymType == GymType.DEFENSIVE && scanResult.selectedMoveset.getDefScore() == 1.0) ||
                            (gymType == GymType.UNIVERSAL && (scanResult.selectedMoveset.getAtkScore() == 1.0
                                    || scanResult.selectedMoveset.getDefScore() == 1.0));
            //࿅ ࿇ ࿈ ࿉ ࿊ ࿋ ࿌
            double perf = iv.percentPerfect;
            String returner = rate_mark + aecp_mark
                    + (gymType == GymType.OFFENSIVE ? (isBestMoveset ? "࿇" : "࿅") : "")
                    + (gymType == GymType.DEFENSIVE ? (isBestMoveset ? "࿌" : "࿊") : "")
                    + (gymType == GymType.UNIVERSAL ? (isBestMoveset && scanResult.selectedMoveset.getAtkScore() == 1.0 && scanResult.selectedMoveset.getDefScore() < 1.0 ? "࿈" : "") : "")
                    + (gymType == GymType.UNIVERSAL ? (isBestMoveset && scanResult.selectedMoveset.getDefScore() == 1.0 ? "࿉" : "") : "")
                    + (gymType == GymType.UNIVERSAL ? (isBestMoveset && scanResult.selectedMoveset.getDefScore() < 1.0 && scanResult.selectedMoveset.getDefScore() < 1.0 ? "࿃" : "") : "")
                    + _sep
                    + (!scanResult.getHasBeenAppraised() ? "◦" :
                    (perf <= 49 ? "·" : "")
                            + (perf > 49 && perf < 64.4 ? "*" : "")
                            + (perf >= 64.4 && perf <= 80 && !scanResult.isLucky ? "⁑" : "")
                            + (perf > 80 && perf < 90 && !scanResult.isLucky ? "⁂" : "")
                            + (perf >= 90 && perf < 100 && !scanResult.isLucky || perf < 90 && scanResult.isLucky ? "☆" : "")
                            + (perf == 100 && !scanResult.isLucky || perf >= 90 && scanResult.isLucky ? "★" : ""));
            return returner;
        } catch (Throwable t) {
            throw new Error(t.getMessage());
        }
    }

    @Override
    public String getPreview() {
        return "Ⓩ㊹★";
    }

    @Override
    public String getTokenName(Context context) {
        return "UniIndex";
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
        return true;
    }

    @Override
    public String getStringRepresentation() {
        return "." + (maxEv ? "max" : "") + this.getClass().getSimpleName() + _sep;
    }
}
