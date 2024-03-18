package io.github.yinjara.component;

import io.github.yinjara.pojo.CompileResult;
import io.github.yinjara.pojo.QualityRuleEntityTypeEnum;
import io.github.yinjara.pojo.RuleEntityEvent;
import io.github.yinjara.pojo.SingleDefectRule;
import io.github.yinjara.rule.ReloadQualityRules;
import io.github.yinjara.utils.DynamicRuleUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Rules;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class LoadQualityRules implements ApplicationListener<ContextRefreshedEvent>, ReloadQualityRules {

    @Getter
    private static Map<String, Rules> ruleGroupSingleRulesMap = new HashMap<>();

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SingleDefectRule singleDefectRule = new SingleDefectRule();
        singleDefectRule.setRuleId("1");
        singleDefectRule.setRuleType("长度");
        singleDefectRule.setDefectName("孔洞");
        singleDefectRule.setLengthUpperLimit(5);
        singleDefectRule.setAreaLowerLimit(3);


        //TODO: 需改为测试数据
        List<SingleDefectRule> singleDefectRuleList = new ArrayList<>();
        singleDefectRuleList.add(singleDefectRule);

        Map<String, String> strCodeOfSingleRulesMap =
                DynamicRuleUtil.generateSingleRuleString(singleDefectRuleList);

        List<CompileResult> compileResultListOfSingleRules =
                DynamicRuleUtil.compile(strCodeOfSingleRulesMap);

        Rules singleRules = DynamicRuleUtil.getQualityEngineRules(compileResultListOfSingleRules);
        ruleGroupSingleRulesMap.put("单一缺陷判定规则", singleRules);
        log.info("规则引擎初始化完毕");
    }

    @Async
    @Override
    @EventListener(classes = {RuleEntityEvent.class})
    public void reloadQualityRules(RuleEntityEvent ruleEntityEvent) throws Exception {
        log.info("规则引擎重新加载，" + ruleEntityEvent.getRuleGroupName() + "规则组规则发生改变！");
        if (Objects.requireNonNull(ruleEntityEvent.getQualityRuleEntityTypeEnum()) == QualityRuleEntityTypeEnum.SingleDefect) {
            //TODO: 需加入测试数据
            List<SingleDefectRule> singleDefectRuleList = null;
            Map<String, String> strCodeOfSingleRulesMap =
                    DynamicRuleUtil.generateSingleRuleString(singleDefectRuleList);
            List<CompileResult> compileResultListOfSingleRules =
                    DynamicRuleUtil.compile(strCodeOfSingleRulesMap);
            Rules singleRules = DynamicRuleUtil.getQualityEngineRules(compileResultListOfSingleRules);
            ruleGroupSingleRulesMap.clear();
            ruleGroupSingleRulesMap.put(ruleEntityEvent.getRuleGroupName(), singleRules);
        }
    }
}