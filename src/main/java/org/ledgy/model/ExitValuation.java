package org.ledgy.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@Builder
public class ExitValuation {
    double commonShareExitValue;

    double preferenceExitValue;

    boolean capHit;

    public boolean prefersPreferenceExit() {
        return preferenceExitValue>commonShareExitValue;
    }

    public double getExitValue(){
        return Math.max(commonShareExitValue, preferenceExitValue);
    }

    public double getExitValueWithTwoDecimal(){
        return  Math.round(getExitValue() * 100.0) / 100.0;
    }

    public String getExitType(){
        return commonShareExitValue>=preferenceExitValue?" (COMMON) ":" (PREFERENCE) ";
    }
}
