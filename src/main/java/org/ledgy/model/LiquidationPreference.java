package org.ledgy.model;

import lombok.Builder;

@Builder
public class LiquidationPreference {
    Participation participation;
    ProfitCap cap;
}
