package proeza.finapp.domain;

import java.math.RoundingMode;

public enum ValueScale {
    CHARGE_SCALE(3, RoundingMode.FLOOR),
    PRICE_SCALE(2, RoundingMode.FLOOR),
    ACCOUNT_BALANCE(2, RoundingMode.FLOOR);

    ValueScale(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    private final int scale;
    private final RoundingMode roundingMode;

    public int getScale() {
        return scale;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }
}
