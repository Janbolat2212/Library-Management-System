package kz.library.model;

import java.io.Serial;
import java.io.Serializable;

public final class Fine implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double amount;
    private final long daysLate;

    private Fine(double amount, long daysLate) {
        this.amount = amount;
        this.daysLate = daysLate;
    }

    public static Fine calculate(long daysLate, double dailyRate) {
        if (daysLate <= 0) {
            throw new IllegalArgumentException("Кешіккен күн саны нөлден үлкен болуы керек.");
        }
        if (dailyRate < 0) {
            throw new IllegalArgumentException("Айыппұл мөлшерлемесі теріс болмауы керек.");
        }
        double amount = Math.round(daysLate * dailyRate * 100.0) / 100.0;
        return new Fine(amount, daysLate);
    }

    public double getAmount() {
        return amount;
    }

    public long getDaysLate() {
        return daysLate;
    }

    @Override
    public String toString() {
        return "Айыппұл {кешіккен күн=%d, сома=%.2f ₸}".formatted(daysLate, amount);
    }
}
