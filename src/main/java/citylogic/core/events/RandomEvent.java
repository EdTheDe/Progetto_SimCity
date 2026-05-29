package citylogic.core.events;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;

public abstract class RandomEvent {
    protected int remainingTicks;
    protected String name;

    public RandomEvent(String name, int durationTicks) {
        this.name = name;
        this.remainingTicks = durationTicks;
    }

    public String getName() {
        return name;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void decrementTick() {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }

    public boolean isExpired() {
        return remainingTicks <= 0;
    }

    /**
     * Applica le modifiche dello specifico evento ai parametri globali della città
     * e alle statistiche accumulate nel tick corrente.
     */
    public abstract void applyModifiers(StatoCitta stato, TickStats stats);
}
