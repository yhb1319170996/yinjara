package io.github.yinjara.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 单缺陷规则
 * @author haibo
 */
@Data
public class SingleDefectRule {
    private String ruleId;

    private String defectName;

    private double lengthLowerLimit;

    private double lengthUpperLimit;

    private double widthLowerLimit;

    private double widthUpperLimit;

    private double areaLowerLimit;

    private double areaUpperLimit;

    private String creator;

    private Date createTime;

    private Date updateTime;

    private String ruleGroupName;

    private String grade;

    private boolean enableState;

    private double densityLowerLimit;

    private double densityUpperLimit;

    private String defectAttributeUnion;

    private String ruleType;
}
