package hankcs.hanlp.model.bigram;

import com.hankcs.hanlp.model.bigram.BigramDependencyModel;
import junit.framework.TestCase;

public class BigramDependencyModelTest extends TestCase
{
    public void testLoad() throws Exception
    {
        assertEquals("限定", BigramDependencyModel.get("传", "v", "角落", "n"));
    }
}