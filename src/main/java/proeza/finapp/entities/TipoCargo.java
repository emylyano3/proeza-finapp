package proeza.finapp.entities;

import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum TipoCargo {
    IMPUESTO("I"),
    COMISION("C"),
    DERECHO_MERCADO("D"),
    OTRO("O");

    private String id;

    TipoCargo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TipoCargo fromId(String id) {
        return id == null
                ? null
                : Arrays.stream(TipoCargo.values())
                .filter(tm -> id.equals(tm.getId()))
                .findAny()
                .orElse(null);
    }
}
