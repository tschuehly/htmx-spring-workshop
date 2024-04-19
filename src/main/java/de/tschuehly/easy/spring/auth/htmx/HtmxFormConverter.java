package de.tschuehly.easy.spring.auth.htmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

@Component
public class HtmxFormConverter implements HttpMessageConverter<Object> {

  private final Charset charset = StandardCharsets.UTF_8;

  @Override
  public boolean canRead(@NotNull Class<?> clazz, MediaType mediaType) {
    if(mediaType == null) return false;
    return mediaType.includes(MediaType.APPLICATION_FORM_URLENCODED);
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return List.of();
  }

  @NotNull
  @Override
  public List<MediaType> getSupportedMediaTypes(@NotNull Class<?> clazz) {
    return List.of(MediaType.APPLICATION_FORM_URLENCODED);
  }


  @NotNull
  @Override
  public Object read(@NotNull Class<?> clazz, HttpInputMessage inputMessage)
      throws IOException, HttpMessageNotReadableException {
    MediaType contentType = inputMessage.getHeaders().getContentType();
    Charset charset = (contentType != null && contentType.getCharset() != null ?
        contentType.getCharset() : this.charset);
    String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

    String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
    HashMap<String, Object> result = new HashMap<>(pairs.length);
    for (String pair : pairs) {
      int idx = pair.indexOf('=');
      if (idx == -1) {
        result.put(URLDecoder.decode(pair, charset), null);
      } else {
        String name = URLDecoder.decode(pair.substring(0, idx), charset);
        String value = URLDecoder.decode(pair.substring(idx + 1), charset);
        result.put(name, value);
      }
    }
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(result, clazz);
  }

  @Override
  public void write(@NotNull Object o, MediaType contentType, HttpOutputMessage outputMessage)
      throws HttpMessageNotWritableException {

  }
}
