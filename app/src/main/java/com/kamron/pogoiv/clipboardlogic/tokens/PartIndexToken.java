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

public class PartIndexToken extends ClipboardToken {

    //protected String string = "";
    private String[] whiteDigits = {"⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪",
            "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲", "⑳", "㉑", "㉒", "㉓", "㉔", "㉕", "㉖", "㉗", "㉘", "㉙",
            "㉚", "㉛", "㉜", "㉝", "㉞", "㉟", "㊱", "㊲", "㊳", "㊴", "㊵", "㊶", "㊷", "㊸", "㊹", "㊺", "㊻", "㊼",
            "㊽", "㊾", "㊿"};

    private String[] whiteLetters = {"Ⓐ", "Ⓑ", "Ⓒ", "Ⓓ", "Ⓔ", "Ⓕ", "Ⓖ", "Ⓗ", "Ⓘ", "Ⓙ", "Ⓚ", "Ⓛ",
            "Ⓜ", "Ⓝ", "Ⓞ", "Ⓟ", "Ⓠ", "Ⓡ", "Ⓢ", "Ⓣ", "Ⓤ", "Ⓥ", "Ⓦ", "Ⓧ", "Ⓨ", "Ⓩ"};

    /*private String[] blackLetters ={"🅐", "🅑", "🅒", "🅓", "🅔", "🅕", "🅖", "🅗", "🅘", "🅙", "🅚", "🅛",
            "🅜", "🅝", "🅞", "🅟", "🅠", "🅡", "🅢", "🅣", "🅤", "🅥", "🅦", "🅧", "🅨", "🅩"};*/

    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     **/
    public PartIndexToken(boolean maxEv, String sep) {
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
            Pokemon evolvedPokemon = evolutionLine.size()==0 ? pokemon : evolutionLine.get(evolutionLine.size() - 1);
            evolutionLine = evolvedPokemon.getEvolutions();
            evolvedPokemon = evolutionLine.size()==0 ? evolvedPokemon : evolutionLine.get(evolutionLine.size() - 1);

            IVCombination lowiv = scanResult.getLowestIVCombination();
            IVCombination iv = scanResult.getHighestIVCombination();

            int aecp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, lowiv, iv, scanResult.levelRange.min).high;
            int clcp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, lowiv, iv, Data.maximumPokemonCurrentLevel).high;
            int mlcp = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, lowiv, iv, 40).high;

            int aehp = maxEv ? pokeInfoCalculator.getHPAtLevel(scanResult, scanResult.levelRange.min, evolvedPokemon) : 0;
            int clhp = maxEv ? pokeInfoCalculator.getHPAtLevel(scanResult, Data.maximumPokemonCurrentLevel, evolvedPokemon) : 0;
            int mlhp = maxEv ? pokeInfoCalculator.getHPAtLevel(scanResult, 40, evolvedPokemon) : 0;

            int aecp_mark = round(aecp / 100);
            int clcp_mark = round(clcp / 100);
            int mlcp_mark = round(mlcp / 100);

            double candy_dust_rate = 10/3;
            double evo_cost = pokeInfoCalculator.getCandyCostForEvolution(pokemon, evolvedPokemon) / 10;
            UpgradeCost cost = new UpgradeCost(0, 0);
            if(Data.maximumPokemonCurrentLevel >= scanResult.levelRange.max)
               cost=pokeInfoCalculator.getUpgradeCost(Data.maximumPokemonCurrentLevel, scanResult.levelRange.max, scanResult.isLucky);
            double cl_cost = max((cost.candy + candy_dust_rate * cost.dust / 1000)/10 - evo_cost, 0);

            cost = pokeInfoCalculator.getUpgradeCost(40, scanResult.levelRange.max, scanResult.isLucky);
            double ml_cost = max((cost.candy + candy_dust_rate * cost.dust / 1000)/10 - cl_cost, 0);

            double perfc = iv.percentPerfect;
            /*if(pokemon.getEvolutions().isEmpty() && scanResult.selectedMoveset!=null){
                Double atk = scanResult.selectedMoveset.getAtkScore();
                Double def = scanResult.selectedMoveset.getDefScore();
                if(atk!=null && def!=null)
                   perfc = Math.round((iv.att*atk + iv.def*def + iv.sta)/45f * 100);
            }*/

            int cp = scanResult.cp;
            int hp = maxEv ? scanResult.hp : 0;
            double cp_rate = perfc*(cp /*+ 2*hp*/)/10000;
            double ae_rate = (aecp/*+2*aehp*/)*(perfc-evo_cost)/10000;
            double cl_rate = (clcp+3*clhp)*(perfc-cl_cost)/10000;
            double ml_rate = (mlcp+3*mlhp)*(perfc-ml_cost)/10000;

            int rate = max(0, min(25, (int)round((cp_rate * 1 + ae_rate * 2 + cl_rate * 6 + ml_rate * 1)
                                                *(40/Data.trainerLevel) / 10)));
            int mark = 0;
            if (scanResult.cp < aecp) mark = aecp_mark;
            else if (scanResult.cp >= aecp && scanResult.cp < clcp) mark = clcp_mark;
            else if (scanResult.cp >= clcp) mark = mlcp_mark;
            int perf = iv.percentPerfect;
            String returner = "" + whiteLetters[rate] + whiteDigits[mark] + _sep
                    + (!scanResult.getHasBeenAppraised() ? "◦" :
                      (perf<=49 ? "·" : "")
                    + (perf>49 && perf < 64.4 ? "*" : "")
                    + (perf>=64.4 && perf <= 80 && !scanResult.isLucky ? "⁑" : "")
                    + (perf>80 && perf < 90 && !scanResult.isLucky ? "⁂" : "")
                    //+ (perf>80 && perf < 85 && !scanResult.isLucky || perf <= 80 && scanResult.isLucky ? "∴" : "")
                    //+ (perf>=86 && perf < 90 && !scanResult.isLucky || perf>80 && perf < 85 && scanResult.isLucky ? "∵" : "")
                    + (perf>=90 && perf < 100 && !scanResult.isLucky || /*perf>80 &&*/ perf < 90 && scanResult.isLucky ? "☆" : "")
                    + (perf==100 && !scanResult.isLucky || perf >= 90 && scanResult.isLucky ? "★" : "") );

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
        return true;
    }

    @Override
    public String getStringRepresentation() {
        return "." + (maxEv ? "max" : "") + this.getClass().getSimpleName() + _sep;
    }
}
