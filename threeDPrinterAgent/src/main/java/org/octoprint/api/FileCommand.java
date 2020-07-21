package org.octoprint.api;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.octoprint.api.model.FileType;
import org.octoprint.api.model.OctoPrintFile;
import org.octoprint.api.model.OctoPrintFileInformation;
import org.octoprint.api.model.OctoPrintFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Implementation of commands found under the File Operations (http://docs.octoprint.org/en/master/api/files.html) endpoint. 
 * 
 * @author rweber, cemakpolat, emregullu
 */
public class FileCommand extends OctoPrintCommand {

	public FileCommand(OctoPrintInstance requestor) {
		super(requestor, "files");
		
	}

	private OctoPrintFileInformation createFile(JsonObject json){
		OctoPrintFileInformation result = null;
		
		//figure out what kind of file this is
		FileType t = FileType.findType(json.get("type").toString());
	
		if(t == FileType.FOLDER)
		{
			result = new OctoPrintFolder(t,json);
		}
		else
		{
			result = new OctoPrintFile(t,json);
		}
		
		return result;
	}
	
	/**
	 * Returns a list of all files (folders and files) from the root. checks local storage only
	 * 
	 * @return a list of all files in the root folder, null if an error occurs
	 */
	public List<OctoPrintFileInformation> listFiles(){
		List<OctoPrintFileInformation> result = null;
		
		//get a list of all the files
		JsonObject json = this.g_comm.executeQuery(this.createRequest("local?recursive=true"));
		
		if(json != null)
		{
			result = new ArrayList<OctoPrintFileInformation>();
			
			JsonArray children = (JsonArray)json.getCollection("files");
			
			for(int count = 0; count < children.size(); count ++)
			{
				//for each file create the object and add to the array
				result.add(this.createFile((JsonObject)children.get(count)));
			}
		}
		
		return result;
	}
	
	/**
	 * Returns an object with information on the given filename
	 * 
	 * @param filename the name of the file, assumes it is local and not on the SD card
	 * @return info about the file, will return null if that file does not exist
	 */
	public OctoPrintFileInformation getFileInfo(final String filename){
		OctoPrintFileInformation result = null;	//returns null if file does not exist
		
		//try and find the file
		JsonObject json = this.g_comm.executeQuery(this.createRequest("local/" + filename + "?recursive=true"));
		
		if(json != null)
		{
			result = this.createFile(json);
		}
		
		return result;
	}
	
	/**
	 * This will load and start printing of the given file
	 * 
	 * @param filename the name of the file, assumes it is local and not on the SD card
	 * @return if operation succeeded
	 */
	public boolean printFile(final String filename){
		OctoPrintHttpRequest request = this.createRequest("local/" + filename);
		request.setType("POST");
		
		//set the payloud
		request.addParam("command", "select");
		request.addParam("print", true);
		
		return g_comm.executeUpdate(request);
	}

	/**
	 * Upload a single file to the given folder located under /api/files/
	 * @param fileName
	 * @param uploadLocation
	 */
	public void uploadFile(final String fileName, String uploadLocation) {
		ClassLoader classLoader = this.getClass().getClassLoader();

		File file = new File(classLoader.getResource(fileName).getFile());

		FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("file", fileBody);
		HttpEntity entity = builder.build();

		HttpPost request = new HttpPost(g_comm.getURL()+ "/org/octoprint/api/files/" +uploadLocation);
		request.setEntity(entity);
		request.setHeader("X-Api-Key", g_comm.getKey());

		HttpClient client = HttpClientBuilder.create().build();

		try {
			client.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Upload all files into the given api/files/<folder>
	 * @param fileNames
	 * @param uploadLocation
	 */
	public void uploadFiles(List<String> fileNames, String uploadLocation) {
		for(String fileName : fileNames) {
			uploadFile(fileName, uploadLocation);
		}
	}

	/**
	 * Upload the file to the local api/files/local folder in the octoprint
	 * @param fileName
	 */
	public void uploadFile(final String fileName) {
		uploadFile(fileName, "local");
	}

	/**
	 * upload the list of files into api/files/local located in octoprint
	 * @param fileNames
	 */
	public void uploadFiles(List<String> fileNames) {
		uploadFiles(fileNames, "local");
	}
	
	/**
	 * This will slice the given STL file into GCODE
	 *
	 * @param fileName the name of the STL file, assumes it is local and not on the SD card
	 * @param slicer the name of slicer
	 * @param slicerProfile the name of the slicer profile which will be used in slicing
	 * @return if operation succeeded
	 */
	public boolean sliceSTLFile(String fileName, String slicer, String slicerProfile) {
		OctoPrintHttpRequest request = this.createRequest("local/" + fileName);

		request.setType("POST");

		request.addParam("command", "slice");
		request.addParam("slicer", slicer);
		request.addParam("profile", slicerProfile);

		return this.g_comm.executeUpdate(request);
	}
	
}
