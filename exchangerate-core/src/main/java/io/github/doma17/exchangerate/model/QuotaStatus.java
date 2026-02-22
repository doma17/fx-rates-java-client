package io.github.doma17.exchangerate.model;

/**
 * Quota status response.
 *
 * @param planQuota total quota in current cycle
 * @param requestsRemaining remaining request count
 * @param refreshDayOfMonth day-of-month when quota resets
 */
public record QuotaStatus(int planQuota, int requestsRemaining, int refreshDayOfMonth) {
}
