package helpers;

public class HourlyPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(long hours) {
        if (hours <= 1) return 4;
        if (hours <= 3) return 4 + (hours - 1) * 3.5;
        return 4 + 2 * 3.5 + (hours - 3) * 2.5;
    }
}
