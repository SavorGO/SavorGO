package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.utils;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.ToolBarSelection;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.TimeSeriesChart;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.renderer.*;

public class ToolBarTimeSeriesChartRenderer extends ToolBarSelection<XYItemRenderer> {

    public ToolBarTimeSeriesChartRenderer(TimeSeriesChart chart) {
        super(getRenderers(), renderer -> {
            chart.setRenderer(renderer);
        });
    }

    private static XYItemRenderer[] getRenderers() {
        XYItemRenderer[] renderers = new XYItemRenderer[]{
                new ChartXYCurveRenderer(),
                new ChartXYLineRenderer(),
                new ChartXYBarRenderer(),
                new ChartDeviationStepRenderer(),
                new ChartXYDifferenceRenderer()
        };
        return renderers;
    }
}
