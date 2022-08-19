package org.ledgy.model;

import lombok.Getter;

@Getter
public enum Round {
    SERIES_C("SERIES_C", LiquidationPreference.builder().participation(Participation.PARTICIPATING_1X).cap(ProfitCap.CAPPED_2X).build(), 1),
    SERIES_B("SERIES B", LiquidationPreference.builder().participation(Participation.PARTICIPATING_1X).cap(ProfitCap.CAPPED_2X).build(), 2),
    SERIES_A("SERIES A", LiquidationPreference.builder().participation(Participation.PARTICIPATING_1X).cap(ProfitCap.CAPPED_2X).build(),3),
    COMMON("COMMON", LiquidationPreference.builder().build(), 4);

    public final String name;
    public final LiquidationPreference liquidationPreference;

    public final int priority;

    Round(String name, LiquidationPreference liquidationPreference, int priority) {
        this.name = name;
        this.liquidationPreference = liquidationPreference;
        this.priority = priority;
    }
}
