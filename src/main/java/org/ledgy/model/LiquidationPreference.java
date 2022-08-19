package org.ledgy.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LiquidationPreference {
    Participation participation;
    ProfitCap cap;

}
