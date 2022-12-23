package com.heytap.ad.osync.core.engine;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.config.OsyncRule;
import com.heytap.ad.osync.core.holder.BeanHolder;
import com.heytap.ad.osync.core.common.util.JsonUtil;

import java.util.*;

public abstract class AbstractEngine implements RuleEngine {


    @Override
    public void parse(String ruleJsonStr) {
        Osync osync = BeanHolder.getBean(Osync.class);
        List<OsyncRule> rules = JsonUtil.string2Obj(ruleJsonStr, List.class);
        for (OsyncRule r : rules) {
            doParse(r, false);
            osync.initTrigger(r.getName());
        }
    }

}
