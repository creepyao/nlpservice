package com.hankcs.hanlp.dictionary.other;

import com.hankcs.hanlp.dictionary.other.PartOfSpeechTagDictionary;
import junit.framework.TestCase;

public class PartOfSpeechTagDictionaryTest extends TestCase
{
    public void testTranslate() throws Exception
    {
        assertEquals("名词", PartOfSpeechTagDictionary.translate("n"));
    }
}