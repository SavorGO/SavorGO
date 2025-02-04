package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.utils;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.ToolBarSelection;

public class ToolBarCategoryOrientation extends ToolBarSelection<String> {

    public ToolBarCategoryOrientation(JFreeChart chart) {
        super(new String[]{"Vertical", "Horizontal"}, orientation -> {
            chart.getCategoryPlot().setOrientation(orientation == "Horizontal" ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL);
        });
    }
}
