package com.kamron.pogoiv.scanlogic;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by Johan on 2018-02-19.
 *
 * A class which represents the data of a moveset, which is used by the MoveSetFraction to create the moveset list.
 */

public class MovesetData {
    private String fast;
    private String charge;
    private String charge2;
    private String fastKey;
    private String chargeKey;
    private String charge2Key;
    private boolean fastIsLegacy;
    private boolean chargeIsLegacy;
    private Boolean charge2IsLegacy;
    private Double atkScore;
    private Double defScore;
    private Double pvpGreatScore;
    private Double pvpUltraScore;
    private Double pvpMasterScore;
    private String fastMoveType;
    private String chargeMoveType;
    private String charge2MoveType;

    /**
     * Create a new Moveset.
     *
     * @param fast   Localized fast move name
     * @param charge Localized charge move name
     */
    public MovesetData(String fast, String charge, String charge2) {
        this.fast = fast;
        this.charge = charge;
        this.charge2 = charge2;
    }

    public MovesetData(String quick,
                       String charge,
                       String charge2,
                       boolean quickIsLegacy,
                       boolean chargeIsLegacy,
                       Boolean charge2IsLegacy,
                       Double atkScore,
                       Double defScore,
                       Double pvpGreatScore,
                       Double pvpUltraScore,
                       Double pvpMasterScore,
                       String chargeMoveType,
                       String charge2MoveType,
                       String quickMoveType) {

        this.fast = quick;
        this.charge = charge;
        this.charge2 = charge2;
        this.fastMoveType = quickMoveType;
        this.chargeMoveType = chargeMoveType;
        this.charge2MoveType = charge2MoveType;
        this.fastIsLegacy = quickIsLegacy;
        this.chargeIsLegacy = chargeIsLegacy;
        this.charge2IsLegacy = charge2IsLegacy;
        this.atkScore = atkScore;
        this.defScore = defScore;
        this.pvpGreatScore = pvpGreatScore;
        this.pvpUltraScore = pvpUltraScore;
        this.pvpMasterScore = pvpMasterScore;
    }

    /**
     * Create a new Moveset.
     *
     * @param fastKey        Unique key identifying the fast move
     * @param chargeKey      Unique key identifying the charge move
     * @param fast           Localized fast move name
     * @param charge         Localized charge move name
     * @param fastMoveType   Fast move type
     * @param chargeMoveType Charge move type
     * @param fastIsLegacy   Whether the fast move is legacy
     * @param chargeIsLegacy Whether the charge move is legacy
     * @param atkScore       A score for the attack power of this moveset
     * @param defScore       A score for the defense power of this moveset
     */
    public MovesetData(String fastKey, String chargeKey, String charge2Key,
                       String fast, String charge, String charge2,
                       String fastMoveType, String chargeMoveType, String charge2MoveType,
                       boolean fastIsLegacy, boolean chargeIsLegacy, Boolean charge2IsLegacy,
                       Double atkScore, Double defScore,
                       Double pvpGreatScore, Double pvpUltraScore, Double pvpMasterScore) {
        this.fastKey = fastKey;
        this.chargeKey = chargeKey;
        this.charge2Key = charge2Key;
        this.fast = fast;
        this.charge = charge;
        this.charge2 = charge2;
        this.fastMoveType = fastMoveType;
        this.chargeMoveType = chargeMoveType;
        this.charge2MoveType = charge2MoveType;
        this.fastIsLegacy = fastIsLegacy;
        this.chargeIsLegacy = chargeIsLegacy;
        this.charge2IsLegacy = charge2IsLegacy;
        this.atkScore = atkScore;
        this.defScore = defScore;
        this.pvpGreatScore = pvpGreatScore;
        this.pvpUltraScore = pvpUltraScore;
        this.pvpMasterScore = pvpMasterScore;
    }

    public String getFastMoveType() {
        return fastMoveType;
    }

    public String getChargeMoveType() {
        return chargeMoveType;
    }

    public String getCharge2MoveType() {
        return charge2MoveType;
    }

    public String getFast() { return fast; }

    public String getCharge() { return charge; }

    public String getCharge2() { return charge2; }

    public String getFastKey() {
        return fastKey;
    }

    public String getChargeKey() {
        return chargeKey;
    }

    public String getCharge2Key() {
        return charge2Key;
    }

    public boolean isFastIsLegacy() {
        return fastIsLegacy;
    }

    public boolean isChargeIsLegacy() {
        return chargeIsLegacy;
    }

    public Boolean isCharge2IsLegacy() {
        return charge2IsLegacy;
    }

    public Double getAtkScore() { return atkScore; }

    public Double getDefScore() {
        return defScore;
    }

    public Double getPvpGreatScore() {
        return pvpGreatScore;
    }

    public Double getPvpUltraScore() {
        return pvpUltraScore;
    }

    public Double getPvpMasterScore() {
        return pvpMasterScore;
    }


    public static class AtkComparator implements Comparator<MovesetData> {
        @Override
        public int compare(MovesetData movesetData, MovesetData other) {

            //The worst moves dont get a score, so they should always be at the end of the list.
            if (movesetData.getAtkScore() == null) {
                return 1;
            }
            if (other.getAtkScore() == null) {
                return -1;
            }
            return Double.compare(other.getAtkScore(), movesetData.getAtkScore());
        }
    }

    public static class DefComparator implements Comparator<MovesetData> {
        @Override
        public int compare(MovesetData movesetData, MovesetData other) {
            //The worst moves dont get a score, so they should always be at the end of the list.
            if (movesetData.getDefScore() == null) {
                return 1;
            }
            if (other.getDefScore() == null) {
                return -1;
            }
            return Double.compare(other.getDefScore(), movesetData.getDefScore());
        }
    }

    public static class Key implements Comparable<Key> {
        private final String quick;
        private final String charge;
        private final String charge2;

        public Key(String quick, String charge, String charge2) {
            this.quick = quick;
            this.charge = charge;
            this.charge2 = charge2;
        }

        public String getQuick() {
            return quick;
        }

        public String getCharge() {
            return charge;
        }

        public String getCharge2() {
            return charge2;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key) o;

            if (!Objects.equals(quick, key.quick)) return false;

            if (!Objects.equals(charge, key.charge)) return false;

            return Objects.equals(charge2, key.charge2);
        }

        @Override public int hashCode() {
            int result = quick != null ? quick.hashCode() : 0;
            result = 31 * result + (charge != null ? charge.hashCode() : 0);
            result = 31 * result + (charge2 != null ? charge2.hashCode() : 0);
            return result;
        }

        @Override public int compareTo(@NonNull Key other) {
            int retval = this.getQuick().compareTo(other.getQuick());
            if (retval == 0) {
                retval = this.getCharge().compareTo(other.getCharge());
            }
            if (retval == 0) {
                retval = this.getCharge2().compareTo(other.getCharge2());
            }
            return retval;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovesetData that = (MovesetData) o;
        return Objects.equals(fast, that.fast) && Objects.equals(charge, that.charge) && Objects.equals(charge2, that.charge2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fast, charge, charge2);
    }

}
