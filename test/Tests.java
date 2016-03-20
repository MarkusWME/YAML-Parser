import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLInvalidContentException;
import at.pcgamingfreaks.yaml.YAMLKeyNotFoundException;
import at.pcgamingfreaks.yaml.YAMLNotInitializedException;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class Tests
{
	@Test
	public void testComments() throws YAMLNotInitializedException, YAMLInvalidContentException
	{
		YAML commentTest = new YAML("# This is a comment Test\n     #And this is another comment");
		assertEquals(2, commentTest.getLogicalLineCount());
		assertEquals("# This is a comment Test", commentTest.getLogicalLine(0));
		assertEquals("     #And this is another comment", commentTest.getLogicalLine(1));
	}

	@Test
	public void testCommentsWithValues() throws YAMLNotInitializedException, YAMLInvalidContentException
	{
		YAML commentValueTest = new YAML("\n# This is a comment Test\n- This is not a comment\n\n# And this is another comment");
		assertEquals(5, commentValueTest.getLogicalLineCount());
		assertEquals("# This is a comment Test", commentValueTest.getLogicalLine(1));
		assertEquals("key.0", commentValueTest.getLogicalLine(2));
		assertEquals("# And this is another comment", commentValueTest.getLogicalLine(4));
	}

	@Test
	public void testList() throws YAMLKeyNotFoundException, YAMLInvalidContentException
	{
		YAML listValueTest = new YAML("- 1\n- 2\n- 3");
		assertEquals(1, listValueTest.getInt("0"));
		assertEquals(2, listValueTest.getInt("1"));
		assertEquals(3, listValueTest.getInt("2"));
	}

	@Test
	public void testMapping() throws YAMLKeyNotFoundException, YAMLInvalidContentException
	{
		YAML mappingValueTest = new YAML("Test: 1\nTest2: 2\nTest3: 3");
		assertEquals(1, mappingValueTest.getInt("Test"));
		assertEquals(2, mappingValueTest.getInt("Test2"));
		assertEquals(3, mappingValueTest.getInt("Test3"));
	}

	@Test
	public void testGetKeys() throws YAMLInvalidContentException
	{
		YAML keyTest = new YAML("Line1: text\nLine2: text\nLine3: text\nLine4:\n  - group1\n  -group2");
		HashSet<String> keys = new HashSet<>();
		keys.add("Line1");
		keys.add("Line2");
		keys.add("Line3");
		assertEquals(keys, keyTest.getKeys(false));
	}

	@Test
	public void testGetSubKeys() throws YAMLInvalidContentException
	{
		YAML subKeyTest = new YAML("Line1: text\nLine2: text\nLine3: text\nLine4:\n  - group1\n  -group2");
		HashSet<String> keys = new HashSet<>();
		keys.add("Line1");
		keys.add("Line2");
		keys.add("Line3");
		keys.add("Line4.0");
		keys.add("Line4.1");
		assertEquals(keys, subKeyTest.getKeys(true));
	}
}