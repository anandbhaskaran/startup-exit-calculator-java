package org.ledgy.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class InvestmentRound {

    Round round;

    int shares;

    double investment;



}
