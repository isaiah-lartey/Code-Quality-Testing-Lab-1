package com.tidewater.freight.repositories;

import com.tidewater.freight.exceptions.ZoneConfigurationException;
import com.tidewater.freight.pricing.ZoneRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryZoneRuleRepository implements ZoneRuleRepository {

    private final Map<String, ZoneRule> rulesByZone;

    public InMemoryZoneRuleRepository(List<ZoneRule> rules) {
        if (rules == null) {
            throw new ZoneConfigurationException("Rules must not be null");
        }
        Map<String, ZoneRule> byZone = new LinkedHashMap<>();
        for (ZoneRule rule : rules) {
            if (rule == null) {
                throw new ZoneConfigurationException("Rules must not contain null");
            }
            String zone = rule.zoneName();
            if (byZone.putIfAbsent(zone, rule) != null) {
                throw new ZoneConfigurationException(
                    "Duplicate rule for zone " + zone);
            }
        }
        this.rulesByZone = Map.copyOf(byZone);
    }

    @Override
    public Optional<ZoneRule> findRule(String zone) {
        if (zone == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(rulesByZone.get(zone));
    }
}