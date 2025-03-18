package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.renderer.bar;

import org.jfree.chart.renderer.category.BarRenderer;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.themes.ChartDrawingSupplier;

public class ChartBarRenderer extends BarRenderer {

    public ChartBarRenderer() {
        initStyle();
    }

    private void initStyle() {
        setDefaultLegendShape(ChartDrawingSupplier.getDefaultShape());
    }

    @Override
    public String toString() {
        return "Bar";
    }
}
