package com.hankcs.hanlp.classification.corpus;

import com.hankcs.hanlp.mining.word2vec.Vector;

public class VectorDocument {
    public Vector vector;
    public String content;
    public int category;

    public Vector getVector() {
        return vector;
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
