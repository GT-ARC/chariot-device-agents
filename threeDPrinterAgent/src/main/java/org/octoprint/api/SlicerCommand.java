package org.octoprint.api;

import org.json.simple.JsonObject;

public class SlicerCommand extends OctoPrintCommand {

    public SlicerCommand(OctoPrintInstance requestor) {
        super(requestor, "slicing");
    }

    /**
     * This will add a new slicing profile
     *
     * @param slicerName the name of the slicer, which the slicer profile will be added to
     * @param profileName the name of the slicer profile
     * @param profileAsJSON slicer profile as a json object
     * @return the result of the request as json object, contains "resource" field : a link to the added slicer profile
     */
    public JsonObject addSlicingProfile(String slicerName, String profileName, JsonObject profileAsJSON) {
        OctoPrintHttpRequest request = this.createRequest(slicerName + "/" + "profiles/" + profileName);

        request.setType("PUT");
        request.setPayload(profileAsJSON);

        return g_comm.executeCreate(request);
    }

}
