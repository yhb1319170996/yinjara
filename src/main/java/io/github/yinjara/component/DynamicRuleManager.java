package io.github.yinjara.component;

import io.github.yinjara.pojo.BaseRule;
import io.github.yinjara.rule.JavaRuleStorageService;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("dynamicRuleManager")
public class DynamicRuleManager {
  private JavaRuleStorageService javaRuleStorageService;

  public Builder builder() {
    return new Builder(this);
  }

  public class Builder {
    private Rules rules = new Rules();
    private Facts facts = new Facts();
    private RulesEngine engine = new DefaultRulesEngine();
    private JavaRuleStorageService javaRuleStorage;

    public Builder(DynamicRuleManager dynamicRuleManager) {
      javaRuleStorage = dynamicRuleManager.javaRuleStorageService;
    }

    /**
     * 设置参数，该参数为值传递，在规则里面或者执行完之后可以取到
     *
     * @param name
     * @param value
     * @return
     */
    public Builder setParameter(String name, Object value) {
      facts.put(name, value);
      return this;
    }

    /**
     * 增加规则组（将指定所属分组的所有启用规则添加进来）
     *
     * @param groupName
     * @return
     */
    public Builder addRuleGroup(String groupName) {
      Collection<BaseRule> rs = javaRuleStorage.listObjByGroup(groupName);
      rs.stream().forEach(rules::register);
      return this;
    }

    /** 运行规则引擎 */
    public Builder run() {
      engine.fire(rules, facts);
      return this;
    }

    /**
     * 获取指定参数，并转为指定类型
     *
     * @param pName
     * @param pType
     * @return
     */
    public <T> T getParameter(String pName, Class<T> pType) {
      return facts.get(pName);
    }
  }
}
