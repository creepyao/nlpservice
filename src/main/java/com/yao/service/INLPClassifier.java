package com.yao.service;

import com.yao.entity.Story;

import javax.jws.WebService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@WebService
public interface INLPClassifier {
    public String train(String userid, String robotid, String corpus, int train_flag) throws IOException;
    public String predict(String userid, String robotid, int trian_flag, String content);
}
