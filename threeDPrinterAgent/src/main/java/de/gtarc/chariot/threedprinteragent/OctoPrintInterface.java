package de.gtarc.chariot.threedprinteragent;

import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import org.octoprint.api.*;
import org.octoprint.api.JobCommand.JobState;
import org.octoprint.api.PrinterCommand.ToolCommand;
import org.octoprint.api.exceptions.OctoPrintAPIException;
import org.octoprint.api.model.Axis;
import org.octoprint.api.model.OctoPrintFileInformation;
import org.octoprint.api.model.OctoPrintJob;
import org.octoprint.api.model.OctoPrintJob.FilamentDetails;
import org.octoprint.api.model.OctoPrintJob.JobProgress;
import org.octoprint.api.model.PrinterState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.util.Validate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/***
 * This class serves the usage of a 3D printer, which is controlled via an octoprint api.
 * @author cemakpolat
 *
 */
public class OctoPrintInterface {
	private static final Logger LOG = LoggerFactory.getLogger(OctoPrintInterface.class);
	private DeviceBean bean;
	public  OctoPrintInstance octoprintInstance = null;

	public String apiKey = "834E4BDDDD624A29A6BB7550C135F70A";
	public String baseUrl = "http://130.149.232.234:80";
	public  int port = 80;
	
	private Long estimatedPrintTime = null;
	private Double estimatedFilamentUsage = null;
	private Long remainingPrintTime = null;
	private Double progress = null;
	private String modelName = null;

	public OctoPrintInterface(String url, int port, String apiKey){
		this.apiKey = apiKey;
		this.baseUrl = url+":"+port;
		this.initiate();
	}

	public OctoPrintInterface(DeviceBean device, String url, String apiKey){
		this.bean = device;
		this.apiKey = apiKey;
		this.baseUrl = url;
		this.initiate();
	}
	
	public void initiate() {
		if(this.baseUrl != null || this.apiKey != null) {
			try {
				final URL url = new URL(this.baseUrl);
				this.octoprintInstance = new OctoPrintInstance(url.getHost(),url.getPort(), this.apiKey);
				final SettingsCommand settingsCommand = new SettingsCommand(this.octoprintInstance);
				try {
					final Map<String, String> flatSettingsMap = settingsCommand.getFlatSettingsMap();
					if (flatSettingsMap == null) {
						LOG.error("Could not retrieve settings from octoprint instance.");
					} 
				} catch (final OctoPrintAPIException e) {
					LOG.error("Could not retrieve settings from octoprint instance.", e);
				}
			}
			catch (final MalformedURLException e) {
				LOG.error("Invalid baseUrl provided!", e);
			}
		}else {
			this.octoprintInstance = null;
		}
	}
	
	public void moveToHome() {
		PrinterCommand printer = new PrinterCommand(this.octoprintInstance);
		if(printer.getCurrentState().isReady() && printer.getCurrentState().isOperational()){
		  printer.moveHome();
		}		
	}

	public void pausePrinting() {
		JobCommand jobCommand = new JobCommand(this.octoprintInstance);
		jobCommand.updateJob(JobState.PAUSE);
	}

	public void resumePrinting() {
		JobCommand jobCommand = new JobCommand(this.octoprintInstance);
		jobCommand.updateJob(JobState.RESUME);
	}

	public void restartPrinting() {
		this.pausePrinting();
		JobCommand jobCommand = new JobCommand(this.octoprintInstance);
		jobCommand.updateJob(JobState.RESTART);
	}

	public void cancelPrinting() {
		JobCommand jobCommand = new JobCommand(this.octoprintInstance);
		jobCommand.updateJob(JobState.CANCEL);
	}

	public void connectWithVirtualPort() {
		ConnectionCommand connectionCommand = new ConnectionCommand(this.octoprintInstance);
		connectionCommand.connectWithVirtualPort();
	}

	public void connectWithPortName(final String portName) {
		ConnectionCommand connectionCommand = new ConnectionCommand(this.octoprintInstance);
		connectionCommand.connectWithPortName(portName);
	}

	public void disconnect() {
		ConnectionCommand connectionCommand = new ConnectionCommand(this.octoprintInstance);
		connectionCommand.disconnect();
	}
	
	public void printModel(String modelname) {
		if(this.isPrinterConnected() || this.isPrinterReady()) {
			FileCommand fcommand = new FileCommand(this.octoprintInstance);
			List<OctoPrintFileInformation> lists = fcommand.listFiles();
			
			for(OctoPrintFileInformation file: lists) { // get all files
				if(file.getName().equalsIgnoreCase(modelname)) {
					fcommand.printFile(modelname);
				}
				//System.out.println(""+file.getName());
			}
		}
	}

	public void setExtruderTemperature(Integer value) {
		PrinterCommand printer = new PrinterCommand(this.octoprintInstance);
		if(printer.getCurrentState().isReady() && printer.getCurrentState().isOperational()){
			if(value > 220) value = 220;
			printer.sendExtruderCommand(ToolCommand.TARGET_TEMP,0, value);// max:220 C
		}
	}
	public Double getExtruderTemp() {
		  //set the extruder temp - use the ToolCommand enum to get the command,the extruder num (0 indexed) and the value
		PrinterCommand printer = new PrinterCommand(this.octoprintInstance);
		if(printer.getCurrentState().isConnected()){
			return printer.getExtruderTemp(0).getActualTemp();
		}
		return null;
	}
	
	public void setBedTemperature(Integer value) {
		PrinterCommand printer = new PrinterCommand(this.octoprintInstance);
		if(printer.getCurrentState().isReady() && printer.getCurrentState().isOperational()){
			if(value > 100) value = 100;
			printer.sendBedCommand(ToolCommand.TARGET_TEMP, value);// max 100
		}
	}
	public Double getBedTemperature() {
		PrinterCommand printer = new PrinterCommand(this.octoprintInstance);
		if(printer.getCurrentState().isConnected()){
			return printer.getBedTemp().getActualTemp();
		}
		return null;
	}

	public void transferFileToPrinter(String fileName) {
		if (new PrinterCommand(this.octoprintInstance).getCurrentState().isConnected()){
			new FileCommand(this.octoprintInstance).uploadFile(fileName);
		}
	}
	public String getPrinterCurrentState() {

		if(this.isPrinterPrinting())
			return "PRINTING";
		else if(this.isPrinterPaused())
			return "PAUSED";
		else if(this.isPrinterOperational())
			return "OPERATIONAL";
		else if(this.isPrinterReady())
			return "READY";
		else if(this.isPrinterConnected())
			return "CONNECTED";
		return "UNKNOWN";
	}
	
	public boolean isPrinterConnected() {
		PrinterCommand printerCommand = new PrinterCommand(this.octoprintInstance);
		PrinterState currentState = printerCommand.getCurrentState();
		if(currentState != null) {
			if (currentState.isConnected()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPrinterReady() {
		PrinterCommand printerCommand = new PrinterCommand(this.octoprintInstance);		
		PrinterState currentState = printerCommand.getCurrentState();
		System.out.println(currentState.toString());
		if (currentState.isReady()) {
			return true;
		}
		return false;
	}
	
	public boolean isPrinterPaused() {
		PrinterCommand printerCommand = new PrinterCommand(this.octoprintInstance);		
		PrinterState currentState = printerCommand.getCurrentState();
		if(currentState != null) {
			if (currentState.isPaused()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPrinterPrinting() {
		PrinterCommand printerCommand = new PrinterCommand(this.octoprintInstance);		
		PrinterState currentState = printerCommand.getCurrentState();
		if(currentState != null) {
			if (currentState.isPrinting()) {
				return true;
			}
		}
		return false;
	}

	public boolean isPrinterOperational() {
		final PrinterCommand printerCommand = new PrinterCommand(this.octoprintInstance);		
		final PrinterState currentState = printerCommand.getCurrentState();
		if(currentState != null) {
			if (currentState.isOperational()) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean getlatestJobStatus() {
		if(octoprintInstance == null) {
			return false;
		}
		final JobCommand jobCommand = new JobCommand(octoprintInstance);
		
		final OctoPrintJob currentJob = jobCommand.getJobDetails();
		if(currentJob == null) {
			LOG.error("Failed to retrieve print job information from octoprint url '{}'.", this.baseUrl);
			return false;
		}
		
		String jobName = currentJob.getName();
		Long estimatedPrintTime = currentJob.getEstimatedPrintTime();
		FilamentDetails fila = currentJob.getFilamentDetails(0);
		Double estimatedFilamentUsage = fila == null ? null : fila.volume();
		JobProgress jobProgress = currentJob.getJobProgress();
		Double progress = jobProgress == null ? null : jobProgress.percentComplete();
		Long remainingPrintTime = jobProgress == null ? null : jobProgress.timeRemaining();
		
		if(estimatedPrintTime != null) this.setEstimatedPrintTime(estimatedPrintTime);
		if(estimatedFilamentUsage != null) this.setEstimatedFilamentUsage(estimatedFilamentUsage);
		if(jobProgress != null) {
			this.setProgress(progress);
			this.setRemainingPrintTime(remainingPrintTime);
		}
		
		return jobName != null;
	}
	
	private void setProgress(Double progress) {
		this.progress = progress;
		
	}
	public Double getProgress() {
		getlatestJobStatus();
		return progress;
	}
	
	private void setRemainingPrintTime(Long remainingPrintTime) {
		this.remainingPrintTime = remainingPrintTime;
		
	}
	public Long getRemainingPrintTime() {
		getlatestJobStatus();
		return this.remainingPrintTime;
	}

	private void setEstimatedPrintTime(Long estimatedPrintTime) {
		this.estimatedPrintTime = estimatedPrintTime;
	}
	public Long getEstimatedPrintTime()
	{
		getlatestJobStatus();
		return this.estimatedPrintTime;
	}
	private void setEstimatedFilamentUsage(Double estimatedFilamentUsage) {
		this.estimatedFilamentUsage = estimatedFilamentUsage;
	}
	
	public Double getEstimatedFilamentUsage() {
		getlatestJobStatus();
		return this.estimatedFilamentUsage;
	}
	
	public void setPrinterState(String state) {
		if(this.isPrinterConnected()) {
			if(state.equalsIgnoreCase("START")) {
				this.execute(JobState.START);
				FileCommand fcommand = new FileCommand(this.octoprintInstance);
				//List<OctoPrintFileInformation> lists = fcommand.listFiles();
				fcommand.printFile(this.modelName);
			} else if(state.equalsIgnoreCase("PAUSE")) {
				this.execute(JobState.PAUSE);
			} else if(state.equalsIgnoreCase("CANCEL")) {
				this.execute(JobState.CANCEL);
			} else if(state.equalsIgnoreCase("RESTART")) {
				this.execute(JobState.RESTART);
			} else if(state.equalsIgnoreCase("RESUME")) {
				this.execute(JobState.RESUME);
			}  else if(state.equalsIgnoreCase("TOGGLE")) {
				this.execute(JobState.TOGGLE);
			}
		}
	}

	// execute the given task
	public void execute(final JobState state) {
		if(octoprintInstance == null) {
			return;
		}
		final JobCommand jobCommand = new JobCommand(octoprintInstance);
		
		jobCommand.updateJob(state);
		LOG.debug("Executed job command: {}", state);
	}

		void updateApiKey(final String key) {
			Validate.notNull(key, "Parameter 'key' mustn't be null!");
			this.apiKey = key;
			initiate();
		}
		void updateBaseUrl(final String url) {
			this.baseUrl = url;
			initiate();
		}

	public void moveAxesRelativeToHome(Double x, Double y, Double z) throws InterruptedException {
		// TODO Auto-generated method stub
		PrinterCommand printer = new PrinterCommand(this.octoprintInstance);

		if(printer.getCurrentState().isReady() && printer.getCurrentState().isOperational()) {
			printer.moveOnAxis(Axis.X, x);
			Thread.sleep(5000);
			printer.moveOnAxis(Axis.Y, y);
			Thread.sleep(5000);
			printer.moveOnAxis(Axis.Z, z);
		}
	}

	public void moveAxesRelativeToCurrentPosition(Double x, Double y, Double z) throws InterruptedException {
		// TODO Auto-generated method stub
		PrinterCommand printer = new PrinterCommand(this.octoprintInstance);

		if(printer.getCurrentState().isReady() && printer.getCurrentState().isOperational()) {
			printer.moveOnAxis(Axis.X, x);
			Thread.sleep(5000);
			printer.moveOnAxis(Axis.Y, y);
			Thread.sleep(5000);
			printer.moveOnAxis(Axis.Z, z);
		}
	}

    
	public Map<String, String> getLatestMeasurements() {
		ArrayList<DevicePropertyImpl> propList = new ArrayList<DevicePropertyImpl>();
			Map<String, String> results = new HashMap<String, String>();
			this.getlatestJobStatus();
			long ts = (new Date()).getTime();
			results.put(Constants.status, this.getPrinterCurrentState());
			results.put(Constants.bedTemperature, this.getBedTemperature().toString());

			results.put(Constants.extruderTemperature, this.getExtruderTemp().toString());

			bean.updateProperty("printing-state",this.getPrinterCurrentState());
			bean.updateProperty("bed-temperature",this.getBedTemperature().toString());
			bean.updateProperty("extruder-temperature",this.getExtruderTemp().toString());
	        
			if(this.isPrinterPrinting()) {
				results.put(Constants.estimatedFilamentUsage, this.getEstimatedFilamentUsage().toString());
				results.put(Constants.estimatedPrintTime, this.getEstimatedPrintTime().toString());
				results.put(Constants.progress, this.getProgress().toString());

				bean.updateProperty("extimated-print-time",this.getEstimatedFilamentUsage().toString());
				bean.updateProperty("estimated-filament-usage",this.getEstimatedPrintTime().toString());
				bean.updateProperty("printing-progress",this.getProgress().toString());
			}
			LOG.info(results.toString());
			return results;
		}

		public void setPrintModel(String model) {
			this.modelName = model;
		}

		public String getPrintingModel() {
			return this.modelName;
		}
		public void handleProxyMessage(String json) {
			System.out.println("incoming message:"+json);
			
			// TODO Auto-generated method stub
			//printer state 
			// bed temperature
			// extruder temperature
			// estimatd print time
			// printing model
			// x, y, z - xaxis, yaxis, zaxis

		}


//	public static void main(String[] args) throws Exception {
		//OctoPrintInterface octoprint = new OctoPrintInterface();
//		octoprint.connectWithPortName("/dev/ttyUSB0");
//		octoprint.moveAxesRelativeToHome(50d , 50d, 0d);
//		octoprint.printModel("cube_l.gcode");
//		LOG.info(octoprint.getPrinterCurrentState());
//		LOG.info(octoprint.getBedTemperature().toString());
//		LOG.info(octoprint.getExtruderTemp().toString());
//		octoprint.getlatestJobStatus();
//		octoprint.getLatestMeasurements();
//		octoprint.moveToHome();
//		octoprint.transferFileToPrinter("1leftgripper.gcode");
//	}

}
