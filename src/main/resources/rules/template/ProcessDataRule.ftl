import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.annotation.Fact;
import com.analysis.pojo.QualityResultPojo;

@Rule(priority = 1)
public class ${className} {
private String qualityResult;
private QualityResultPojo actualQualityResult;

@Condition
public boolean runQuality(@Fact("value") double value,
@Fact("actualProcessDataName") String actualProcessDataName,
@Fact("actualTradeMark") String actualTradeMark,
@Fact("actualResult") QualityResultPojo actualResult) {
this.actualQualityResult = actualResult;
String qualityTradeMark = "${tradeMark}";
String processDataName = "${processDataName}";
double qualityLowerLimit = ${qualityLowerLimit};
double qualityUpperLimit = ${qualityUpperLimit};
this.qualityResult = "${qualityResult}";
return processDataName.equals(actualProcessDataName)
&& actualTradeMark.equals(qualityTradeMark)
&& (value > qualityLowerLimit)
&& (value <= qualityUpperLimit);
}

@Action
public void generateQualityResult() {
this.actualQualityResult.setResult(this.qualityResult);
}
}

