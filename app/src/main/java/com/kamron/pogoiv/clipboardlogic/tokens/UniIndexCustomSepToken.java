package com.kamron.pogoiv.clipboardlogic.tokens;

public class UniIndexCustomSepToken extends UniIndexToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     * @param maxEv
     */
    public UniIndexCustomSepToken(boolean maxEv) {
        super(maxEv, "");
    }

}
