package rfl.astroimagej.dev.catalogs;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.FieldObject;
import rfl.astroimagej.dev.queries.QueryResult;
import rfl.astroimagej.dev.utils.AstroCoords;

/**
 * Queries the AAVSO Variable Star Plotter (VSP) database for field star photometry data user-specified parmeters.
 * The search region is centred on  RA and DEC coordinates and covers a square fov. The query response returns 
 * field star records with photometry data for the specified magnitude band (B, V, Rc or Ic).
 * <p>
 * Example vsp RESTful url: https://app.aavso.org/vsp/api/chart/?format=json&fov=30.0&maglimit=14.5&ra=97.63665&dec=29.67230;
 * Refer getUrl() method for details
 * </p>
 * <p> Note that currently VSP API is not documented on-line (May 2021) </p> 
 * 
 * <p>
 * Json format response, format [root]/[photometry]/[fieldstar[1] ...fieldstar[n]
 * where each field star comprises coordinate data and an array of wave-band magnitude data. 
 * </p>
 */
public class VspCatalog implements AstroCatalog {
	private CatalogQuery query = null;
	private ObjectMapper objectMapper = null;

	// create Jackson object mapper to decode json response to vsp query
	public VspCatalog() {
		objectMapper = new ObjectMapper();
	}

	
	/**
	 *  Runs the VSP database query with url compiled from user-input parameters and decodes
	 *  json response to extract photometry data.
	 *  <p>
	 *  Utilises Jackson api to 'tree-walk' json node structure.
	 *  </p>
	 * 
	 * @param query CatalogQuery object encapsulating VSP database query parameters
	 * 
	 * @return result VSP database QueryResult type response comprising an array of FieldObjects 
	 * 			matching user-input query parameters
	 */
	@Override
	public QueryResult runQuery(CatalogQuery query) {		
		// vsp database query object, configured from user inputs to Catalog dialog
		// filter json response for selected magband / photometry filter type
		this.query = query;	
		String magBand = query.getMagBand();
		
		// vsp query response, index to object id
		QueryResult result = new QueryResult(query.getObjectId());
		
		// reference to json root node
		JsonNode root = null;
		try {
			root = objectMapper.readTree(new URL(getUrl()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// photometry node: parent node for field object nodes
		JsonNode foNodes = root.findPath("photometry");
		for (JsonNode foNode : foNodes) {
			// create a new FieldObject, and add object coordinates + auid
			FieldObject fo = new FieldObject();			
			fo.setObjectId(foNode.findPath("auid").asText());
			fo.setRaHr(AstroCoords.raHms_To_raHr(foNode.path("ra").asText()));
			fo.setDecDeg(AstroCoords.decDms_To_decDeg(foNode.path("dec").asText()));
			
			// if node matching magBand is found then add mag & magErr
			// to fo object and append to result list
			JsonNode bNode = importMagData(foNode, magBand);
			if (bNode != null) {
				fo.setMag(bNode.path("mag").asDouble());
				fo.setMagErr(bNode.path("error").asDouble());
				result.setFieldObject(fo);		// append to List<FieldObject>
			}
		}
		return result;
	}
	
	/*
	 * Searches current field object for magBand photometry data 
	 * 
	 * @param foNode field object node
	 * @param magBand selected mag band (B, V, Rc, Ic)
	 * @return node JsonNode pointing to selected magnitude data (if found), null otherwise
	 */
	private JsonNode importMagData(JsonNode foNode, String magBand) {
		// loop through bands nodes, return node matching magBand otherwise null
		JsonNode node = null;
		JsonNode bandNodes = foNode.findPath("bands");
		for (JsonNode bandNode : bandNodes) {
			String currentBand = bandNode.path("band").asText();
			if (currentBand.equalsIgnoreCase(magBand)) {
				node = bandNode;
				break;
			}
		}
		return node;
	}

	// compile VSP API url
	private String getUrl() {
		String url = "https://app.aavso.org/vsp/api/chart/?format=json"; // VSP header
		url +=  String.format("&fov=%.1f", query.getFovAmin()); // fov nn.n (arcmin)
		url += String.format("&maglimit=%.1f", query.getMagLimit()); // magLimit nn.n (mag)
		url += String.format("&ra=%.5f", query.getRaHr() * 15.0); // ra nnn.nnnnn (0 to 360 deg)
		url += String.format("&dec=%.5f", query.getDecDeg()); // dec nn.nnnnn (0 to ± 90 deg)
		return url;
	}

	public static void main(String[] args) {

		CatalogQuery query = new CatalogQuery();
		VspCatalog catalog = new VspCatalog();
		QueryResult result = catalog.runQuery(query);
		
		System.out.println("here i am");
		System.out.println(result.toString());
	}
}

