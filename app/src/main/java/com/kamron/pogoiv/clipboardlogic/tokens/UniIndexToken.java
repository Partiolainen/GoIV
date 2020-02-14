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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
            //Pokemon initialPokemon =  pokeInfoCalculator.getEvolutionLine(pokemon).get(0); // evolutionLine.size() == 0 ? pokemon : evolutionLine.get(0);

            IVCombination iv = scanResult.getHighestIVCombination();

            boolean isFinalForm = maxEv && pokemon.getEvolutions().isEmpty() && scanResult.selectedMoveset != null;
            double aeCP = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, iv, iv, scanResult.levelRange.min).high;
            double mlCP = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, iv, iv, 40).high;
            String aecp_mark = whiteDigits[(int) Math.floor((isFinalForm ? mlCP : aeCP) / 100)];

            double profiledCP = 0;
            switch (GetGymType(pokemon)){
                case UNIVERSAL:
                    profiledCP = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, iv, iv, scanResult.levelRange.min).high;
                    break;
                case OFFENSIVE:
                    profiledCP = Math.floor((evolvedPokemon.baseAttack + iv.att)
                            * Math.pow(Data.getLevelCpM(scanResult.levelRange.min), 2) * 10);
                    break;
                case DEFENSIVE:
                    profiledCP = Math.floor(Math.sqrt(evolvedPokemon.baseDefense + iv.def)
                            * Math.sqrt(evolvedPokemon.baseStamina + iv.sta)
                            * Math.pow(Data.getLevelCpM(scanResult.levelRange.min), 2) * 10);
                    break;
            }

            double profiledCP40 = 0;
            switch (GetGymType(pokemon)){
                case UNIVERSAL:
                    profiledCP40 = pokeInfoCalculator.getCpRangeAtLevel(evolvedPokemon, iv, iv, 40).high;
                    break;
                case OFFENSIVE:
                    profiledCP40 = Math.floor((evolvedPokemon.baseAttack + iv.att)
                            * Math.pow(Data.getLevelCpM(40), 2) * 10);
                    break;
                case DEFENSIVE:
                    profiledCP40 = Math.floor(Math.sqrt(evolvedPokemon.baseDefense + iv.def)
                            * Math.sqrt(evolvedPokemon.baseStamina + iv.sta)
                            * Math.pow(Data.getLevelCpM(40), 2) * 10);
                    break;
            }

            Double atkScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getAtkScore() == null) ? 0 : scanResult.selectedMoveset.getAtkScore();
            Double defScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getDefScore() == null) ? 0 : scanResult.selectedMoveset.getDefScore();
            GymType gymType = GetGymType(evolvedPokemon);

            double rate = isFinalForm
                    ? 0.2 * (iv.att+iv.def+iv.sta)/45.0 +
                      0.55 * (profiledCP / profiledCP40) +
                      0.25 * ((gymType == GymType.OFFENSIVE ? 1.0 : 0.0) * atkScore
                              + (gymType == GymType.DEFENSIVE ? 1.0 : 0.0) * defScore
                              + (gymType == GymType.UNIVERSAL ? 1.0 / 2.0 : 0.0) * atkScore
                              + (gymType == GymType.UNIVERSAL ? 1.0 / 2.0 : 0.0) * defScore)
                    : (0.2 + 0.25 * 0.2 / 0.75) * (iv.att+iv.def+iv.sta)/45.0 +
                      (0.55 + 0.25 * 0.55 / 0.75) * (profiledCP / profiledCP40);

            Double pvpPotential = GetPVPPotential(scanResult);
            String rate_mark = (pvpPotential < 0.6) ? whiteLetters[(int) (25.0 * rate)] : blackDigits[(int) (21.0*GetPVPRate(scanResult, mlCP))];
            String badge = Badge(evolvedPokemon, scanResult, isFinalForm, pvpPotential);

            String badges = badge + rate_mark + aecp_mark + GetIVBadge(iv, scanResult.isLucky);
            String returner = badges + GetShortName(pokemon.name, 11 - badges.length());
            return returner;
        } catch (Throwable t) {
            throw new Error(t.getMessage());
        }
    }

    private double GetPVPRate(ScanResult scanResult, double mlCP) {
        Double pvpGreatScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpGreatScore() == null) ? 0 : scanResult.selectedMoveset.getPvpGreatScore();
        Double pvpUltraScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpUltraScore() == null) ? 0 : scanResult.selectedMoveset.getPvpUltraScore();
        Double pvpMasterScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpMasterScore() == null) ? 0 : scanResult.selectedMoveset.getPvpMasterScore();

        int cp = scanResult.cp;
        if (cp <= 1500) return pvpGreatScore * (double) cp / 1500.0;
        if (cp <= 2500) return pvpUltraScore * (double) cp / 2500.0;
        return pvpMasterScore * (double) cp / mlCP;
    }

    private double GetPVPPotential(ScanResult scanResult){
        Double pvpGreatScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpGreatScore() == null) ? 0 : scanResult.selectedMoveset.getPvpGreatScore();
        Double pvpUltraScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpUltraScore() == null) ? 0 : scanResult.selectedMoveset.getPvpUltraScore();
        Double pvpMasterScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpMasterScore() == null) ? 0 : scanResult.selectedMoveset.getPvpMasterScore();

        int cp = scanResult.cp;
        if(cp <= 1500) return pvpGreatScore;
        if(cp <= 2500) return pvpUltraScore;
        return pvpMasterScore;
    }

    private String Badge(Pokemon evolvedPokemon, ScanResult scanResult, boolean isFinalForm, double pvpPotential) {
        GymType gymType = GetGymType(evolvedPokemon);
        if(pvpPotential >= 0.6) return "φ";
        if(!isFinalForm) return "";
        if (gymType == GymType.DEFENSIVE || gymType == GymType.UNIVERSAL && scanResult.selectedMoveset.getDefScore() > 0.95)
            return "ø";
        if ((gymType == GymType.OFFENSIVE || gymType == GymType.UNIVERSAL)&& scanResult.selectedMoveset.getAtkScore() > 0.95)
            return "ψ";
        return "";
    }

    private GymType GetGymType(Pokemon pokemon){
        GymType gymType = GymType.UNIVERSAL;
        int baseDefSta = (int) Math.floor(Math.sqrt(pokemon.baseDefense) * Math.sqrt(pokemon.baseStamina));
        if (pokemon.baseAttack >= 198 && baseDefSta < 180) gymType = GymType.OFFENSIVE;
        if (pokemon.baseAttack < 198 && baseDefSta >= 180) gymType = GymType.DEFENSIVE;
        return gymType;
    }

    private String GetIVBadge(IVCombination IV, boolean IsLucky)
    {
        double ivValue = 100.0*(IV.att+IV.def+IV.sta)/45.0;
        if (ivValue <= 49) return "·";
        if (ivValue > 49 && ivValue < 64.4) return "*";
        if (ivValue >= 64.4 && ivValue < 80 && !IsLucky) return "⁑";
        if (ivValue >= 80 && ivValue < 90 && !IsLucky) return "⁂";
        if (ivValue >= 90 && ivValue < 100 && !IsLucky || ivValue < 90 && IsLucky) return "☆";
        if (ivValue==100.0 && !IsLucky || IsLucky && ivValue >= 90) return "★";
        return "·";
    }

    public String GetShortName(String name, int len)
    {
        name = name.split(" - ")[0].trim();
	    String other = name.substring(1);
        String first = name.substring(0,1);
        for (int i = 0; i < name.length()-len; i++)
        {
            int a = StringUtils.lastIndexOfAny(other, new String[] {"a", "e", "u", "i", "o"});
            if (a > -1){
                StringBuilder sb = new StringBuilder(other);
                sb.deleteCharAt(a);
                other = sb.toString();
            }
        }

        String res = first+other;
        return res.substring(0, Math.min(len, res.length()));
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
