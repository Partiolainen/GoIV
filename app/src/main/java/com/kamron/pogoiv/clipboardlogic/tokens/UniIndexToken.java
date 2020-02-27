package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.clipboardlogic.GymType;
import com.kamron.pogoiv.clipboardlogic.PVPLeague;
import com.kamron.pogoiv.clipboardlogic.PVPMark;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.Arrays;
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
            switch (GetGymType(pokemon)) {
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
            switch (GetGymType(pokemon)) {
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
                    ? 0.2 * (iv.att + iv.def + iv.sta) / 45.0 +
                    0.55 * (profiledCP / profiledCP40) +
                    0.25 * ((gymType == GymType.OFFENSIVE ? 1.0 : 0.0) * atkScore
                            + (gymType == GymType.DEFENSIVE ? 1.0 : 0.0) * defScore
                            + (gymType == GymType.UNIVERSAL ? 1.0 / 2.0 : 0.0) * atkScore
                            + (gymType == GymType.UNIVERSAL ? 1.0 / 2.0 : 0.0) * defScore)
                    : (0.2 + 0.25 * 0.2 / 0.75) * (iv.att + iv.def + iv.sta) / 45.0 +
                    (0.55 + 0.25 * 0.55 / 0.75) * (profiledCP / profiledCP40);

            PVPMark pvpMark = isFinalForm ? GetPVPMark(scanResult) : GetPVPMark(evolvedPokemon, aeCP);
            String rate_mark = (pvpMark.Mark >= GetMinLeagueRate(pvpMark.League) && isFinalForm || !isFinalForm && pvpMark.Mark >= 0.5)
                    ? blackDigits[(int) (21.0 * GetPVPRate(scanResult, aeCP, mlCP, isFinalForm))] +
                      blackDigits[(int) (21.0 * GetPVPMaxRate(scanResult, evolvedPokemon, aeCP, mlCP, isFinalForm, pokeInfoCalculator))]
                    : whiteLetters[(int) (25.0 * rate)];
            String badge = Badge(evolvedPokemon, pokemon, scanResult, isFinalForm, pvpMark);

            String badges = badge + rate_mark + aecp_mark + GetIVBadge(iv, scanResult.isLucky);
            String returner = badges + GetShortName(pokemon.name, 11 - badges.length());
            return returner;
        } catch (Throwable t) {
            throw new Error(t.getMessage());
        }
    }

    private double GetPVPMaxRate(ScanResult scanResult, Pokemon evolvedPokemon, double aeCP, double mlCP, boolean isFinalForm, PokeInfoCalculator calc){
        double targetCP = mlCP;
        if(aeCP<=2500) targetCP = 2500;
        if(aeCP<=1500) targetCP = 1500;
        double level = scanResult.levelRange.max;
        IVCombination iv = scanResult.getHighestIVCombination();
        double cpAtLev = aeCP;
        while (targetCP > cpAtLev) {
            if(level>=40) break;
            level = level + 0.5;
            double tCP = calc.getCpRangeAtLevel(evolvedPokemon, iv, iv, level).high;
            if (tCP>targetCP) break;
            cpAtLev = tCP;
        }

        int cp = scanResult.cp;
        if (!isFinalForm) {
            if (aeCP <= 1500) return GetMinLeagueRate(PVPLeague.GREAT) * cpAtLev / 1500.0;
            if (aeCP <= 2500) return GetMinLeagueRate(PVPLeague.ULTRA) * cpAtLev / 2500.0;
            return GetMinLeagueRate(PVPLeague.MASTER) * cpAtLev / mlCP;
        }
        Double pvpGreatScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpGreatScore() == null) ? 0 : scanResult.selectedMoveset.getPvpGreatScore();
        Double pvpUltraScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpUltraScore() == null) ? 0 : scanResult.selectedMoveset.getPvpUltraScore();
        Double pvpMasterScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpMasterScore() == null) ? 0 : scanResult.selectedMoveset.getPvpMasterScore();

        if (cp <= 1500) return pvpGreatScore * cpAtLev / 1500.0;
        if (cp <= 2500) return pvpUltraScore * cpAtLev / 2500.0;
        return pvpMasterScore * cpAtLev / mlCP;
    }

    private double GetPVPRate(ScanResult scanResult, double aeCP, double mlCP, boolean isFinalForm) {
        int cp = scanResult.cp;
        if (!isFinalForm) {
            if (aeCP <= 1500) return 0.5 * aeCP / 1500.0;
            if (aeCP <= 2500) return 0.5 * aeCP / 2500.0;
            return 0.5 * aeCP / mlCP;
        }
        Double pvpGreatScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpGreatScore() == null) ? 0 : scanResult.selectedMoveset.getPvpGreatScore();
        Double pvpUltraScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpUltraScore() == null) ? 0 : scanResult.selectedMoveset.getPvpUltraScore();
        Double pvpMasterScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpMasterScore() == null) ? 0 : scanResult.selectedMoveset.getPvpMasterScore();

        if (cp <= 1500) return pvpGreatScore * (double) cp / 1500.0;
        if (cp <= 2500) return pvpUltraScore * (double) cp / 2500.0;
        return pvpMasterScore * (double) cp / mlCP;
    }

    private PVPMark GetPVPMark(ScanResult scanResult){
        Double pvpGreatScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpGreatScore() == null) ? 0 : scanResult.selectedMoveset.getPvpGreatScore();
        Double pvpUltraScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpUltraScore() == null) ? 0 : scanResult.selectedMoveset.getPvpUltraScore();
        Double pvpMasterScore = (scanResult == null || scanResult.selectedMoveset == null || scanResult.selectedMoveset.getPvpMasterScore() == null) ? 0 : scanResult.selectedMoveset.getPvpMasterScore();

        int cp = scanResult.cp;
        if(cp <= 1500) return new PVPMark(PVPLeague.GREAT, pvpGreatScore);
        if(cp <= 2500) return new PVPMark(PVPLeague.ULTRA, pvpUltraScore);
        return new PVPMark(PVPLeague.MASTER, pvpMasterScore);
    }

    private PVPMark GetPVPMark(Pokemon evolvedPokemon, double aeCP){
        String name = evolvedPokemon.name.split(" - ")[0].trim().toUpperCase();
        if(aeCP<=1500){
            String[] topArray = new String[]{"REGISTEEL", "AZUMARILL", "SKARMORY", "ALTARIA", "BASTIODON", "MEDICHAM", "HYPNO", "PROBOPASS", "SABLEYE", "TROPIUS", "MELMETAL", "WHISCASH", "QUAGSIRE", "TOXICROAK", "STEELIX", "LANTURN", "CRESSELIA", "VIGOROTH", "CLEFABLE", "JIRACHI", "ZWEILOUS", "BRONZONG", "LAPRAS", "HAUNTER", "MANTINE", "REGICE", "REGIROCK", "IVYSAUR", "LUGIA", "FORRETRESS", "UXIE", "SHIFTRY", "GALLADE", "WIGGLYTUFF", "MAWILE", "VICTREEBEL", "TOGEKISS", "SKUNTANK"};
            List<String> topList = Arrays.asList(topArray);
            if(topList.contains(name)) return new PVPMark(PVPLeague.GREAT, 0.5);
            return new PVPMark(PVPLeague.GREAT, 0);
        }

        if(aeCP<=2500){
            String[] topArray = new String[]{"REGISTEEL", "GIRATINA", "REGICE", "SNORLAX", "POLIWRATH", "REGIROCK", "KANGASKHAN", "STEELIX", "UXIE", "CRESSELIA", "CLEFABLE", "GYARADOS", "TOGEKISS", "SUICUNE", "LAPRAS", "MELMETAL", "POLITOED", "WHISCASH", "LUGIA", "DRAPION", "TOXICROAK", "ZANGOOSE", "KINGDRA", "FLYGON", "SHIFTRY", "CONKELDURR", "DRAGONITE", "SEISMITOAD", "MACHAMP", "JIRACHI", "GARCHOMP", "PRIMEAPE", "CLOYSTER", "DRIFBLIM", "GRANBULL", "HARIYAMA", "GOLURK", "URSARING", "HONCHKROW", "GALLADE", "HERACROSS", "BLAZIKEN", "LUCARIO", "GLISCOR", "DONPHAN", "VIRIZION", "BRELOOM"};
            List<String> topList = Arrays.asList(topArray);
            if(topList.contains(name)) return new PVPMark(PVPLeague.ULTRA, 0.5);
            return new PVPMark(PVPLeague.ULTRA, 0);
        }

        String[] topArray = new String[]{"DIALGA", "GIRATINA", "TOGEKISS", "MELMETAL", "SNORLAX", "HEATRAN", "GARCHOMP", "REGICE", "REGIROCK", "LUGIA", "KYOGRE", "ARTICUNO", "GROUDON", "CONKELDURR", "DARKRAI", "HYDREIGON", "DRAGONITE", "MACHAMP", "TYRANITAR", "RHYPERIOR", "HARIYAMA", "MAGNEZONE", "ZAPDOS", "LATIOS", "HERACROSS", "BLAZIKEN", "LUCARIO", "GYARADOS", "MAMOSWINE", "RAIKOU", "SUICUNE", "EXCADRILL", "PALKIA", "URSARING", "MILOTIC", "RAYQUAZA", "REGIGIGAS", "SCIZOR", "JIRACHI", "LEAFEON", "LATIAS", "REGISTEEL", "GALLADE", "AGGRON", "MOLTRES", "WEAVILE", "GARDEVOIR", "VAPOREON", "VIRIZION"};
        List<String> topList = Arrays.asList(topArray);
        if(topList.contains(name)) return new PVPMark(PVPLeague.MASTER, 0.5);
        return new PVPMark(PVPLeague.MASTER, 0);
    }

    private double GetMinLeagueRate(PVPLeague league){
        switch (league){
            case GREAT:
                return 0.7;
            case ULTRA:
                return 0.6;
            case MASTER:
                return 0.5;
        }
        return 0;
    }

    private String Badge(Pokemon evolvedPokemon, Pokemon pokemon, ScanResult scanResult, boolean isFinalForm, PVPMark pvpMark) {
        GymType gymType = GetGymType(evolvedPokemon);
        if (pvpMark.Mark >= GetMinLeagueRate(pvpMark.League) && isFinalForm) return GetLeagueBadge(pvpMark.League, true);
        if (!isFinalForm && pvpMark.Mark >= 0.5) return GetLeagueBadge(pvpMark.League, false);
        if (!isFinalForm && GetGymType(pokemon) == GymType.DEFENSIVE && scanResult.selectedMoveset.getDefScore() >= 0.95)
            return "Θ";
        if (!isFinalForm && GetGymType(pokemon) == GymType.DEFENSIVE) return "θ";
        if (!isFinalForm) return "";
        if ((gymType == GymType.DEFENSIVE || gymType == GymType.UNIVERSAL) && scanResult.selectedMoveset.getDefScore() == 1.0)
            return "Θ";
        if (scanResult.selectedMoveset.getAtkScore() == 1.0)
            return "Ψ";
        if (gymType == GymType.DEFENSIVE || gymType == GymType.UNIVERSAL && scanResult.selectedMoveset.getDefScore() >= 0.95)
            return "θ";
        if ((gymType == GymType.OFFENSIVE || gymType == GymType.UNIVERSAL) && scanResult.selectedMoveset.getAtkScore() >= 0.95)
            return "ψ";
        return "";
    }

    private String GetLeagueBadge(PVPLeague league, boolean upper){
        switch (league) {
            case GREAT:
                return upper ? "Ξ" : "ξ";
            case ULTRA:
                return upper ? "Υ" : "υ";
            case MASTER:
                return upper ? "Ω" : "ω";
        }
        return "";
    }

    private GymType GetGymType(Pokemon pokemon){
        GymType gymType = GymType.UNIVERSAL;
        int baseDefSta = (int) Math.floor(Math.sqrt(pokemon.baseDefense) * Math.sqrt(pokemon.baseStamina));
        if (pokemon.baseAttack < 198 && baseDefSta >= 180 || pokemon.baseStamina >= 280) gymType = GymType.DEFENSIVE;
        if (pokemon.baseAttack >= 198 && baseDefSta < 180) gymType = GymType.OFFENSIVE;
        return gymType;
    }

    private String GetIVBadge(IVCombination IV, boolean IsLucky)
    {
        double ivValue = 100.0*(IV.att+IV.def+IV.sta)/45.0;
        if (ivValue <= 49) return "·";
        if (ivValue > 49 && ivValue < 64.5) return "*";
        if (ivValue >= 64.5 && ivValue <= 80 && !IsLucky) return "⁑";
        if (ivValue > 80 && ivValue < 90 && !IsLucky) return "⁂";
        if (ivValue >= 90 && ivValue < 100 && !IsLucky || ivValue < 90 && IsLucky) return "☆";
        if (ivValue==100.0 && !IsLucky || IsLucky && ivValue >= 90) return "★";
        return "·";
    }

    public String GetShortName(String name, int len)
    {
        name = name.split(" - ")[0].trim();
	    String other = name.substring(1, name.length()-1);
        String first = name.substring(0,1);
        String last =  name.substring(name.length()-1);
        for (int i = 0; i < name.length()-len; i++)
        {
            int a = StringUtils.lastIndexOfAny(other, new String[] {"a", "e", "u", "i", "o", "y"});
            if (a > -1){
                StringBuilder sb = new StringBuilder(other);
                sb.deleteCharAt(a);
                other = sb.toString();
            }
        }

        String res = first+other+last;
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


