package argos.chart2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple charting application using JFreeChart to chart step data for the Argos 
 * accelerometer-based location tracking application.
 */
public class StepDataCharter extends ApplicationFrame {
	

	private static final long serialVersionUID = 4345581151204054147L;
	private boolean FOOT_DOWN = true;
	private boolean FOOT_UP = false;

    /**
     * Creates a new instance.
     *
     * @param title  the frame title.
     */
    public StepDataCharter(final String title) {

        super(title);

        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }
    
    /**
     * Creates the dataset
     * 
     * @return the dataset
     */
    private XYDataset createDataset() {
        //read a file with no header
        final XYSeries series1 = new XYSeries("X accel");
        final XYSeries series2 = new XYSeries("Y accel");
        final XYSeries series3 = new XYSeries("Z accel");
        final XYSeries series4 = new XYSeries("X cal");
        final XYSeries series5 = new XYSeries("Y cal");
        final XYSeries series6 = new XYSeries("Z cal");
        final XYSeries series7 = new XYSeries("X corrected");
        final XYSeries series8 = new XYSeries("Y corrected");
        final XYSeries series9 = new XYSeries("Z corrected");
        final XYSeries series10 = new XYSeries("X velocity");
        final XYSeries series11 = new XYSeries("Y velocity");
        final XYSeries series12 = new XYSeries("Z velocity");
        final XYSeries series13 = new XYSeries("X pos");
        final XYSeries series14 = new XYSeries("Y pos");
        final XYSeries series15 = new XYSeries("Z pos");
        
    	try {
			BufferedReader br = new BufferedReader(new FileReader("C:\\Temp\\rawdata.txt"));
			try {
				float xCalibrate = 0;
				float yCalibrate = 0;
				float zCalibrate = 0;
				long calibratePoints = 0;
			    String line = br.readLine();
			    boolean previousButton = FOOT_DOWN;
		    	boolean currentButton = FOOT_DOWN;
		    	
		    	float currentTimestamp = 0;
		    	float previousTimestamp = 0;
		    	
		    	float xVel = 0;
		    	float yVel = 0;
		    	float zVel = 0;
		    	
		    	float xPos = 0;
		    	float yPos = 0;
		    	float zPos = 0;
		    	
			    while (line != null) {
			    	String[] items = line.split("\\s+");
			    	currentTimestamp = Float.parseFloat(items[0]);
			    	float stepInt = Integer.parseInt(items[1]);
			    	float xAccel = Float.parseFloat(items[2]);
			    	float yAccel = Float.parseFloat(items[3]);
			    	float zAccel = Float.parseFloat(items[4]);
			    	
			    	if (stepInt == 1) {
			    		currentButton = FOOT_DOWN;
			    	} else {
			    		currentButton = FOOT_UP;
			    	}

			    	if (previousButton == FOOT_DOWN && currentButton == FOOT_UP) {
			    		//Just started stepping
			    		calibratePoints = 0;
			    	}
			    	if (previousButton == FOOT_DOWN && currentButton == FOOT_DOWN) {
			    		//Foot still down
			    		//Keep averaging readings for calibration
		    			xCalibrate = ((xCalibrate * calibratePoints) + xAccel) / (calibratePoints + 1);
		    			yCalibrate = ((yCalibrate * calibratePoints) + yAccel) / (calibratePoints + 1);
		    			zCalibrate = ((zCalibrate * calibratePoints) + zAccel) / (calibratePoints + 1);
			    		calibratePoints++;
			    	}
			    	if (previousButton == FOOT_UP && currentButton == FOOT_UP) {
			    		//Foot still up
			    		//Integrate velocity
			    		float timeDelta = (currentTimestamp - previousTimestamp) / 1000000;
				    	xVel = xVel + (xAccel - xCalibrate) * timeDelta;
				    	yVel = yVel + (yAccel - yCalibrate) * timeDelta;
				    	zVel = zVel + (zAccel - zCalibrate) * timeDelta;
				    	
				    	xPos = xPos + xVel * timeDelta;
				    	yPos = yPos + yVel * timeDelta;
				    	zPos = zPos + zVel * timeDelta;
			    	}
			    	if (previousButton == FOOT_UP && currentButton == FOOT_DOWN) {
			    		//just finished stepping
		    			xCalibrate = xAccel;
		    			yCalibrate = yAccel;
		    			zCalibrate = zAccel;
			    		calibratePoints++;
			    		
			    		//set velocity to zero
				    	xVel = 0;
				    	yVel = 0;
				    	zVel = 0;
			    	}
			    				    	
			        series1.add(currentTimestamp, xAccel);
			        series2.add(currentTimestamp, yAccel);
			        series3.add(currentTimestamp, zAccel);
			        /*
			        series4.add(currentTimestamp, xCalibrate);
			        series5.add(currentTimestamp, yCalibrate);
			        series6.add(currentTimestamp, zCalibrate);			        
			        series7.add(currentTimestamp, xAccel - xCalibrate + 20);
			        series8.add(currentTimestamp, yAccel - yCalibrate + 25);
			        series9.add(currentTimestamp, zAccel - zCalibrate + 30);
			        series10.add(currentTimestamp, xVel + 35);
			        series11.add(currentTimestamp, yVel + 40);
			        series12.add(currentTimestamp, zVel + 45);
			        series13.add(currentTimestamp, xPos + 50);
			        series14.add(currentTimestamp, yPos + 55);
			        series15.add(currentTimestamp, zPos + 60);
			        */
			        previousTimestamp = currentTimestamp;
			        previousButton = currentButton;
			        line = br.readLine();
			    }
			} finally {
			    br.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        dataset.addSeries(series4);
        dataset.addSeries(series5);
        dataset.addSeries(series6);                
        dataset.addSeries(series7);
        dataset.addSeries(series8);
        dataset.addSeries(series9);
        dataset.addSeries(series10);
        dataset.addSeries(series11);
        dataset.addSeries(series12);
        dataset.addSeries(series13);
        dataset.addSeries(series14);
        dataset.addSeries(series15);
        return dataset;
        
    }

    private void createDomainMarkers(XYPlot plot) {
        //read a file with no header
 
    	try {
			BufferedReader br = new BufferedReader(new FileReader("C:\\Temp\\rawdata.txt"));
			try {
			    String line = br.readLine();
			    float stepStart = 0;
			    float stepEnd = 0;
			    boolean footDown = false;
			    while (line != null) {
			    	String[] items = line.split("\\s+");
			    	float timestamp = Float.parseFloat(items[0]);
			    	float stepInt = Integer.parseInt(items[1]);
			    	if (footDown) {
				    	if (stepInt == 1) {
				    		//foot still down
				    		footDown = true;
				    	} else {
				    		footDown = false;
				    		//starting a step
				    		stepStart = timestamp;
				    	}
			    	} else {
				    	if (stepInt == 1) {
				    		//ending a step
				    		stepEnd = timestamp;
					        IntervalMarker marker = new IntervalMarker(stepStart, stepEnd, new Color(0, 0, 255, 50), new BasicStroke(), new Color(0, 255, 0, 50), new BasicStroke(), 0.5f);
					        plot.addDomainMarker(marker, org.jfree.ui.Layer.BACKGROUND);
				    		footDown = true;
				    	} else {
				    		//continuing a step
				    		footDown = false;
				    	}
			    	}
			        line = br.readLine();
			    }
			} finally {
			    br.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the data for the chart.
     * 
     * @return a chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Step Data",      // chart title
            "time",                      // x axis label
            "Y",                      // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
  //      legend.setDisplaySeriesShapes(true);
        
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        //renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesShapesVisible(2, false);
        renderer.setSeriesShapesVisible(3, false);
        renderer.setSeriesShapesVisible(4, false);
        renderer.setSeriesShapesVisible(5, false);
        renderer.setSeriesShapesVisible(6, false);
        renderer.setSeriesShapesVisible(7, false);
        renderer.setSeriesShapesVisible(8, false);
        renderer.setSeriesShapesVisible(9, false);
        renderer.setSeriesShapesVisible(10, false);
        renderer.setSeriesShapesVisible(11, false);        
        renderer.setSeriesShapesVisible(12, false);
        renderer.setSeriesShapesVisible(13, false);
        renderer.setSeriesShapesVisible(14, false);    
        
        createDomainMarkers(plot);
        //IntervalMarker marker = new IntervalMarker(6600000.0, 10000000.0, new Color(0, 0, 255, 50), new BasicStroke(), new Color(0, 255, 0, 50), new BasicStroke(), 0.5f);
        //plot.addDomainMarker(marker, org.jfree.ui.Layer.BACKGROUND);
        //IntervalMarker marker2 = new IntervalMarker(12000000.0, 15000000.0, new Color(0, 0, 255, 50), new BasicStroke(), new Color(0, 255, 0, 50), new BasicStroke(), 0.5f);
        //plot.addDomainMarker(marker2, org.jfree.ui.Layer.BACKGROUND);
        //IntervalMarker marker = new IntervalMarker(2.0, 5.0, new Color(0, 0, 255, 50), new BasicStroke(), new Color(0, 255, 0, 50), new BasicStroke(), 0.5f);
        //plot.addRangeMarker(marker, org.jfree.ui.Layer.BACKGROUND);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.
                
        return chart;
        
    }


    /**
     * Starting method for the charter
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final StepDataCharter demo = new StepDataCharter("Step Data");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}