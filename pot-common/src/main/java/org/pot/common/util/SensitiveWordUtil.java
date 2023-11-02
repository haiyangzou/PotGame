package org.pot.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.file.loader.BaseFileLoader;
import org.pot.common.file.loader.LoadManager;
import org.pot.common.util.file.CharsetUtil;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class SensitiveWordUtil implements BaseFileLoader {
    private static final String END = "END";
    private static final SensitiveWordUtil loader = new SensitiveWordUtil();
    private static volatile Map sensitiveWordRootMap = new ConcurrentHashMap<>();

    public SensitiveWordUtil() {
    }

    public static void load(String filePath) {
        LoadManager.loadAndRegister(filePath, loader);
    }

    @Override
    public void loadFile(File file) throws Exception {
        String filePath = FilenameUtil.formatPath(file);
        List<String> sensitiveWords = FileUtils.readLines(file, CharsetUtil.ENCODING_UTF_8);
        Map root = new ConcurrentHashMap<>(sensitiveWords.size());
        for (String sensitiveWord : sensitiveWords) {
            addSensitiveWord(root, sensitiveWord);
        }
        sensitiveWordRootMap = root;
    }

    private static void addSensitiveWord(Map root, String sensitiveWord) {
        sensitiveWord = StringUtils.trimToEmpty(sensitiveWord);
        if (StringUtils.isBlank(sensitiveWord)) {
            return;
        }
        Map nowMap = root;
        for (int i = 0; i < sensitiveWord.length(); i++) {
            char keyChar = sensitiveWord.charAt(i);
            Map charMap = (Map) nowMap.get(keyChar);
            if (charMap == null) {
                charMap = new ConcurrentHashMap();
                charMap.put(END, false);
                nowMap.put(keyChar, charMap);
            }
            nowMap = charMap;
            if (i == sensitiveWord.length() - 1) {
                nowMap.put(END, true);
            }
        }
    }

    public static void addSensitiveWord(String sensitiveWord) {
        addSensitiveWord(sensitiveWordRootMap, sensitiveWord);
    }

    private static void deleteSensitiveWord(Map root, String sensitiveWord) {
        sensitiveWord = StringUtils.trimToEmpty(sensitiveWord);
        if (StringUtils.isBlank(sensitiveWord)) {
            return;
        }
        Map nowMap = root;
        for (int i = 0; i < sensitiveWord.length(); i++) {
            nowMap = (Map) nowMap.get(sensitiveWord.charAt(i));
            if (nowMap == null) {
                return;
            }
        }
        if (Boolean.TRUE.equals(nowMap.get(END))) {
            nowMap.put(END, false);
        }
    }

    public static void deleteSensitiveWord(String sensitiveWord) {
        deleteSensitiveWord(sensitiveWordRootMap, sensitiveWord);
    }

    public static boolean isContainSensitiveWord(String text) {
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> getSensitiveWord(String text) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                result.add(text.substring(i, i + length));
                i = i + length - 1;
            }
        }
        return result;
    }

    public static String replaceSensitiveWord(String text, char ch) {
        String result = text;
        Set<String> set = getSensitiveWord(text);
        for (String word : set) {
            String replacement = StringUtils.repeat(ch, word.length());
            result = StringUtils.replace(result, word, replacement);
        }
        return result;
    }

    private static int checkSensitiveWord(String text, int beginIndex) {
        int length = 0;
        boolean match = false;
        Map nowMap = sensitiveWordRootMap;
        for (int i = 0; i < text.length(); i++) {
            char word = text.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                length++;
                if (Boolean.TRUE.equals(nowMap.get(END))) {
                    match = true;
                    break;
                }
            } else {
                break;
            }
        }
        if (!match) {
            length = 0;
        }
        return length;
    }

    @Override
    public int ordinal() {
        return 0;
    }
}
