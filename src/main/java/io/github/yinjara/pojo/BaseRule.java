package io.github.yinjara.pojo;

import org.jeasy.rules.annotation.Priority;

import java.util.Objects;

/**
 * 规则基类
 * @author haibo
 */
public class BaseRule {

  private int priority = Integer.MAX_VALUE;

  /*重写equals方法和hashCode方法，让Set集合判定同类型的两个对象相同*/

  @Override
  public boolean equals(Object obj) {
    return Objects.nonNull(obj)
        && Objects.equals(this.getClass().getName(), obj.getClass().getName());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getClass().getName());
  }

  /** 获取优先级 */
  @Priority
  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }
}
