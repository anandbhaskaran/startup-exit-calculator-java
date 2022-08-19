package org.ledgy.model;

import lombok.Getter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Getter
public class Stacking extends AbstractList<InvestmentRound> {
    List<InvestmentRound> investmentRounds = new ArrayList<>();

    HashMap<InvestmentRound, Double> ownership = new HashMap<>();

    HashMap<InvestmentRound, ExitValuation> exitValues = new HashMap<>();

    double totalInvestment = 0;

    int totalShares = 0;


    @Override
    public InvestmentRound get(int index) {
        return investmentRounds.get(index);
    }

    @Override
    public void add(int position, InvestmentRound investmentRound) {
        investmentRounds.add(investmentRound);
        investmentRounds.sort(Comparator.comparingInt(r -> r.getRound().priority));
        totalInvestment += investmentRound.investment;
        totalShares += investmentRound.shares;
    }

    @Override
    public int size() {
        return investmentRounds.size();
    }

    public HashMap<InvestmentRound, ExitValuation> computeExit(double exitValuation, HashMap<InvestmentRound, ExitValuation> computedExitValues) {

        // Compute ownership
        for (InvestmentRound roundToBeComputed:investmentRounds) {
            ownership.put(roundToBeComputed, (double) roundToBeComputed.getShares() / (double) totalShares);
        }

        // Compute exit values
        for (InvestmentRound roundToBeComputed: investmentRounds) {
            double commonShareExitValuation = exitValuation * ownership.get(roundToBeComputed);
            double preferenceExitValuation = 0;
            boolean capHit = false;
            if(roundToBeComputed.getRound().liquidationPreference.participation == Participation.PARTICIPATING_1X) {
                double liquidityExitPreferenceValue = Math.min(exitValuation, roundToBeComputed.investment);
                double liquidityExitProfitValue = Math.max(0, (exitValuation - getPreferenceTotalInvestment(computedExitValues)) * ownership.get(roundToBeComputed));
                if(roundToBeComputed.getRound().liquidationPreference.cap == ProfitCap.CAPPED_2X){
                    preferenceExitValuation = Math.min(2*roundToBeComputed.getInvestment(), liquidityExitPreferenceValue +liquidityExitProfitValue);
                    if(preferenceExitValuation == 2*roundToBeComputed.getInvestment()){
                        capHit = true;
                    }
                }
            }

            ExitValuation roundExitValuation = ExitValuation.builder()
                    .commonShareExitValue(commonShareExitValuation)
                    .preferenceExitValue(preferenceExitValuation)
                    .capHit(capHit)
                    .build();
            exitValues.put(roundToBeComputed, roundExitValuation);

            if(roundExitValuation.prefersPreferenceExit()){
                // Recompute all the others
                recomputeStacking(exitValuation - roundExitValuation.preferenceExitValue, List.of(roundToBeComputed));

                // If one of the valuation hits the cap, recompute all the others till there are no new caped rounds
                List<InvestmentRound> capedRounds = exitValues.entrySet().stream().filter(entry -> entry.getValue().capHit && entry.getValue().prefersPreferenceExit()).map(Map.Entry::getKey).toList();
                boolean hasNewCapedRounds = !capedRounds.isEmpty();
                while (hasNewCapedRounds){
                    System.out.println("Hit cap: " + capedRounds.stream().map(ir->ir.getRound().name).collect(Collectors.joining(", ")));

                    double newExitValuation = exitValuation - capedRounds.stream().mapToDouble(ir -> exitValues.get(ir).getExitValue()).sum();
                    recomputeStacking(newExitValuation,  capedRounds);

                    hasNewCapedRounds = exitValues.entrySet().stream().filter(entry -> entry.getValue().capHit && entry.getValue().prefersPreferenceExit()).map(Map.Entry::getKey).toList().size() > capedRounds.size();
                }

                break;
            }
        }
        return exitValues;
    }

    private void recomputeStacking(double newExitValuation, List<InvestmentRound> computedRounds) {
        ArrayList<InvestmentRound> newStackingRounds = new ArrayList<>(investmentRounds);
        newStackingRounds.removeAll(computedRounds);

        Stacking stacking = new Stacking();
        stacking.addAll(newStackingRounds);
        HashMap<InvestmentRound, ExitValuation> recomputedExit = stacking.computeExit(newExitValuation, exitValues);
        exitValues.putAll(recomputedExit);
    }

    // Eleminate the preference money that needs to be given to lower priority stakeholders,
    // if we know that they will pick up the common stocks
    private double getPreferenceTotalInvestment(HashMap<InvestmentRound, ExitValuation> computedExitValues) {
        double preferenceTotalInvestment = totalInvestment;
        for(InvestmentRound roundToBeComputed: investmentRounds){
            if(computedExitValues.get(roundToBeComputed) != null && !computedExitValues.get(roundToBeComputed).prefersPreferenceExit()){
                preferenceTotalInvestment -= roundToBeComputed.investment;
            }
        }
        return preferenceTotalInvestment;
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        for (InvestmentRound investmentRound:investmentRounds) {
            output.append(investmentRound.getRound().name);
            output.append(" - ");
            output.append(exitValues.get(investmentRound).getExitValue());
            output.append(exitValues.get(investmentRound).getExitType());
            output.append("\n");
        }
        return output.toString();
    }
}