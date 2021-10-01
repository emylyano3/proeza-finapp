package proeza.finapp.domain;

import java.math.RoundingMode;

public enum DecimalType {
    CHARGE(3, RoundingMode.FLOOR),
    RATE(2, RoundingMode.FLOOR),
    ASSET_PRICE(2, RoundingMode.FLOOR),
    ACCOUNT_BALANCE(2, RoundingMode.FLOOR);

    DecimalType(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    private final int scale;
    private final RoundingMode roundingMode;

    public int scale() {
        return scale;
    }

    public RoundingMode roundingMode() {
        return roundingMode;
    }
}
