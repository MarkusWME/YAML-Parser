import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLInvalidContentException;
import at.pcgamingfreaks.yaml.YAMLKeyNotFoundException;
import at.pcgamingfreaks.yaml.YAMLNotInitializedException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Tests
{
	@Test
	public void testComments() throws YAMLNotInitializedException, YAMLInvalidContentException
	{
		YAML commentTest = new YAML("# This is a comment Test\n     #And this is another comment");
		assertEquals(1, commentTest.getLogicalLineCount());
		assertEquals("# This is a comment Test\n     #And this is another comment", commentTest.getLogicalLine(0));
	}

	@Test
	public void testCommentsWithValues() throws YAMLNotInitializedException, YAMLInvalidContentException
	{
		YAML commentValueTest = new YAML("\n# This is a comment Test\n- This is not a comment\n\n# And this is another comment");
		assertEquals(3, commentValueTest.getLogicalLineCount());
		assertEquals("\n# This is a comment Test", commentValueTest.getLogicalLine(0));
		assertEquals("key.0", commentValueTest.getLogicalLine(1));
		assertEquals("\n# And this is another comment", commentValueTest.getLogicalLine(2));
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
}