package com.tidewater.freight.repositories;

import com.tidewater.freight.exceptions.ZoneConfigurationException;
import com.tidewater.freight.pricing.ZoneRule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryZoneRuleRepository implements ZoneRuleRepository {

    private final Map<String, ZoneRule> rulesByZone;

    public InMemoryZoneRuleRepository(Map<String, ZoneRule> rules) {
        Map<String, ZoneRule> copy = new LinkedHashMap<>();
        for (Map.Entry<String, ZoneRule> entry : rules.entrySet()) {
            if (copy.putIfAbsent(entry.getKey(), entry.getValue()) != null) {
                throw new ZoneConfigurationException(
                    "Duplicate rule for zone " + entry.getKey());
            }
        }
        this.rulesByZone = Map.copyOf(copy);
    }

    @Override
    public Optional<ZoneRule> findRule(String zone) {
        if (zone == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(rulesByZone.get(zone));
    }
}