package oekoflex;

import util.TimeUtil;

/**
 * Definitions of market informations for
 * <ul>
 * 		<li> energy only market,
 * 		<li> balancing power market
 * </ul>
 */
public enum Market {
    SPOT_MARKET(1),       //EOM
    BALANCING_MARKET(16),  //Regelenergie
    START_VALUE(0),
	BALANCING_MARKET_CAPACITY(16),
	BALANCING_MARKET_WORK(1);
	
    private final int ticks;

    Market(final int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    public float getDurationInHours() {
        return getTicks() * TimeUtil.HOUR_PER_TICK;
    }
}

