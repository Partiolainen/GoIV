package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
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

            double candy_dust_rate = 10 / 3;
            double iv_decrease = 7.5;

            Pokemon pokemon = scanResult.pokemon;
            List<Pokemon> evolutionLine = pokemon.getEvolutions(); //pokeInfoCalculator.getEvolutionLine(pokemon);
            Pokemon evolvedPokemon = evolutionLine.size() == 0 ? pokemon : evolutionLine.get(evolutionLine.size() - 1);
            evolutionLine = evolvedPokemon.getEvolutions();
            evolvedPokemon = evolutionLine.size() == 0 ? evolvedPokemon : evolutionLine.get(evolutionLine.size() - 1);

            IVCombination lowiv = scanResult.getLowestIVCombination();
            IVCombination iv = scanResult.getHighestIVCombination();
            IVCombination maxiv = new IVCombination(15, 15, 15);

            double aecp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, lowiv, iv, scanResult.levelRange.min).high;
            double mlcp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, lowiv, iv, 40).high;
            double maxiv_cp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, maxiv, maxiv, 40).high;

            //int aehp = maxEv ? pokeInfoCalculator.getHPAtLevel(scanResult, scanResult.levelRange.min, evolvedPokemon) : 0;
            //int mlhp = maxEv ? pokeInfoCalculator.getHPAtLevel(scanResult, 40, evolvedPokemon) : 0;

            double maxcost = (704 + 270 * candy_dust_rate) / 10;
            int evo_cost_candy = pokeInfoCalculator.getCandyCostForEvolution(pokemon, evolvedPokemon);

            UpgradeCost cost = pokeInfoCalculator.getUpgradeCost(40, scanResult.levelRange.max, scanResult.isLucky);
            double ml_cost = (cost.candy - evo_cost_candy + candy_dust_rate * cost.dust / 1000.0) / 10.0;

            double cp_rate = aecp / mlcp;
            double ml_cost_rate = (maxcost - ml_cost / 2) / maxcost;
            double iv_rate = (1.0 - (1.0 - mlcp / maxiv_cp) * iv_decrease);

            boolean isFinalForm = maxEv && pokemon.getEvolutions().isEmpty() && scanResult.selectedMoveset!=null;

            double moveDecrease = 1.0;
            double nonFinalAtkScore = 0.93705;
            double nonFinalDefScore = 0.8864;
            Double atkScore = isFinalForm ? 1.0-(1.0-scanResult.selectedMoveset.getAtkScore())/moveDecrease : nonFinalAtkScore;
            Double defScore = isFinalForm ? 1.0-(1.0-scanResult.selectedMoveset.getDefScore())/moveDecrease : nonFinalDefScore;

            double cp_att = ((evolvedPokemon.baseAttack + iv.att) * Math.pow(0.7903001, 2.0)) * atkScore;
            double cp_def = (Math.sqrt(evolvedPokemon.baseDefense + iv.def) * Math.sqrt(evolvedPokemon.baseStamina + iv.sta)
                    * Math.pow(0.7903001, 2.0)) * defScore;

            double cp_att_max = ((evolvedPokemon.baseAttack + 15.0) * Math.pow(0.7903001, 2.0));
            double cp_def_max = (Math.sqrt(evolvedPokemon.baseDefense + 15.0) * Math.sqrt(evolvedPokemon.baseStamina + 15.0) * Math.pow(0.7903001, 2.0));

            double profile_incr = 1.5;
            boolean isAtt = evolvedPokemon.baseAttack > Math.sqrt(evolvedPokemon.baseDefense) * Math.sqrt(evolvedPokemon.baseStamina) + 2.0;

            double cp_att_rate = (1.0 - (1.0 - cp_att / cp_att_max) * (isAtt ? profile_incr : 1.0));
            double cp_def_rate = (1.0 - (1.0 - cp_def / cp_def_max) * (!isAtt ? profile_incr : 1.0));

            int rate_att = (int) round(max(0.0, min(25.0, cp_rate * ml_cost_rate * iv_rate * cp_att_rate * 25.0)));
            int rate_def = (int) round(max(0.0, min(20.0, cp_rate * ml_cost_rate * iv_rate * cp_def_rate * 20.0)));

            //int rate = max(0, min(isAtt ? 25 : 20, (int) round(isAtt ? rate_att : rate_def)));

            int mark;
            if (scanResult.cp < aecp) mark = (int) round(aecp / 100.0);
            else mark = (int) round(mlcp / 100.0);
            int perf = iv.percentPerfect;
            String moveSymb = "";
            if(isFinalForm){
                double score = isAtt ? scanResult.selectedMoveset.getAtkScore() : scanResult.selectedMoveset.getDefScore();
                if(score==1.0){
                    moveSymb = "◉";
                }else if(score > .95){
                    moveSymb = "◎";
                }
            }

            String returner = ""
                    + (isAtt ? whiteLetters[rate_att] :
                               blackDigits[rate_def])
                    + whiteDigits[mark]
                    + moveSymb
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
