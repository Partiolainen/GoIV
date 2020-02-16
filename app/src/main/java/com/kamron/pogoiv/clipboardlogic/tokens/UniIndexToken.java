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
            String rate_mark = (pvpMark.Mark >= 0.6 && isFinalForm || !isFinalForm && pvpMark.Mark >= 0.5)
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
        if(aeCP<=1500) targetCP = 1500;
        if(aeCP<=2500) targetCP = 2500;
        double level = scanResult.levelRange.max;
        IVCombination iv = scanResult.getHighestIVCombination();
        double cpAtLev = 0;
        while (targetCP > cpAtLev) {
            if(level>=40) break;
            level = level + 0.5;
            double tCP = calc.getCpRangeAtLevel(evolvedPokemon, iv, iv, level).high;
            if (tCP>targetCP) break;
            cpAtLev = tCP;
        }

        int cp = scanResult.cp;
        if (!isFinalForm) {
            if (aeCP <= 1500) return 0.6 * cpAtLev / 1500.0;
            if (aeCP <= 2500) return 0.6 * cpAtLev / 2500.0;
            return 0.6 * cpAtLev / mlCP;
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
            String[] topArray = new String[]{"DIALGA", "LATIAS", "GIRATINA", "KYUREM", "ZEKROM", "RESHIRAM", "REGISTEEL", "MEW", "ARCEUS", "URSARING", "AZUMARILL", "PALKIA", "MEGANIUM", "VENUSAUR", "LANTURN", "SKARMORY", "TRANQUILL", "UNFEZANT", "KYOGRE", "MANAPHY", "MEWTWO", "MILOTIC", "PHIONE", "ALTARIA", "DITTO", "FERROTHORN", "LUDICOLO", "POLITOED", "POLIWRATH", "PRINPLUP", "TOGEKISS", "DEOXYS", "JIRACHI", "JELLICENT", "TROPIUS", "CLEFABLE", "SCRAFTY", "CRESSELIA", "LEAVANNY", "TORTERRA", "WHIMSICOTT", "SABLEYE", "SHEDINJA", "KELDEO", "SALAMENCE", "BASTIODON", "GRANBULL", "BELLOSSOM", "LEAFEON", "NUZLEAF", "SHIFTRY", "VICTREEBEL", "DEINO", "DRAGONAIR", "GYARADOS", "HYDREIGON", "SEADRA", "SHELGON", "ZWEILOUS", "GLOOM", "IVYSAUR", "ODDISH", "ROSELIA", "ROSERADE", "SUNFLORA", "VILEPLUME", "WEEPINBELL", "BRONZONG", "STEELIX", "LUNATONE", "ESCAVALIER", "HERACROSS", "RAMPARDOS", "FORRETRESS", "HEATRAN", "MAGMORTAR", "MAROWAK", "MOLTRES", "NINETALES", "RAPIDASH", "SIMISEAR", "WIGGLYTUFF", "AZURILL", "FRILLISH", "LOMBRE", "MANTINE", "MANTYKE", "MELOETTA", "STARAPTOR", "VIRIZION", "BRONZOR", "DROWZEE", "GOTHITA", "HYPNO", "JYNX", "MEDITITE", "MIME_JR", "RALTS", "SLOWPOKE", "WOOBAT", "MANDIBUZZ", "TORNADUS", "VULLABY", "LUCARIO", "MEDICHAM", "REGIROCK", "GALLADE", "GARDEVOIR", "GOTHITELLE", "ELECTRODE", "GALVANTULA", "JOLTEON", "KLINK", "LAPRAS", "SERPERIOR", "SERVINE", "BAYLEEF", "BUDEW", "COTTONEE", "PARASECT", "RAYQUAZA", "SUNKERN", "MELMETAL", "GEODUDE", "GRAVELER", "PACHIRISU", "RAIKOU", "STUNFISK", "BRELOOM", "EMPOLEON", "FERALIGATR", "CHERRIM", "KLANG", "KLINKLANG", "MAGNETON", "NOCTOWL", "SWELLOW", "VIGOROTH", "GROUDON", "BLAZIKEN", "ALOMOMOLA", "KINGDRA", "LUMINEON", "SAMUROTT", "WALREIN", "CONKELDURR", "HARIYAMA", "TOXICROAK", "EMOLGA", "MAGNEMITE", "MAREEP", "MELTAN", "PICHU", "PIKACHU", "ROTOM", "SIMISAGE", "TANGROWTH", "ZAPDOS", "AMOONGUSS", "FOONGUS", "SIGILYPH", "TYPHLOSION", "REGICE", "AMPHAROS", "HITMONCHAN", "HITMONTOP", "MACHAMP", "PRIMEAPE", "UXIE", "VICTINI", "GROTLE", "TURTWIG", "MAWILE", "MIGHTYENA", "JUMPLUFF", "METAGROSS", "RIOLU", "FLOATZEL", "SHARPEDO", "ABOMASNOW", "PROBOPASS", "RAICHU", "LUGIA", "BLASTOISE", "SWAMPERT", "TERRAKION", "MAGNEZONE"};
            List<String> topList = Arrays.asList(topArray);
            if(topList.contains(name)) return new PVPMark(PVPLeague.GREAT, 0.5);
            return new PVPMark(PVPLeague.GREAT, 0);
        }

        if(aeCP<=2500){
            String[] topArray = new String[]{"CRESSELIA", "LUNATONE", "GIRATINA", "REGISTEEL", "JIRACHI", "KYUREM", "JELLICENT", "SABLEYE", "SHEDINJA", "EMPOLEON", "FERALIGATR", "SCRAFTY", "TOGEKISS", "CLEFABLE", "WHIMSICOTT", "KINGDRA", "GARDEVOIR", "NINETALES", "POLIWRATH", "WIGGLYTUFF", "LUCARIO", "SWAMPERT", "UXIE", "LUGIA", "MEGANIUM", "VENUSAUR", "DIALGA", "LATIAS", "ESCAVALIER", "HERACROSS", "TYPHLOSION", "GALLADE", "GOTHITELLE", "HYDREIGON", "ZWEILOUS", "REGICE", "NOCTOWL", "REGIROCK", "ARTICUNO", "CLOYSTER", "DELIBIRD", "DEWGONG", "GLACEON", "SEEL", "SHELLDER", "METAGROSS", "PALKIA", "RESHIRAM", "MELMETAL", "AMPHAROS", "BLASTOISE", "JOLTEON", "PACHIRISU", "TORTERRA", "GRANBULL", "BRELOOM", "CONKELDURR", "HARIYAMA", "MACHAMP", "MEDICHAM", "MEW", "TOXICROAK", "LEAVANNY", "AGGRON", "BASTIODON", "BOLDORE", "RHYPERIOR", "ROGGENROLA", "SEADRA", "TYRANITAR", "FERROTHORN", "GENGAR", "HAUNTER", "LICKILICKY", "FLYGON", "RAIKOU", "GLISCOR", "NIDOKING", "SCEPTILE", "BANETTE", "PRIMEAPE", "RIOLU", "POLITOED", "ZEKROM", "MEWTWO", "MUK", "TANGROWTH", "RAICHU", "DRAGONITE", "GYARADOS", "SHIFTRY", "LAPRAS", "HITMONCHAN", "HITMONTOP", "MAGNETON", "MAGNEZONE", "URSARING", "ARCEUS", "LATIOS", "CELEBI", "DEOXYS", "KADABRA", "KROOKODILE", "MILOTIC", "KLINKLANG", "MELTAN", "BRONZONG", "HONCHKROW", "FORRETRESS", "MANDIBUZZ", "VULLABY", "LANTURN", "EXEGGCUTE", "EXEGGUTOR", "SCIZOR", "STEELIX", "LEAFEON", "GOLEM", "SNORLAX", "VICTINI"};
            List<String> topList = Arrays.asList(topArray);
            if(topList.contains(name)) return new PVPMark(PVPLeague.ULTRA, 0.5);
            return new PVPMark(PVPLeague.ULTRA, 0);
        }

        String[] topArray = new String[]{"DIALGA", "LATIAS", "KYUREM", "PALKIA", "RESHIRAM", "ARCEUS", "URSARING", "ZEKROM", "GARCHOMP", "LANDORUS", "METAGROSS", "HYDREIGON", "ZWEILOUS", "GIRATINA", "LUGIA", "MEW", "MELMETAL", "TYRANITAR", "TOGEKISS", "KYOGRE", "MANAPHY", "MILOTIC", "PHIONE", "JIRACHI", "DRAGONITE", "LATIOS", "SABLEYE", "SHEDINJA", "GYARADOS", "KINGDRA", "NOCTOWL", "SALAMENCE", "RAIKOU", "RAYQUAZA", "RHYPERIOR", "TERRAKION", "GARDEVOIR", "KROOKODILE", "DARKRAI", "MANDIBUZZ", "REGIGIGAS", "MEWTWO", "REGIROCK", "HOUNDOUR", "MURKROW", "PERSIAN", "UMBREON", "VULLABY", "ZORUA", "GROUDON", "WEAVILE", "AGGRON", "FLYGON", "BASTIODON", "BOLDORE", "ROGGENROLA", "FERROTHORN", "IGGLYBUFF", "SPIRITOMB", "HEATRAN", "EMPOLEON", "FERALIGATR", "ARTICUNO", "CLOYSTER", "DELIBIRD", "GLACEON", "SEEL", "SHELLDER", "SEADRA", "MELOETTA", "STARAPTOR", "VIRIZION", "SNORLAX", "DRUDDIGON", "FRAXURE", "HAXORUS", "HIPPOWDON", "MAGMORTAR", "MAROWAK", "MOLTRES", "RAPIDASH", "SIMISEAR"};
        List<String> topList = Arrays.asList(topArray);
        if(topList.contains(name)) return new PVPMark(PVPLeague.MASTER, 0.5);
        return new PVPMark(PVPLeague.MASTER, 0);
    }

    private String Badge(Pokemon evolvedPokemon, Pokemon pokemon, ScanResult scanResult, boolean isFinalForm, PVPMark pvpMark) {
        GymType gymType = GetGymType(evolvedPokemon);
        if (pvpMark.Mark >= 0.6 && isFinalForm) return GetLeagueBadge(pvpMark.League, true);
        if (!isFinalForm && pvpMark.Mark >= 0.5) return GetLeagueBadge(pvpMark.League, false);
        if (!isFinalForm && GetGymType(pokemon) == GymType.DEFENSIVE && scanResult.selectedMoveset.getDefScore() > 0.95) return "Θ";
        if (!isFinalForm && GetGymType(pokemon) == GymType.DEFENSIVE) return "θ";
        if (!isFinalForm) return "";
        if ((gymType == GymType.DEFENSIVE || gymType == GymType.UNIVERSAL) && scanResult.selectedMoveset.getDefScore() == 1.0)
            return "Θ";
        if (gymType == GymType.DEFENSIVE || gymType == GymType.UNIVERSAL && scanResult.selectedMoveset.getDefScore() > 0.95)
            return "θ";
        if (scanResult.selectedMoveset.getAtkScore() == 1.0)
            return "Ψ";
        if ((gymType == GymType.OFFENSIVE || gymType == GymType.UNIVERSAL) && scanResult.selectedMoveset.getAtkScore() > 0.95)
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
                return upper ? "Σ" : "σ";
        }
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


