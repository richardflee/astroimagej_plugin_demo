package rfl.astroimagej.dev.catalog_ui;

import java.io.IOException;

import javax.swing.JOptionPane;
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

import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.enums.SimbadUrlType;
import rfl.astroimagej.dev.utils.CatalogUrls;
import rfl.astroimagej.exceptions.SimbadNotFoundException;

/**
 * Queries the SIMBAD on-line database with user input object id. If a match is found, 
 * updates catlog form with simbad ra and dec coordinates plus B, V, Rc and Ic magnitudes, as available.
 * <p>
 * SIMBAD data is XML-based VOTable format (text and HTML formats are other options). 
 * To simplify decoding XML data, individual  requests are made for each  parameter.
 * </p>
 * <p>
 *  Target and coordinate data should always be imported, but some magnitude data may be
 * missing, e.g. for the R and/or I bands.
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
	private final String NO_DATA = "*****";

	/**
	 * Configure XPath to parse xml.  Note: Ignores xml namespace.
	 */
	public SimbadCatalog() {
		// Assemble XPath objects ..
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			// ignore xml/VOTable namespaces
			factory.setNamespaceAware(false);
			this.builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			String message = "Error compiling SIMBAD query\n";
			message += e1.getMessage();
			JOptionPane.showMessageDialog(null, message, "Simbad", JOptionPane.INFORMATION_MESSAGE);
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
	 * @throws SimbadNotFoundException throws exception if user input name is not 
	 *  		identified in the Simbad database
	 */
	public SimbadResult runQuery(CatalogQuery query) throws SimbadNotFoundException {
		
		// run Simbad query
		SimbadResult result = new SimbadResult(query.getObjectId());
		
		// search database for user input object name
		// throws SimbadNotFoundException if no match found
		String data = downloadSimbadItem(query, SimbadUrlType.USER_TARGET_NAME);
		result.setSimbadId(data);
		
		// no checks on coordinate data, assumed good
		// object J2000 RA converted deg -> hrs
		data = downloadSimbadItem(query, SimbadUrlType.RA_HR);
		result.setSimbadRaHr(Double.parseDouble(data) / 15.0);
		
		// object J2000 Dec in deg
		data = downloadSimbadItem(query, SimbadUrlType.DEC_DEG);
		result.setSimbadDecDeg(Double.parseDouble(data));
		
		// object magnitude for filters B, V, Rc and Ic.
		// return null if no magnitude data for this filter
		data = downloadSimbadItem(query, SimbadUrlType.MAG_B);
		Double num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagB(num);
		
		data = downloadSimbadItem(query, SimbadUrlType.MAG_V);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagV(num);
		
		data = downloadSimbadItem(query, SimbadUrlType.MAG_R);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagR(num);
		
		data = downloadSimbadItem(query, SimbadUrlType.MAG_I);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		result.setMagI(num);
		
		return result;
	}

	/*
	 * Queries the SIMBAD database for single SimbadDataType data item. Applies 250
	 * ms delay after query returns to buffer successive queries
	 * 
	 * Refer reference above for details Xpath xml parser
	 * 
	 * @param dataType query data type, SimbadId, coordinates or filter magnitudes	 
	 * 
	 * @return text data value 
	 * 
	 * @throws SimbadNotFoundException thrown if specified object name is not found
	 * in SIMBAD database
	 */
	private String downloadSimbadItem(CatalogQuery query, SimbadUrlType paramType) 
			throws SimbadNotFoundException {
		NodeList nodes = null;
		String result = null;
		
		// compile SIMBAD url for current SinbadDataType
		String url = CatalogUrls.getUrl(query, paramType);
		
		// Create Xpath and run xml query for dataType-specified item
		try {
			Document doc = builder.parse(url);
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//TD/text()");
			nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			// buffer successive queries
			Thread.sleep(250);
		} catch (SAXException | IOException | XPathExpressionException | InterruptedException e1) {
			String message = "Error running SIMBAD query\n";
			message += e1.getMessage();
			JOptionPane.showMessageDialog(null, message, "Simbad", JOptionPane.INFORMATION_MESSAGE);
		}
		
		// node item 0 is SimbadId name. 
		// Query objectId: throw SimbadNotFoundException if input does not match SIMBAD records
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
}


