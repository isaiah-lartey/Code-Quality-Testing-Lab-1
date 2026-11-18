package com.tidewater.freight.repositories;

import com.tidewater.freight.pricing.ZoneRule;

import java.util.Optional;

public interface ZoneRuleRepository {

    Optional<ZoneRule> findRule(String zone);
}