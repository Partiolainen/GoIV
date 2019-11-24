package com.kamron.pogoiv.clipboardlogic.tokens;

public class PartIndexCustomSepToken extends PartIndexToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv
     */
    public PartIndexCustomSepToken(boolean maxEv) {
        super(maxEv, "");
    }

}
