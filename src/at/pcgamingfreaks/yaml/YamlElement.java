package at.pcgamingfreaks.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface YamlElement
{
	@NotNull String getComment();
	void setComment(String comment);
	@Nullable Character getQuoteChar();
	void setQuoteChar(@Nullable Character qChar);
}