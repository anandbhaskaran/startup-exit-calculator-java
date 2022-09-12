package org.ledgy;

import lombok.Getter;
import org.ledgy.model.ExitValuation;
import org.ledgy.model.InvestmentRound;
import org.ledgy.model.Participation;
import org.ledgy.model.ProfitCap;

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
        totalInvestment += investmentRound.getInvestment();
        totalShares += investmentRound.getShares();
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
            ExitValuation roundExitValuation = getExitValuation(exitValuation, computedExitValues, roundToBeComputed);

            if(roundExitValuation.prefersPreferenceExit()){
                // Recompute all the others
                recomputeStacking(exitValuation - roundExitValuation.getPreferenceExitValue(), List.of(roundToBeComputed));

                // If one of the valuation hits the cap, recompute all the others till there are no new caped rounds
                recomputeOnCap(exitValuation);

                // Don't continue the execution as it will computed recursively
                break;
            }
        }
        return exitValues;
    }

    private void recomputeOnCap(double exitValuation) {

        // Get list of rounds that hits the cap and the investor prefers the preferential exit
        List<InvestmentRound> capedRounds = exitValues.entrySet().stream().filter(entry -> entry.getValue().isCapHit() && entry.getValue().prefersPreferenceExit()).map(Map.Entry::getKey).toList();
        boolean hasNewCapedRounds = !capedRounds.isEmpty();

        // Recompute till there is no change in cap hit
        while (hasNewCapedRounds){
            capedRounds = exitValues.entrySet().stream().filter(entry -> entry.getValue().isCapHit() && entry.getValue().prefersPreferenceExit()).map(Map.Entry::getKey).toList();

            System.out.println("Hit cap: " + capedRounds.stream().map(ir->ir.getRound().name).collect(Collectors.joining(", ")));

            double newExitValuation = exitValuation - capedRounds.stream().mapToDouble(ir -> exitValues.get(ir).getExitValue()).sum();
            recomputeStacking(newExitValuation,  capedRounds);

            hasNewCapedRounds = exitValues.entrySet().stream().filter(entry -> entry.getValue().isCapHit() && entry.getValue().prefersPreferenceExit()).map(Map.Entry::getKey).toList().size() > capedRounds.size();
        }
    }

    private ExitValuation getExitValuation(double exitValuation, HashMap<InvestmentRound, ExitValuation> computedExitValues, InvestmentRound roundToBeComputed) {
        // Common share Exit valuation
        double commonShareExitValuation = exitValuation * ownership.get(roundToBeComputed);

        // Preference Exit Valuation
        double preferenceExitValuation = 0;
        boolean capHit = false;
        if(roundToBeComputed.getRound().liquidationPreference.getParticipation() == Participation.PARTICIPATING_1X) {
            double liquidityExitPreferenceValue = Math.min(exitValuation, roundToBeComputed.getInvestment());
            double liquidityExitProfitValue = Math.max(0, (exitValuation - getPreferenceTotalInvestment(computedExitValues)) * ownership.get(roundToBeComputed));
            if(roundToBeComputed.getRound().liquidationPreference.getCap() == ProfitCap.CAPPED_2X){
                preferenceExitValuation = Math.min(2* roundToBeComputed.getInvestment(), liquidityExitPreferenceValue +liquidityExitProfitValue);
                if(preferenceExitValuation == 2* roundToBeComputed.getInvestment()){
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
        return roundExitValuation;
    }

    private void recomputeStacking(double newExitValuation, List<InvestmentRound> computedRounds) {
        ArrayList<InvestmentRound> newStackingRounds = new ArrayList<>(investmentRounds);
        newStackingRounds.removeAll(computedRounds);

        Stacking stacking = new Stacking();
        stacking.addAll(newStackingRounds);
        HashMap<InvestmentRound, ExitValuation> recomputedExit = stacking.computeExit(newExitValuation, exitValues);
        exitValues.putAll(recomputedExit);
    }

    // If we know that the low priority stakeholder will pick up the common stocks,
    // eliminate the preference money that needs to be given to lower priority stakeholders
    private double getPreferenceTotalInvestment(HashMap<InvestmentRound, ExitValuation> computedExitValues) {
        double preferenceTotalInvestment = totalInvestment;
        for(InvestmentRound roundToBeComputed: investmentRounds){
            if(computedExitValues.get(roundToBeComputed) != null && !computedExitValues.get(roundToBeComputed).prefersPreferenceExit()){
                preferenceTotalInvestment -= roundToBeComputed.getInvestment();
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
