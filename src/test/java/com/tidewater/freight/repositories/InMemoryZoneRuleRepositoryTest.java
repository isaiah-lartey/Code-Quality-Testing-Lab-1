package com.tidewater.freight.repositories;

import com.tidewater.freight.exceptions.ZoneConfigurationException;
import com.tidewater.freight.pricing.DomesticZoneRule;
import com.tidewater.freight.pricing.RegionalZoneRule;
import com.tidewater.freight.pricing.ZoneRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("InMemoryZoneRuleRepository")
class InMemoryZoneRuleRepositoryTest {

    @Test
    @DisplayName("finds the rule for a known zone")
    void findsRuleForKnownZone() {
        ZoneRule domestic = new DomesticZoneRule();
        ZoneRuleRepository repository =
            new InMemoryZoneRuleRepository(List.of(domestic));

        assertSame(domestic, repository.findRule("DOMESTIC").orElseThrow());
    }

    @Test
    @DisplayName("unknown zone returns empty")
    void unknownZoneReturnsEmpty() {
        ZoneRuleRepository repository =
            new InMemoryZoneRuleRepository(List.of(new DomesticZoneRule()));

        assertTrue(repository.findRule("MOON").isEmpty());
    }

    @Test
    @DisplayName("null zone returns empty, not a NullPointerException")
    void nullZoneReturnsEmpty() {
        ZoneRuleRepository repository =
            new InMemoryZoneRuleRepository(List.of(new DomesticZoneRule()));

        assertTrue(repository.findRule(null).isEmpty());
    }

    @Test
    @DisplayName("mutating the input list after construction does not change the repository")
    void repositoryIsDefensivelyCopied() {
        List<ZoneRule> input = new ArrayList<>();
        input.add(new DomesticZoneRule());
        ZoneRuleRepository repository = new InMemoryZoneRuleRepository(input);

        input.clear();

        assertTrue(repository.findRule("DOMESTIC").isPresent());
    }

    @Test
    @DisplayName("the rule's zoneName() is the key it is looked up by")
    void keyComesFromRuleZoneName() {
        ZoneRule domestic = new DomesticZoneRule();
        ZoneRule regional = new RegionalZoneRule();
        ZoneRuleRepository repository =
            new InMemoryZoneRuleRepository(List.of(domestic, regional));

        assertSame(domestic, repository.findRule(domestic.zoneName()).orElseThrow());
        assertSame(regional, repository.findRule(regional.zoneName()).orElseThrow());
    }

    @Test
    @DisplayName("two rules claiming the same zone throws ZoneConfigurationException")
    void rejectsDuplicateZones() {
        ZoneRule first = new DomesticZoneRule();
        ZoneRule second = new ZoneRule() {
            @Override public String zoneName() { return "DOMESTIC"; }
            @Override public BigDecimal baseCost(BigDecimal weightKg) {
                return BigDecimal.ZERO;
            }
        };

        ZoneConfigurationException ex = assertThrows(ZoneConfigurationException.class, () ->
            new InMemoryZoneRuleRepository(List.of(first, second)));

        assertEquals("Duplicate rule for zone DOMESTIC", ex.getMessage());
    }

    @Test
    @DisplayName("a null rule list throws ZoneConfigurationException")
    void rejectsNullRuleList() {
        ZoneConfigurationException ex = assertThrows(ZoneConfigurationException.class, () ->
            new InMemoryZoneRuleRepository(null));

        assertEquals("Rules must not be null", ex.getMessage());
    }

    @Test
    @DisplayName("a null rule inside the list throws ZoneConfigurationException")
    void rejectsNullRuleInList() {
        List<ZoneRule> rules = new ArrayList<>();
        rules.add(new DomesticZoneRule());
        rules.add(null);

        ZoneConfigurationException ex = assertThrows(ZoneConfigurationException.class, () ->
            new InMemoryZoneRuleRepository(rules));

        assertEquals("Rules must not contain null", ex.getMessage());
    }
}
