//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package org.pot.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;

@Slf4j
public class JsonUtils {

  private static ThreadLocal<ObjectMapper> objMapperLocal = new ThreadLocal<ObjectMapper>() {
    public ObjectMapper initialValue() {
      return (new ObjectMapper()).configure(Feature.INTERN_FIELD_NAMES, false);
    }
  };

  public JsonUtils() {
  }

  public static String toJSON(Object value) {
    String result = null;

    try {
      result = ((ObjectMapper) objMapperLocal.get()).writeValueAsString(value);
    } catch (Exception var3) {
      log.error("toJson error:{0}", new Object[] { value });
    }

    if ("null".equals(result)) {
      result = null;
    }

    return result;
  }

  public static <T> T toT(String jsonString, Class<T> clazz) {
    try {
      return ((ObjectMapper) objMapperLocal.get()).readValue(jsonString, clazz);
    } catch (Exception var3) {
      log.error("toT error: {0}", new Object[] { jsonString });
      return null;
    }
  }

  public static <T> T toT(String jsonString, TypeReference valueTypeRef) {
    try {
      return ((ObjectMapper) objMapperLocal.get()).readValue(jsonString, valueTypeRef);
    } catch (Exception var3) {
      log.error("toT error: {0}", new Object[] { jsonString });
      return null;
    }
  }

  public static <T> List<T> toTList(String jsonString, Class<T> clazz) {
    try {
      return (List) ((ObjectMapper) objMapperLocal.get())
          .readValue(jsonString, TypeFactory.collectionType(List.class, clazz));
    } catch (Exception var3) {
      log.error("toTList error: {0}", new Object[] { jsonString });
      return null;
    }
  }

  public static Map<String, Object> toMap(String jsonString) {
    return (Map) toT(jsonString, Map.class);
  }

  public static String prettyPrint(Object value) {
    String result = null;

    try {
      result = ((ObjectMapper) objMapperLocal.get()).defaultPrettyPrintingWriter()
          .writeValueAsString(value);
    } catch (Exception var3) {
      log.error("prettyPrint error: {0}", new Object[] { value });
    }

    if ("null".equals(result)) {
      result = null;
    }

    return result;
  }

  public static void main(String[] args) {
    // JsonUtils.Message msg1 = new JsonUtils.Message();
    // msg1.uid = "1";
    // msg1.opr_time = new Date();
    // msg1.content = "hello world---1";
    // JsonUtils.Message msg2 = new JsonUtils.Message();
    // msg2.uid = "2";
    // msg2.opr_time = new Date();
    // msg2.content = "hello world---2";
    // List<JsonUtils.Message> list = new ArrayList();
    // list.add(msg1);
    // list.add(msg2);
    // String json = toJSON(list);
    // System.out.println(json);
    // List<JsonUtils.Message> newMsg = toTList(json, JsonUtils.Message.class);
    // System.out.println(newMsg);
    // System.out.println(((JsonUtils.Message) newMsg.get(0)).uid);
    System.out.print(valid());
  }

  private static boolean valid() {
    for (int i = 0; i < 10; i++) {
      if (i > 11)
        return false;
    }
    return true;
  }

  static class Message {

    String uid;
    Date opr_time;
    @JsonIgnore
    String content;

    Message() {
    }

    public String getUid() {
      return this.uid;
    }

    public void setUid(String uid) {
      this.uid = uid;
    }

    public Date getOpr_time() {
      return this.opr_time;
    }

    public void setOpr_time(Date opr_time) {
      this.opr_time = opr_time;
    }

    public String getContent() {
      return this.content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
