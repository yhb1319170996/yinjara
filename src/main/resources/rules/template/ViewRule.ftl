import com.analysis.pojo.QualityResultPojo;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Rule(priority = 1)
public class ${className} {
  private String qualityResult;
  private QualityResultPojo actualQualityResult;

  private int generateGradeQualityValue(
      String gradeNameUnions, Map<String, Integer> gradeValueMap) {
    int returnValue = 0;
    if (!gradeNameUnions.isEmpty()) {
      List<String> gradeNameUnionsList = Arrays.asList(gradeNameUnions.split(","));
      for (Map.Entry<String, Integer> entry : gradeValueMap.entrySet()) {
        if (gradeNameUnionsList.contains(entry.getKey())) {
          returnValue += entry.getValue();
        }
      }
    }
    return returnValue;
  }

  @Condition
  public boolean runQuality(
      @Fact("gradeValueMap") Map<String, Integer> gradeValueMap,
      @Fact("actualResult") QualityResultPojo actualResult) {
    double totalScaleNumber = 0;
    String grade1Quality = "${grade1Quality}";
    String grade2Quality = "${grade2Quality}";
    String grade3Quality = "${grade3Quality}";
    String grade4Quality = "${grade4Quality}";
    String grade5Quality = "${grade5Quality}";
    String grade1QualityUnions = "${grade1QualityUnions}";
    String grade2QualityUnions = "${grade2QualityUnions}";
    String grade3QualityUnions = "${grade3QualityUnions}";
    String grade4QualityUnions = "${grade4QualityUnions}";
    String grade5QualityUnions = "${grade5QualityUnions}";
    double grade1InitialValue =
        (!grade1Quality.isEmpty() && grade1QualityUnions.isEmpty())
            ? gradeValueMap.get(grade1Quality)
            : 0;
    double grade2InitialValue =
        (!grade2Quality.isEmpty() && grade2QualityUnions.isEmpty())
            ? gradeValueMap.get(grade2Quality)
            : 0;
    double grade3InitialValue =
        (!grade3Quality.isEmpty() && grade3QualityUnions.isEmpty())
            ? gradeValueMap.get(grade3Quality)
            : 0;
    double grade4InitialValue =
        (!grade4Quality.isEmpty() && grade4QualityUnions.isEmpty())
            ? gradeValueMap.get(grade4Quality)
            : 0;
    double grade5InitialValue =
        (!grade5Quality.isEmpty() && grade5QualityUnions.isEmpty())
            ? gradeValueMap.get(grade5Quality)
            : 0;

    double grade1QualityValue =
        (!grade1Quality.isEmpty() && !grade1QualityUnions.isEmpty())
            ? generateGradeQualityValue(grade1QualityUnions, gradeValueMap)
            : grade1InitialValue;
    double grade2QualityValue =
        (!grade2Quality.isEmpty() && !grade2QualityUnions.isEmpty())
            ? generateGradeQualityValue(grade2QualityUnions, gradeValueMap)
            : grade2InitialValue;
    double grade3QualityValue =
        (!grade3Quality.isEmpty() && !grade3QualityUnions.isEmpty())
            ? generateGradeQualityValue(grade3QualityUnions, gradeValueMap)
            : grade3InitialValue;
    double grade4QualityValue =
        (!grade4Quality.isEmpty() && !grade4QualityUnions.isEmpty())
            ? generateGradeQualityValue(grade4QualityUnions, gradeValueMap)
            : grade4InitialValue;
    double grade5QualityValue =
        (!grade5Quality.isEmpty() && !grade5QualityUnions.isEmpty())
            ? generateGradeQualityValue(grade5QualityUnions, gradeValueMap)
            : grade5InitialValue;
    for (Map.Entry<String, Integer> entry : gradeValueMap.entrySet()) {
      totalScaleNumber += entry.getValue();
    }
    this.actualQualityResult = actualResult;

    double grade1Percent = ${grade1Percent};
    double grade2Percent = ${grade2Percent};
    double grade3Percent = ${grade3Percent};
    double grade4Percent = ${grade4Percent};
    double grade5Percent = ${grade5Percent};
    this.qualityResult = "${qualityResult}";
    return ((grade1QualityValue / totalScaleNumber * 100) ${grade1Attribution} grade1Percent)
        && ((grade2QualityValue / totalScaleNumber * 100) ${grade2Attribution} grade2Percent)
        && ((grade3QualityValue / totalScaleNumber * 100) ${grade3Attribution} grade3Percent)
        && ((grade4QualityValue / totalScaleNumber * 100) ${grade4Attribution} grade4Percent)
        && ((grade5QualityValue / totalScaleNumber * 100) ${grade5Attribution} grade5Percent);
  }

  @Action
  public void generateQualityResult() {
    this.actualQualityResult.setResult(this.qualityResult);
  }
}
