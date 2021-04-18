package proeza.finapp.entities;

import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum TipoMovimiento {
    COMPRA("C"),
    VENTA("V");

    String id;

    TipoMovimiento(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TipoMovimiento fromId(String id) {
        return id == null
                ? null
                : Arrays.stream(TipoMovimiento.values())
                .filter(tm -> id.equals(tm.getId()))
                .findAny()
                .orElse(null);
    }
}
