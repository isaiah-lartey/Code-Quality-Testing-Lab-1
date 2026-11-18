# Tidewater Freight: Shipping Cost Service

A refactor of the starter shipping-cost service. The starter compiled and its tests passed, but the calculator swallowed every failure as a price of `0.0`, the tests proved nothing, and adding a zone meant editing an `if` chain. This refactor fixes those problems, replaces the tests, and makes the coming new zone a new class rather than an edit.

## Read this first

- **[`ANALYSIS.md`](ANALYSIS.md)**: the pre-refactor analysis of the starter. It contains twelve ranked findings, the hollow tests, and the planned structure. It was written before any code changed, and its line links are pinned to the starter commit [`10cb1a3`](https://github.com/isaiah-lartey/Code-Quality-Testing-Lab-1/tree/10cb1a3), so they open the starter code rather than the refactored tree.
- **This file**: the reasoning behind the refactor, what was kept, fixed, and rewritten, and the improvements beyond the brief.

## Running it

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.tidewater.freight.Demo   # the demo
mvn test                                                    # 55 tests
mvn verify                                                  # tests + JaCoCo: target/site/jacoco/index.html
mvn test-compile org.pitest:pitest-maven:mutationCoverage   # PIT: target/pit-reports/index.html
```

The demo prints:

```
50.00
967.50
Rejected: Unknown zone: MOON
```

The first two lines are the same prices the starter produced, now formatted to two decimal places. The third line changed: it is the fix for the most serious finding. In the starter, the same call returned `0.0` and printed it as if it were a price.

## What was kept, fixed, and rewritten

### Kept: business rules, unchanged and pinned by tests

The rates and fees are the starter's own values, not new ones.

| Rule | Value | Pinned by |
|---|---|---|
| Domestic rate | 5.0 / kg, no fee | [`ShippingPricingTest`](src/test/java/com/tidewater/freight/ShippingPricingTest.java) |
| Regional rate | 8.0 / kg + 10 handling | [`ShippingPricingTest`](src/test/java/com/tidewater/freight/ShippingPricingTest.java) |
| International rate | 15.0 / kg + 25 customs | [`ShippingPricingTest`](src/test/java/com/tidewater/freight/ShippingPricingTest.java) |
| Heavy threshold | strictly greater than 30 kg | [`HeavyItemSurchargeTest`](src/test/java/com/tidewater/freight/pricing/HeavyItemSurchargeTest.java) |
| Heavy surcharge | 20 | [`HeavyItemSurchargeTest`](src/test/java/com/tidewater/freight/pricing/HeavyItemSurchargeTest.java) |
| Express multiplier | 1.5 | [`ServiceLevelTest`](src/test/java/com/tidewater/freight/models/ServiceLevelTest.java) |
| Order of operations | surcharge added **before** the express multiplier | the `DOMESTIC 31kg EXPRESS = 262.50` row in [`ShippingPricingTest`](src/test/java/com/tidewater/freight/ShippingPricingTest.java) |

The ordering rule is the one the starter left unwritten. It is preserved deliberately and pinned by the `262.50` row: if a future refactor swaps the two operations, that row fails with `252.50`.

The demo entry point and the three-zone shape are also kept.

### Fixed

- **`InMemoryRateRepository` → [`InMemoryZoneRuleRepository`](src/main/java/com/tidewater/freight/repositories/InMemoryZoneRuleRepository.java).** The name now says what it holds. A zone's rate and fee used to be defined in two places (repository and calculator); they now live together in one `ZoneRule`.
- **`findRate` returning `double` → [`findRule`](src/main/java/com/tidewater/freight/repositories/ZoneRuleRepository.java) returning `Optional<ZoneRule>`.** The repository no longer decides what "not found" means; the caller does. This lets the calculator throw `UnknownZoneException` with the zone in the message.
- **The repository takes a `List<ZoneRule>`, keyed by `rule.zoneName()`.** It previously took a `Map` with caller-supplied keys, which made two things decorative: the duplicate-zone guard could never fire (a `Map` cannot hold two entries with the same key), and `zoneName()` was never called. Keying by `zoneName()` makes both load-bearing.
- **Validation moved into [`ShipmentRequest`](src/main/java/com/tidewater/freight/models/ShipmentRequest.java).** The record's compact constructor rejects a null or blank zone, a null or non-positive weight, and a null service level, throwing `InvalidShipmentException`. The starter had no validation.
- **The unused `ShippingException(String, Throwable)` constructor was removed.** It was dead API surface.

### Rewritten

- **The `if / else if` zone chain → [`ZoneRule`](src/main/java/com/tidewater/freight/pricing/ZoneRule.java) with one class per zone:** [`DomesticZoneRule`](src/main/java/com/tidewater/freight/pricing/DomesticZoneRule.java), [`RegionalZoneRule`](src/main/java/com/tidewater/freight/pricing/RegionalZoneRule.java), and [`InternationalZoneRule`](src/main/java/com/tidewater/freight/pricing/InternationalZoneRule.java).
- **`String serviceLevel` → the [`ServiceLevel`](src/main/java/com/tidewater/freight/models/ServiceLevel.java) enum.** A typo is now a compile error, not a silently standard price.
- **`double` arithmetic → `BigDecimal`.** Rates and fees are exact decimals, and the calculator returns `setScale(2, RoundingMode.HALF_UP)`.
- **The `println` left the calculator.** Output belongs to [`Demo`](src/main/java/com/tidewater/freight/Demo.java); the calculator computes and returns.
- **The three hollow tests → eight test files, 55 tests.** See [The tests](#the-tests).

### One behaviour changed, by design

The starter returned `0.0` on an unknown zone. The refactor throws [`UnknownZoneException`](src/main/java/com/tidewater/freight/exceptions/UnknownZoneException.java). This is the fix for the most serious finding, not a regression: the starter told the caller the shipment cost nothing while a failure had happened. The demo shows the change: `Rejected: Unknown zone: MOON` instead of `0.0`.

## How the design absorbs the coming new zone

[`NewZoneExtensionTest`](src/test/java/com/tidewater/freight/NewZoneExtensionTest.java) proves the brief's requirement rather than claiming it. A test-only `ArcticZoneRule` is registered alongside `DomesticZoneRule` and priced through the real calculator:

- `ARCTIC 2kg STANDARD = 90.00` (20/kg + 50 cold-chain fee)
- `DOMESTIC 10kg STANDARD` costs the same before and after `ARCTIC` is registered

No production class is edited to add the zone. `ShippingCalculator`, the three existing rules, `HeavyItemSurcharge`, and `ShipmentRequest` are all untouched. Adding a zone means writing a new class that implements `ZoneRule` and adding one entry to the repository's list. That is the open-closed principle, proven by a test.

## The tests

Eight files, 55 tests, one file per concern. The test tree mirrors the production packages.

| File | Tests | What it pins |
|---|---|---|
| [`ShippingCalculatorTest`](src/test/java/com/tidewater/freight/ShippingCalculatorTest.java) | 2 | unknown zone throws; prices round HALF_UP to two decimals |
| [`ShipmentRequestTest`](src/test/java/com/tidewater/freight/models/ShipmentRequestTest.java) | 9 | every validation rule, the `0.01` / `-0.01` boundary pair, and zone whitespace trimming |
| [`InMemoryZoneRuleRepositoryTest`](src/test/java/com/tidewater/freight/repositories/InMemoryZoneRuleRepositoryTest.java) | 8 | lookup contract, defensive copy, `zoneName()` as key, duplicate guard, null list and null element guards |
| [`HeavyItemSurchargeTest`](src/test/java/com/tidewater/freight/pricing/HeavyItemSurchargeTest.java) | 7 | the 30 kg boundary, both sides |
| [`ServiceLevelTest`](src/test/java/com/tidewater/freight/models/ServiceLevelTest.java) | 5 | STANDARD = 1, EXPRESS = 1.5, and `apply` does not round |
| [`ZoneRulesTest`](src/test/java/com/tidewater/freight/pricing/ZoneRulesTest.java) | 12 | each rule's rate and fee, and `zoneName()` matches the lane |
| [`ShippingPricingTest`](src/test/java/com/tidewater/freight/ShippingPricingTest.java) | 10 | exact composed prices, the 30 kg boundary, the ordering rule, the failure path end to end |
| [`NewZoneExtensionTest`](src/test/java/com/tidewater/freight/NewZoneExtensionTest.java) | 2 | a new zone slots in with no edit to production code |

Test design choices worth naming:

- **`assertEquals` on `BigDecimal` in the composed-price tests, `compareTo` in the pure-function tests.** A price is a two-decimal value, so scale is part of the contract in `ShippingPricingTest`. `ServiceLevel.apply` and `HeavyItemSurcharge.surchargeFor` have no rounding contract, so `compareTo` asks the right question (numerical equality) instead of the wrong one (scale equality).
- **One file per concern, not one giant test class.** Unit tests live in the sub-packages and integration tests at the root, so the test pyramid is visible.
- **Named tests where assertions differ; parameterized tables where only inputs differ.** `ShipmentRequestTest` uses named tests because each rejection has a different expected message. `ShippingPricingTest` and `HeavyItemSurchargeTest` use `@CsvSource` because every row asserts the same way.

## Improvements beyond the brief

There are two, of different kinds.

### 1. Parameterized exact-price matrix (test quality)

[`ShippingPricingTest`](src/test/java/com/tidewater/freight/ShippingPricingTest.java). The starter's three tests asserted `cost > 0`, which passes for any positive number and does not check the price. This parameterized matrix asserts exact prices instead. The refactored suite is checked by PIT, which generates its own mutants: **118 of 23 killed, test strength 100%**. The three uncovered mutants are in `Demo`'s print statement, which no test exercises — a demo, not the pricing logic. A green build now means the pricing rules work, not merely that three tests ran.

### 2. Duplicate-zone guard (a guard against a class of bug)

[`InMemoryZoneRuleRepository`](src/main/java/com/tidewater/freight/repositories/InMemoryZoneRuleRepository.java) throws [`ZoneConfigurationException`](src/main/java/com/tidewater/freight/exceptions/ZoneConfigurationException.java) at construction if two rules claim the same zone. This is the same class of bug the starter had (two sources, one price, no error), caught at the cheapest moment: startup, not the first shipment. [`InMemoryZoneRuleRepositoryTest.rejectsDuplicateZones`](src/test/java/com/tidewater/freight/repositories/InMemoryZoneRuleRepositoryTest.java) proves it fires, and the `List<ZoneRule>` constructor is what makes it possible.


## Findings addressed

Of the twelve ranked findings in [`ANALYSIS.md`](ANALYSIS.md):

- **Seven are pinned by tests:** #1, #2, #3, #4, #6, #8, #10, plus the duplicate-zone-guard improvement.
- **Five are pinned by the shape of the code and reviewed rather than tested:**
  - #5: the 31-line method is now 10 lines.
  - #7: the print left the calculator.
  - #9: the compiler enforces the service level through the `ServiceLevel` enum, so a typo no longer builds.
  - #11: the noise comments and the empty `if` branch are gone.
  - #12: lines are wrapped to 100 columns and the test has no wildcard imports.
