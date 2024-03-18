package io.github.yinjara.rule;


import io.github.yinjara.pojo.BaseRule;

import java.util.Collection;

/**
 * Java规则类存储器
 * @author haibo
 */
public interface JavaRuleStorageService {

  /**
   * 容器是否包含指定规则
   */
  boolean contains(String groupName, BaseRule rule);

  /**
   * 添加规则到容器
   *
   */
  boolean add(String groupName, BaseRule rule);

  /**
   * 批量添加规则到容器的指定组
   */
  boolean batchAdd(String groupName, Iterable<? extends BaseRule> rules);

  /**
   * 从容器移除指定规则
   */
  boolean remove(String groupName, BaseRule rule);

  /**
   * 从容器移除指定组的规则
   */
  boolean remove(String group);

  /**
   * 从容器获取指定组的所有规则
   */
  Collection<BaseRule> listObjByGroup(String group);
}
