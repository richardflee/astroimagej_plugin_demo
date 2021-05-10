package rfl.astroimagej.dev.catalogs;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rfl.astroimagej.dev.enums.SimbadUrlType;
import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.SimbadResult;
import rfl.astroimagej.exceptions.SimbadNotFoundException;

/**
 * Queries the SIMBAD on-line database with user input target index. If a match to
 * the object name is found, downloads object ra and dec coordinates and
 * available B, V, Rc and Ic magnitudes.
 * <p>
 * SIMBAD data is XML-based VOTable format (text and HTML formats are other
 * options). To simplify decoding XML data, single requests are made for each
 * parameter, with a short delay between successive requests. Target and
 * coordinate data should always be imported, but some magnitude data may be
 * missing, e.g. for the R or I bands.
 * </p>
 * <p>
 * Parsing XML formatted data is implemented with XPath.
 * XPath references: e.g. https://howtodoinjava.com/java/xml/java-xpath-tutorial-example/
 * </p>
 * <p>
 * SIMBAD reference: http://simbad.u-strasbg.fr/guide/sim-url.htx.
 * </p>
 */
public class SimbadCatalog {
	private DocumentBuilder builder;
	private CatalogQuery query;	
	private final String NO_DATA = "*****";


	/**
	 * Configure XPath to parse xml.
	 * <p>
	 * Note: Ignores xml namespace.
	 * </p>
	 */
	public SimbadCatalog() {
		// 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			// ignore xml/VOTable namespaces
			factory.setNamespaceAware(false);
			this.builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Runs a sequence of SIMBAD queries to download coordinate and magnitude data
	 * for the user specified target name
	 * <p>
	 * Each parameter is an individual query with a 250 ms buffer delay between
	 * successive queries.
	 * </p>
	 * 
	 * @param query catalog query object containing  user input name index to SIMBAD database
	 * @return result encapsulates SimbadID, coordinates and available magnitudes
	 *         
	 * @throws SimbadNotFoundException thorws this exception if user input name is not 
	 *  		identified in the Simbad database
	 */
	public SimbadResult runQuery(CatalogQuery query) throws SimbadNotFoundException {
		
		this.query = query;		
		SimbadResult result = new SimbadResult(query.getObjectId());
		
		// search database for user input object name
		// throws SimbadNotFoundException if no match found
		String data = downloadSimbadItem(SimbadUrlType.USER_TARGET_NAME);
		result.setSimbadId(data);
		
		// no checks on coordinate data, assumed good
		// object J2000 RA converted deg -> hrs
		data = downloadSimbadItem(SimbadUrlType.RA_HR);
		result.setSimbadRaHr(Double.parseDouble(data) / 15.0);
		
		// object J2000 Dec in deg
		data = downloadSimbadItem(SimbadUrlType.DEC_DEG);
		result.setSimbadDecDeg(Double.parseDouble(data));
		
		// object magnitude for filters B, V, Rc and Ic.
		// return null if no magnitude data for this filter
		data = downloadSimbadItem(SimbadUrlType.MAG_B);
		Double num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagB(num);
		
		data = downloadSimbadItem(SimbadUrlType.MAG_V);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagV(num);
		
		data = downloadSimbadItem(SimbadUrlType.MAG_R);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagR(num);
		
		data = downloadSimbadItem(SimbadUrlType.MAG_I);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagI(num);
		
		return result;
	}


	/*
	 * Compile and return SIMBAD votable (xml) url with user-input target name url
	 * and append url fragment specified by SimbadDataType
	 * 
	 * @param dataType specifies url fragment with associated Simbad data type, RA,
	 * DEC ..
	 * 
	 * @return compiled SIMBAD xml url (see reference above)
	 */
	private String getUrl(SimbadUrlType paramType) {
		String url = "http://simbad.u-strasbg.fr/simbad/sim-id?output.format=votable";
		url += String.format("&Ident=%s&output.params=main_id,", query.getObjectId());
	    return url + paramType.getUrlFragment();
	}

	/*
	 * Queries the SIMBAD database for single SimbadDataType data item. Applies 250
	 * ms delay after query returns to buffer successive queries <p> Refer reference
	 * above for details Xpath xml parser </p>
	 * 
	 * @param dataType query data type, SimbadId, coordinates or filter magnitudes
	 * 
	 * @return text data value
	 * 
	 * @throws SimbadNotFoundException thrown if specified object name is not found
	 * in SIMBAD database
	 */
	private String downloadSimbadItem(SimbadUrlType paramType) throws SimbadNotFoundException {
		NodeList nodes = null;
		String result = null;
		
		// compile SIMBAD url for current SinbadDataType
		String url = getUrl(paramType);
		
		// Create Xpath and run xml query for dataType-specified item
		try {
			Document doc = builder.parse(url);
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//TD/text()");
			nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			// buufer successive queries
			Thread.sleep(250);
		} catch (SAXException | IOException | XPathExpressionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// node item 0 is SimbadId name. If target is current query then 
		// throw SimbadNotFoundException if input object name does not match SIMBAD records
		if (paramType == SimbadUrlType.USER_TARGET_NAME) {
			try {
				result = nodes.item(0).getNodeValue();
			} catch (NullPointerException npe) {
				String message = 
						String.format("Identifier not found in the SIMBAD database: %s ", query.getObjectId());
				throw new SimbadNotFoundException(message);
			}
			// other data is node item 1. Null check manages missing magnitude data  
		} else {
			result = (nodes.getLength() == 1) ? NO_DATA : nodes.item(1).getNodeValue();
		}
		return result;
	}

	
	public static void main(String[] args) throws Exception {
			
		SimbadCatalog simbad = new SimbadCatalog();
		CatalogQuery query = new CatalogQuery();
		
		query.setObjectId("vega");
		
		
		SimbadResult result =  simbad.runQuery(query);		
		System.out.println(result.toString());
		
	}
	
}


