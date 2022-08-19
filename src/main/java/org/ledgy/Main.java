package org.ledgy;

import org.ledgy.model.InvestmentRound;
import org.ledgy.model.Round;
import org.ledgy.model.Stacking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        InvestmentRound commonRound = InvestmentRound.builder().round(Round.COMMON).shares(1000000).investment(0).build();
        InvestmentRound roundA = InvestmentRound.builder().round(Round.SERIES_A).shares(200000).investment(900000).build();
        InvestmentRound roundB = InvestmentRound.builder().round(Round.SERIES_B).shares(300000).investment(2100000).build();
        InvestmentRound roundC = InvestmentRound.builder().round(Round.SERIES_C).shares(1500000).investment(15000000).build();

        List<InvestmentRound> investmentRounds = new ArrayList<>();
        investmentRounds.add(commonRound);
        investmentRounds.add(roundA);
        investmentRounds.add(roundB);
        investmentRounds.add(roundC);

        execute(investmentRounds, 45000000);
    }

    public static Stacking execute(List<InvestmentRound> investmentRounds, double exitValuation) {
        Stacking staking = setupInvestments(investmentRounds);
        staking.computeExit(exitValuation, new HashMap<>());
        return staking;
    }

    private static Stacking setupInvestments(List<InvestmentRound> investmentRounds) {
        Stacking stacking = new Stacking();
        stacking.addAll(investmentRounds);

        return stacking;

    }
}