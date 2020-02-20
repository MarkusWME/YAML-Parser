package at.pcgamingfreaks.yaml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// Mainly abused for mixins to keep the main class more readable
public interface YamlGetter
{
	/**
	 * Checks if the given key exists in the YAML object
	 * @param key The key that should be checked
	 * @return If the key exists in the object or not
	 */
	boolean isSet(@NotNull String key);

	/**
	 * Gets a yaml value element from the YAML object
	 *
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 */
	@Nullable YamlValue getValue(@NotNull String key);

	/**
	 * Gets a byte value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a byte
	 */
	default byte getByte(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		return Byte.parseByte(getString(key));
	}

	/**
	 * Gets a byte value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a byte
	 */
	default byte getByte(@NotNull String key, byte defaultValue) throws NumberFormatException
	{
		return Byte.parseByte(getString(key, "" + defaultValue));
	}

	/**
	 * Gets a list of byte values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The byte list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a byte list
	 */
	default @NotNull List<Byte> getByteList(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		List<String> stringValues = getStringList(key);
		List<Byte> values = new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Byte.parseByte(value));
		}
		return values;
	}

	/**
	 * Gets a list of byte values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The byte list of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a byte list
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Byte> getByteList(@NotNull String key, @Nullable List<Byte> defaultValue) throws NumberFormatException
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		List<Byte> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Byte.parseByte(value));
		}
		return values;
	}

	/**
	 * Gets a short value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a short
	 */
	default short getShort(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		return Short.parseShort(getString(key));
	}

	/**
	 * Gets a short value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a short
	 */
	default short getShort(@NotNull String key, short defaultValue) throws NumberFormatException
	{
		return Short.parseShort(getString(key, "" + defaultValue));
	}

	/**
	 * Gets a list of short values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The short list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a short list
	 */
	default @NotNull List<Short> getShortList(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		List<String> stringValues = getStringList(key);
		List<Short> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Short.parseShort(value));
		}
		return values;
	}

	/**
	 * Gets a list of short values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The short list of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a short list
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Short> getShortList(@NotNull String key, @Nullable List<Short> defaultValue) throws NumberFormatException
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		List<Short> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Short.parseShort(value));
		}
		return values;
	}

	/**
	 * Gets an int value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to an int
	 */
	default int getInt(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		return Integer.parseInt(getString(key));
	}

	/**
	 * Gets an int value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to an int
	 */
	default int getInt(@NotNull String key, int defaultValue) throws NumberFormatException
	{
		return Integer.parseInt(getString(key, "" + defaultValue));
	}

	/**
	 * Gets a list of int values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The int list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to an int list
	 */
	default @NotNull List<Integer> getIntList(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		List<String> stringValues = getStringList(key);
		List<Integer> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Integer.parseInt(value));
		}
		return values;
	}

	/**
	 * Gets a list of int values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The int list of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to an int list
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Integer> getIntList(@NotNull String key, @Nullable List<Integer> defaultValue) throws NumberFormatException
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		List<Integer> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Integer.parseInt(value));
		}
		return values;
	}

	/**
	 * Gets a long value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a long
	 */
	default long getLong(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		return Long.parseLong(getString(key));
	}

	/**
	 * Gets a long value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a long
	 */
	default long getLong(@NotNull String key, long defaultValue) throws NumberFormatException
	{
		return Long.parseLong(getString(key, "" + defaultValue));
	}

	/**
	 * Gets a list of long values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The long list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a long list
	 */
	default @NotNull List<Long> getLongList(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		List<String> stringValues = getStringList(key);
		List<Long> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Long.parseLong(value));
		}
		return values;
	}

	/**
	 * Gets a list of long values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The long list of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a long list
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Long> getLongList(@NotNull String key, @Nullable List<Long> defaultValue) throws NumberFormatException
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		List<Long> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Long.parseLong(value));
		}
		return values;
	}

	/**
	 * Gets a float value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a float
	 */
	default float getFloat(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		return Float.parseFloat(getString(key));
	}

	/**
	 * Gets a float value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a float
	 */
	default float getFloat(@NotNull String key, float defaultValue) throws NumberFormatException
	{
		return Float.parseFloat(getString(key, "" + defaultValue));
	}

	/**
	 * Gets a list of float values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The float list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a float list
	 */
	default @NotNull List<Float> getFloatList(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		List<String> stringValues = getStringList(key);
		List<Float> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Float.parseFloat(value));
		}
		return values;
	}

	/**
	 * Gets a list of float values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The float list of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a float list
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Float> getFloatList(@NotNull String key, @Nullable List<Float> defaultValue) throws NumberFormatException
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		List<Float> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Float.parseFloat(value));
		}
		return values;
	}

	/**
	 * Gets a double value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a double
	 */
	default double getDouble(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		return Double.parseDouble(getString(key));
	}

	/**
	 * Gets a double value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a double
	 */
	default double getDouble(@NotNull String key, double defaultValue) throws NumberFormatException
	{
		return Double.parseDouble(getString(key, "" + defaultValue));
	}

	/**
	 * Gets a list of double values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The double list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws NumberFormatException If the value of the searched key can not be converted to a double list
	 */
	default @NotNull List<Double> getDoubleList(@NotNull String key) throws YamlKeyNotFoundException, NumberFormatException
	{
		List<String> stringValues = getStringList(key);
		List<Double> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Double.parseDouble(value));
		}
		return values;
	}

	/**
	 * Gets a list of double values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The double list of the key you searched for
	 * @throws NumberFormatException If the value of the searched key can not be converted to a double list
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Double> getDoubleList(@NotNull String key, @Nullable List<Double> defaultValue) throws NumberFormatException
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		List<Double> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Double.parseDouble(value));
		}
		return values;
	}

	/**
	 * Gets a boolean value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 */
	default boolean getBoolean(@NotNull String key) throws YamlKeyNotFoundException
	{
		return Boolean.parseBoolean(getString(key));
	}

	/**
	 * Gets a boolean value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 */
	default boolean getBoolean(@NotNull String key, boolean defaultValue)
	{
		return Boolean.parseBoolean(getString(key, "" + defaultValue));
	}

	/**
	 * Gets a list of boolean values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The boolean list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 */
	default @NotNull List<Boolean> getBooleanList(@NotNull String key) throws YamlKeyNotFoundException
	{
		List<String> stringValues = getStringList(key);
		List<Boolean> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Boolean.parseBoolean(value));
		}
		return values;
	}

	/**
	 * Gets a list of boolean values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The boolean list of the key you searched for
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Boolean> getBooleanList(@NotNull String key, @Nullable List<Boolean> defaultValue)
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		List<Boolean> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			values.add(Boolean.parseBoolean(value));
		}
		return values;
	}

	/**
	 * Gets a char value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws YamlInvalidContentException If the value of the searched key can not be converted to a char
	 */
	default char getChar(@NotNull String key) throws YamlKeyNotFoundException, YamlInvalidContentException
	{
		String value = getString(key);
		if (value.length() == 1)
		{
			return value.charAt(0);
		}
		throw new YamlInvalidContentException("The value you searched for could not be converted to a character");
	}

	/**
	 * Gets a char value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 * @throws YamlInvalidContentException If the value of the searched key can not be converted to a char
	 */
	default char getChar(@NotNull String key, char defaultValue) throws YamlInvalidContentException
	{
		String value = getString(key, "" + defaultValue);
		if (value.length() == 1)
		{
			return value.charAt(0);
		}
		throw new YamlInvalidContentException("The value you searched for could not be converted to a character");
	}

	static @NotNull List<Character> getCharListFromStringList(@NotNull List<String> stringValues) throws YamlInvalidContentException
	{
		List<Character> values =  new ArrayList<>(stringValues.size());
		for (String value : stringValues)
		{
			if (value.length() == 1)
			{
				values.add(value.charAt(0));
			}
			else
			{
				values.clear();
				throw new YamlInvalidContentException("The value you searched for could not be converted to a character");
			}
		}
		return values;
	}

	/**
	 * Gets a list of char values from the YAML object
	 * @param key The key of the list you want to get
	 * @return The char list of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 * @throws YamlInvalidContentException If the value of the searched key can not be converted to a char list
	 */
	default @NotNull List<Character> getCharList(@NotNull String key) throws YamlKeyNotFoundException, YamlInvalidContentException
	{
		return getCharListFromStringList(getStringList(key));
	}

	/**
	 * Gets a list of char values from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The char list of the key you searched for
	 * @throws YamlInvalidContentException If the value of the searched key can not be converted to a char list
	 */
	@Contract("_, !null -> !null")
	default @Nullable List<Character> getCharList(@NotNull String key, @Nullable List<Character> defaultValue) throws YamlInvalidContentException
	{
		List<String> stringValues = getStringList(key, null);
		if (stringValues == null)
		{
			return defaultValue;
		}
		return getCharListFromStringList(stringValues);
	}

	/**
	 * Gets a string value from the YAML object
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 */
	default @NotNull String getString(@NotNull String key) throws YamlKeyNotFoundException
	{
		YamlValue value = getValue(key);
		if (value != null)
		{
			return value.getValue();
		}
		throw new YamlKeyNotFoundException("The key you wanted to retrieve (\"" + key + "\") could not be found in the YAML object");
	}

	/**
	 * Gets a string value from the YAML object
	 * @param key The key of the value you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The value of the key you searched for
	 */
	@Contract("_, !null -> !null")
	default @Nullable String getString(@NotNull String key, @Nullable String defaultValue)
	{
		YamlValue value = getValue(key);
		if (value != null)
		{
			return value.getValue();
		}
		return defaultValue;
	}

	/**
	 * Gets a list of strings from the YAML object
	 * @param key The key of the list you want to get
	 * @return The list of values you searched for
	 * @throws YamlKeyNotFoundException If the key you searched for could not be found in the YAML object
	 */
	default @NotNull List<String> getStringList(@NotNull String key) throws YamlKeyNotFoundException
	{
		List<String> list = getStringList(key, null);
		if(list != null) return list;
		throw new YamlKeyNotFoundException("The key you wanted to retrieve (\"" + key + "\") could not be found in the YAML object");
	}

	/**
	 * Gets a list of strings from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The list of values you searched for
	 */
	@Contract("_, !null -> !null")
	@Nullable List<String> getStringList(@NotNull String key, @Nullable List<String> defaultValue);
}