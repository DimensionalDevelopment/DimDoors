package org.dimdev.dimdoors.util;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

public class Timer {
    public static final double TICKS_PER_SECOND = 20.0;
    public static final double MILLIS_PER_TICK = 1000.0 / TICKS_PER_SECOND;

    private static final double NOT_STARTED = Double.NaN;

    private final TickSource tickSource;
    private final double fadeInTicks;
    private final double durationTicks;
    private final double fadeOutTicks;

    private double fadeInStartedAt = NOT_STARTED;
    private double fadeInStartVisibility;
    private double fadeOutStartsAt = NOT_STARTED;

    public Timer(
            TickSource tickSource,
            double fadeInTicks,
            double durationTicks,
            double fadeOutTicks
    ) {
        this.tickSource = Objects.requireNonNull(tickSource, "tickSource");
        this.fadeInTicks = requireNonNegative(fadeInTicks, "fadeInTicks");
        this.durationTicks = requireNonNegative(durationTicks, "durationTicks");
        this.fadeOutTicks = requireNonNegative(fadeOutTicks, "fadeOutTicks");
    }

    public Timer(
            LongSupplier tickSupplier,
            DoubleSupplier partialTickSupplier,
            double fadeInTicks,
            double durationTicks,
            double fadeOutTicks
    ) {
        this(
                new SuppliedTickSource(tickSupplier, partialTickSupplier),
                fadeInTicks,
                durationTicks,
                fadeOutTicks
        );
    }

    public static MutableTickSource mutableTickSource() {
        return new MutableTickSource();
    }

    public static double millisecondsToTicks(double milliseconds) {
        return milliseconds / MILLIS_PER_TICK;
    }

    public void trigger() {
        double now = currentTick();

        if (!isRunning(now)) {
            this.fadeInStartedAt = now;
            this.fadeInStartVisibility = 0.0;
            this.fadeOutStartsAt =
                    now + this.fadeInTicks + this.durationTicks;
            return;
        }

        if (now > this.fadeOutStartsAt) {
            double visibility = getVisibility(now);

            this.fadeInStartedAt = now;
            this.fadeInStartVisibility = visibility;
            this.fadeOutStartsAt =
                    now + this.fadeInTicks + this.durationTicks;
            return;
        }

        double fadeInEndsAt = this.fadeInStartedAt + this.fadeInTicks;

        this.fadeOutStartsAt = Math.max(
                this.fadeOutStartsAt,
                Math.max(fadeInEndsAt, now) + this.durationTicks
        );
    }

    public void reset() {
        this.fadeInStartedAt = NOT_STARTED;
        this.fadeInStartVisibility = 0.0;
        this.fadeOutStartsAt = NOT_STARTED;
    }

    public boolean hasStarted() {
        return !Double.isNaN(this.fadeInStartedAt);
    }

    public boolean isRunning() {
        return isRunning(currentTick());
    }

    public boolean isVisible() {
        return getVisibility() > 0.0F;
    }

    public float getVisibility() {
        return (float) getVisibility(currentTick());
    }

    private double getVisibility(double now) {
        if (!hasStarted()) {
            return 0.0;
        }

        double fadeInElapsed = now - this.fadeInStartedAt;

        if (fadeInElapsed < 0.0) {
            return 0.0;
        }

        if (this.fadeInTicks > 0.0 && fadeInElapsed < this.fadeInTicks) {
            double progress = fadeInElapsed / this.fadeInTicks;

            return this.fadeInStartVisibility
                    + (1.0 - this.fadeInStartVisibility) * progress;
        }

        if (now < this.fadeOutStartsAt) {
            return 1.0;
        }

        if (this.fadeOutTicks > 0.0) {
            double fadeOutElapsed = now - this.fadeOutStartsAt;

            if (fadeOutElapsed < this.fadeOutTicks) {
                return 1.0 - fadeOutElapsed / this.fadeOutTicks;
            }
        }

        return 0.0;
    }

    private boolean isRunning(double now) {
        return hasStarted()
                && now >= this.fadeInStartedAt
                && now < this.fadeOutStartsAt + this.fadeOutTicks;
    }

    public double getFadeInTicks() {
        return this.fadeInTicks;
    }

    public double getDurationTicks() {
        return this.durationTicks;
    }

    public double getFadeOutTicks() {
        return this.fadeOutTicks;
    }

    public double getTotalTicks() {
        return this.fadeInTicks
                + this.durationTicks
                + this.fadeOutTicks;
    }

    private double currentTick() {
        return this.tickSource.getTick()
                + this.tickSource.getPartialTick();
    }

    private static double requireNonNegative(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new IllegalArgumentException(
                    name + " must be finite and non-negative"
            );
        }

        return value;
    }

    public interface TickSource {
        long getTick();

        double getPartialTick();
    }

    public static class MutableTickSource implements TickSource {
        private long tick;
        private double partialTick;

        public void update(long tick, double partialTick) {
            this.tick = tick;
            this.partialTick = partialTick;
        }

        @Override
        public long getTick() {
            return this.tick;
        }

        @Override
        public double getPartialTick() {
            return this.partialTick;
        }
    }

    private record SuppliedTickSource(
            LongSupplier tickSupplier,
            DoubleSupplier partialTickSupplier
    ) implements TickSource {
        private SuppliedTickSource {
            Objects.requireNonNull(tickSupplier, "tickSupplier");
            Objects.requireNonNull(partialTickSupplier, "partialTickSupplier");
        }

        @Override
        public long getTick() {
            return this.tickSupplier.getAsLong();
        }

        @Override
        public double getPartialTick() {
            return this.partialTickSupplier.getAsDouble();
        }
    }
}