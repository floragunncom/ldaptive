/* See LICENSE for licensing and NOTICE for copyright. */
package org.ldaptive.io;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchResult;

/**
 * Writes a {@link SearchResult} as JSON to a {@link Writer}.
 *
 * @author  Middleware Services
 */
public class JsonWriter implements SearchResultWriter
{

  /** Writer to write to. */
  private final Writer jsonWriter;

  /** To convert a search result to JSON. */
  private final Gson gson;


  /**
   * Creates a new json writer.
   *
   * @param  writer  to write JSON to
   */
  public JsonWriter(final Writer writer)
  {
    jsonWriter = writer;
    final GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(SearchResult.class, new SearchResultSerializer());
    gson = builder.disableHtmlEscaping().create();
  }


  /**
   * Writes the supplied search result to the writer.
   *
   * @param  result  search result to write
   *
   * @throws  IOException  if an error occurs using the writer
   */
  @Override
  public void write(final SearchResult result)
    throws IOException
  {
    gson.toJson(result, jsonWriter);
    jsonWriter.flush();
  }


  /**
   * Serializes a {@link SearchResult} by creating a json array to contain the entries. Each entry is a json object with
   * the DN represented as a json primitive. Each attribute contains a json array of values.
   */
  private static class SearchResultSerializer implements JsonSerializer<SearchResult>
  {


    @Override
    public JsonElement serialize(final SearchResult result, final Type type, final JsonSerializationContext context)
    {
      final JsonArray json = new JsonArray();
      for (LdapEntry entry : result.getEntries()) {
        final JsonObject jsonEntry = new JsonObject();
        jsonEntry.add("dn", entry.getDn() != null ? new JsonPrimitive(entry.getDn()) : null);
        for (LdapAttribute attr : entry.getAttributes()) {
          final JsonArray jsonAttrValues = new JsonArray();
          attr.getStringValues().forEach(jsonAttrValues::add);
          jsonEntry.add(attr.getName(), jsonAttrValues);
        }
        json.add(jsonEntry);
      }
      return json;
    }
  }
}
