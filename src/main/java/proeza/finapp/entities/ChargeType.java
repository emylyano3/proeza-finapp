package proeza.finapp.entities;

import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum ChargeType {
    IMPUESTO("I"),
    COMISION("C"),
    DERECHO_MERCADO("D"),
    OTRO("O");

    private String id;

    ChargeType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ChargeType fromId(String id) {
        return id == null
                ? null
                : Arrays.stream(ChargeType.values())
                .filter(tm -> id.equals(tm.getId()))
                .findAny()
                .orElse(null);
    }
}
