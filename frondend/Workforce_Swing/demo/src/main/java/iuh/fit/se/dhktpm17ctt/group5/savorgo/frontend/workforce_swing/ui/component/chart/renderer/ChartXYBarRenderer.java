package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.renderer;

import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.chart.themes.DefaultChartTheme;

public class ChartXYBarRenderer extends ClusteredXYBarRenderer {

    public ChartXYBarRenderer() {
        setBarPainter(DefaultChartTheme.getInstance().getXYBarPainter());
        setShadowVisible(DefaultChartTheme.getInstance().isShadowVisible());
        setMargin(0.3);
    }

    @Override
    public String toString() {
        return "Bar";
    }
}
