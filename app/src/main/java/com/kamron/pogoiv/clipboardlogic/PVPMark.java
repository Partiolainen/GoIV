package com.kamron.pogoiv.clipboardlogic;

import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.Arrays;
import java.util.List;

import static com.kamron.pogoiv.Pokefly.scanResult;

public class PVPMark {

    private boolean _isFinalForm;
    private ScanResult _scanResult;
    private PokeInfoCalculator _pokeInfoCalculator;
    private double _aeCP;
    private double _mlCP;
    private Pokemon _evolvedPokemon;

    private Double _pvpGreatScore = 0.0;
    private Double _pvpUltraScore = 0.0;
    private Double _pvpMasterScore = 0.0;

    public PVPMark(ScanResult scanResult, PokeInfoCalculator pokeInfoCalculator) {
        _scanResult = scanResult;
        _pokeInfoCalculator = pokeInfoCalculator;
        Pokemon pokemon = scanResult.pokemon;
        _isFinalForm = pokemon.getEvolutions().isEmpty() && scanResult.selectedMoveset != null;

        List<Pokemon> evolutionLine = pokemon.getEvolutions(); //pokeInfoCalculator.getEvolutionLine(pokemon);
        _evolvedPokemon = evolutionLine.size() == 0 ? pokemon : evolutionLine.get(evolutionLine.size() - 1);
        evolutionLine = _evolvedPokemon.getEvolutions();
        _evolvedPokemon = evolutionLine.size() == 0 ? _evolvedPokemon : evolutionLine.get(evolutionLine.size() - 1);
        IVCombination iv = scanResult.getHighestIVCombination();
        _aeCP = pokeInfoCalculator.getCpRangeAtLevel(_evolvedPokemon, iv, iv, scanResult.levelRange.min).high;
        _mlCP = pokeInfoCalculator.getCpRangeAtLevel(_evolvedPokemon, iv, iv, 40).high;

        _pvpGreatScore = (_scanResult == null || _scanResult.selectedMoveset == null || _scanResult.selectedMoveset.getPvpGreatScore() == null) ? 0 : _scanResult.selectedMoveset.getPvpGreatScore();
        _pvpUltraScore = (_scanResult == null || _scanResult.selectedMoveset == null || _scanResult.selectedMoveset.getPvpUltraScore() == null) ? 0 : _scanResult.selectedMoveset.getPvpUltraScore();
        _pvpMasterScore = (_scanResult == null || _scanResult.selectedMoveset == null || _scanResult.selectedMoveset.getPvpMasterScore() == null) ? 0 : _scanResult.selectedMoveset.getPvpMasterScore();

        League = getLeague();
        Mark = getScore();
        Rate = getRate();
        MaxRate = getMaxRate();
    }

    /*public PVPMark(PVPLeague league, double mark) {
        League = league;
        Mark = mark;
    }*/

    public PVPLeague League;
    public double Mark;
    public double Rate;
    public double MaxRate;

    private PVPLeague getLeague() {
        if (_isFinalForm) {
            PVPLeague league = PVPLeague.MASTER;
            if (_aeCP <= 1500) league = PVPLeague.GREAT;
            if ((_pvpUltraScore > _pvpGreatScore || _aeCP > 1500) && _pvpUltraScore >= _pvpMasterScore && _aeCP <= 2500)
                league = PVPLeague.ULTRA;
            if ((_pvpMasterScore > _pvpUltraScore || _aeCP > 2500) && _pvpMasterScore >= _pvpGreatScore)
                league = PVPLeague.MASTER;

            return league;
        }

        if (_aeCP <= 1500) return PVPLeague.GREAT;
        if (_aeCP <= 2500) return PVPLeague.ULTRA;
        return PVPLeague.MASTER;
    }

    private double getScore() {
        if (_isFinalForm) {

            switch (League) {
                case GREAT:
                    return _pvpGreatScore;
                case ULTRA:
                    return _pvpUltraScore;
                case MASTER:
                    return _pvpMasterScore;
            }
        }

        String name = _evolvedPokemon.name.split(" - ")[0].trim().toUpperCase();
        if (_aeCP <= 1500) {
            String[] topArray = new String[]{"REGISTEEL", "ALTARIA", "SKARMORY", "AZUMARILL", "UMBREON", "PROBOPASS", "SABLEYE", "DEOXYS", "MEDICHAM", "HYPNO", "BASTIODON", "TROPIUS", "WHISCASH", "LAPRAS", "CLEFABLE", "ZWEILOUS", "LANTURN", "VIGOROTH", "JIRACHI", "CRESSELIA", "BRONZONG", "STEELIX", "TOXICROAK", "MELMETAL"};
            List<String> topList = Arrays.asList(topArray);
            if (topList.contains(name)) return 0.5;
            return 0.0;
        }

        if (_aeCP <= 2500) {
            String[] topArray = new String[]{"REGISTEEL", "GIRATINA", "REGICE", "SNORLAX", "POLIWRATH", "SUICUNE", "LAPRAS", "GYARADOS", "CLEFABLE", "UXIE", "REGIROCK", "CRESSELIA", "STEELIX", "KANGASKHAN", "TOGEKISS", "DRAGONITE", "UMBREON", "KINGDRA", "WHISCASH", "CONKELDURR", "LUGIA", "FLYGON", "SEISMITOAD", "SHIFTRY", "POLITOED", "JIRACHI", "WIGGLYTUFF", "MACHAMP", "GARCHOMP", "TOXICROAK", "ZANGOOSE", "DRAPION", "MELMETAL"};
            List<String> topList = Arrays.asList(topArray);
            if (topList.contains(name)) return 0.5;
            return 0.0;
        }

        String[] topArray = new String[]{"DIALGA", "GIRATINA", "TOGEKISS", "HEATRAN", "SNORLAX", "GARCHOMP", "MELMETAL", "DRAGONITE", "HYDREIGON", "CONKELDURR", "LUGIA", "ARTICUNO", "REGICE", "KYOGRE", "REGIROCK", "MACHAMP", "GROUDON", "DARKRAI", "RHYPERIOR", "TYRANITAR", "SUICUNE", "LUCARIO", "LATIOS", "HARIYAMA", "BLAZIKEN", "RAIKOU", "GYARADOS", "MAGNEZONE", "HERACROSS", "MAMOSWINE"};
        List<String> topList = Arrays.asList(topArray);
        if (topList.contains(name)) return 0.5;
        return 0.0;
    }

    private double getRate(){
        if(_isFinalForm){
            switch (League) {
                case GREAT:
                    return _pvpGreatScore * _aeCP / 1500.0;
                case ULTRA:
                    return _pvpUltraScore * _aeCP / 2500.0;
                case MASTER:
                    return _pvpMasterScore * _aeCP / _mlCP;
            }
        }

        switch (League) {
            case GREAT:
                return 0.5 * _aeCP / 1500.0;
            case ULTRA:
                return 0.5 * _aeCP / 2500.0;
            case MASTER:
                return 0.5 * _aeCP / _mlCP;
        }

        return 0;
    }

    private double getMaxRate(){
        double targetCP = 0.0;
        switch (League) {
            case GREAT:
                targetCP = 1500;
                break;
            case ULTRA:
                targetCP = 2500;
                break;
            case MASTER:
                targetCP = _mlCP;
                break;
        }

        double level = scanResult.levelRange.max;
        IVCombination iv = scanResult.getHighestIVCombination();
        double cpAtLev = _aeCP;
        while (targetCP > cpAtLev) {
            if(level>=40) break;
            level = level + 0.5;
            double tCP = _pokeInfoCalculator.getCpRangeAtLevel(_evolvedPokemon, iv, iv, level).high;
            if (tCP>targetCP) break;
            cpAtLev = tCP;
        }

        if (!_isFinalForm) {
            switch (League) {
                case GREAT:
                    return getMinLeagueRate(PVPLeague.GREAT)* cpAtLev / 1500.0;
                case ULTRA:
                    return getMinLeagueRate(PVPLeague.ULTRA)* cpAtLev / 2500.0;
                case MASTER:
                    return getMinLeagueRate(PVPLeague.MASTER)* cpAtLev / _mlCP;
            }
        }
        switch (League) {
            case GREAT:
                return _pvpGreatScore * cpAtLev / 1500.0;
            case ULTRA:
                return _pvpUltraScore * cpAtLev / 2500.0;
            case MASTER:
                return _pvpMasterScore * cpAtLev / _mlCP;
        }

        return 0;
    }

    private double getMinLeagueRate(PVPLeague league){
        switch (league){
            case GREAT:
                return 0.6;
            case ULTRA:
                //return 0.6;
            case MASTER:
                return 0.5;
        }
        return 0;
    }
}
