package io.github.yinjara.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 类编译结果
 * @author haibo
 */
@Getter
@Setter
@Builder
@ToString
public class CompileResult {

  // 主类全类名
  private String mainClassFileName;

  // 编译出来的全类名和对应class字节码
  private Map<String, byte[]> byteCode;
}
