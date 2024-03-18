package io.github.yinjara.pojo;

import org.springframework.context.ApplicationEvent;

/**
 * @author haibo
 */
public class RuleEntityEvent extends ApplicationEvent {
    private QualityRuleEntityTypeEnum qualityRuleEntityTypeEnum;
    private String ruleGroupName;

    public QualityRuleEntityTypeEnum getQualityRuleEntityTypeEnum() {
        return qualityRuleEntityTypeEnum;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public RuleEntityEvent(Object source, QualityRuleEntityTypeEnum qualityRuleEntityTypeEnum, String ruleGroupName) {
        super(source);
        this.qualityRuleEntityTypeEnum = qualityRuleEntityTypeEnum;
        this.ruleGroupName = ruleGroupName;
    }
}
