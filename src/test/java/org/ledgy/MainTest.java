package org.ledgy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ledgy.model.ExitValuation;
import org.ledgy.model.InvestmentRound;
import org.ledgy.model.Round;
import org.ledgy.model.Stacking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    Main main;
    InvestmentRound commonRound = InvestmentRound.builder().round(Round.COMMON).shares(1000000).investment(0).build();
    InvestmentRound roundA = InvestmentRound.builder().round(Round.SERIES_A).shares(200000).investment(900000).build();
    InvestmentRound roundB = InvestmentRound.builder().round(Round.SERIES_B).shares(300000).investment(2100000).build();
    InvestmentRound roundC = InvestmentRound.builder().round(Round.SERIES_C).shares(1500000).investment(15000000).build();
    List<InvestmentRound> investmentRounds;

    Stacking stacking;

    @BeforeEach
    void setUp(){
        main = new Main();
        investmentRounds= new ArrayList<>();
        investmentRounds.add(commonRound);
        investmentRounds.add(roundA);
        investmentRounds.add(roundB);
        investmentRounds.add(roundC);
    }

    @AfterEach
    void printResults(){
        System.out.println(stacking);
    }

    @Test
    void executeStage1() {
        System.out.println("Exit valuation : " + 60000000);
        stacking = Main.execute(investmentRounds, 60000000);
        HashMap<InvestmentRound, ExitValuation> exitValues = stacking.getExitValues();

        assertEquals(20000000, exitValues.get(commonRound).getExitValue() );
        assertEquals(4000000, exitValues.get(roundA).getExitValue() );
        assertEquals(6000000, exitValues.get(roundB).getExitValue() );
        assertEquals(30000000, exitValues.get(roundC).getExitValue() );
    }

    @Test
    void executeStage2() {
        System.out.println("Exit valuation : " + 25000000);
        stacking = Main.execute(investmentRounds, 25000000);
        HashMap<InvestmentRound, ExitValuation> exitValues = stacking.getExitValues();

        assertEquals(2333333.33, exitValues.get(commonRound).getExitValueWithTwoDecimal() );
        assertEquals(1366666.67, exitValues.get(roundA).getExitValueWithTwoDecimal() );
        assertEquals(2800000, exitValues.get(roundB).getExitValueWithTwoDecimal() );
        assertEquals(18500000, exitValues.get(roundC).getExitValueWithTwoDecimal() );
    }

    @Test
    void executeStage3() {
        System.out.println("Exit valuation : " + 35000000);
        stacking = Main.execute(investmentRounds, 35000000);
        HashMap<InvestmentRound, ExitValuation> exitValues = stacking.getExitValues();

        assertEquals(5750000, exitValues.get(commonRound).getExitValueWithTwoDecimal() );
        assertEquals(1800000, exitValues.get(roundA).getExitValueWithTwoDecimal() );
        assertEquals(3825000, exitValues.get(roundB).getExitValueWithTwoDecimal() );
        assertEquals(23625000, exitValues.get(roundC).getExitValueWithTwoDecimal() );
    }

    @Test
    void executeStage4() {
        System.out.println("Exit valuation : " + 45000000);
        stacking = Main.execute(investmentRounds, 45000000);
        HashMap<InvestmentRound, ExitValuation> exitValues = stacking.getExitValues();

        assertEquals(9555555.56, exitValues.get(commonRound).getExitValueWithTwoDecimal() );
        assertEquals(1911111.11, exitValues.get(roundA).getExitValueWithTwoDecimal() );
        assertEquals(4200000, exitValues.get(roundB).getExitValueWithTwoDecimal() );
        assertEquals(29333333.33, exitValues.get(roundC).getExitValueWithTwoDecimal() );
    }

    @Test
    void executeStage5() {
        System.out.println("Exit valuation : " + 40000000);
        stacking = Main.execute(investmentRounds, 40000000);
        HashMap<InvestmentRound, ExitValuation> exitValues = stacking.getExitValues();

        assertEquals(7600000.00, exitValues.get(commonRound).getExitValueWithTwoDecimal() );
        assertEquals(1800000.00, exitValues.get(roundA).getExitValueWithTwoDecimal() );
        assertEquals(4200000, exitValues.get(roundB).getExitValueWithTwoDecimal() );
        assertEquals(26400000, exitValues.get(roundC).getExitValueWithTwoDecimal() );
    }

    @Test
    void executeStage6() {
        System.out.println("Exit valuation : " + 50000000);
        stacking = Main.execute(investmentRounds, 50000000);
        HashMap<InvestmentRound, ExitValuation> exitValues = stacking.getExitValues();

        assertEquals(13166666.67, exitValues.get(commonRound).getExitValueWithTwoDecimal() );
        assertEquals(2633333.33, exitValues.get(roundA).getExitValueWithTwoDecimal() );
        assertEquals(4200000, exitValues.get(roundB).getExitValueWithTwoDecimal() );
        assertEquals(30000000, exitValues.get(roundC).getExitValueWithTwoDecimal() );
    }

    @Test
    void executeStage7() {
        System.out.println("Exit valuation : " + 70000000);
        stacking = Main.execute(investmentRounds, 70000000);
        HashMap<InvestmentRound, ExitValuation> exitValues = stacking.getExitValues();

        assertEquals(23333333.33, exitValues.get(commonRound).getExitValueWithTwoDecimal() );
        assertEquals(4666666.67, exitValues.get(roundA).getExitValueWithTwoDecimal() );
        assertEquals(7000000, exitValues.get(roundB).getExitValueWithTwoDecimal() );
        assertEquals(35000000, exitValues.get(roundC).getExitValueWithTwoDecimal() );
    }
}