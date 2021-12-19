package com.paymybuddy.transfer.constant;

import java.util.Map;
import java.util.Set;

/**
 * This class defines the constants values of differents currency and their
 * associated symbols.
 */
public class Currencies {
	public static final Map<String, String> SYMBOLS = Map.of("EUR", "€");
	public static final Set<String> AUTHORIZED_CURRENCIES = Set.of("EUR");
}
