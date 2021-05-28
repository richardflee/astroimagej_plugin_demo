package rfl.astroimagej.dev.catalog_simbad;

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

import rfl.astroimagej.dev.enums.SimbadDataType;

/**
 * Queries the SIMBAD on-line database by user input target name. If a match to
 * the object name is found, downloads object ra and dec coordinates and
 * available B, V, Rc and Ic magnitudes.
 * <p>
 * SIMBAD data is XML-based VOTable format (text and HTML formats are other
 * options). To simplify decoding XML data, single requests are made for each
 * parameter, with a short delay between successive requests. Target and
 * coordinate data should always be imported, but some magnitude data may be
 * missing, e.g. for the Rc or Ic bands.
 * </p>
 * <p>
 * Parsing XML formatted data is implemented with XPath.
 * </p>
 * <p>
 * SIMBAD reference: http://simbad.u-strasbg.fr/guide/sim-url.htx. XPath
 * references: e.g.
 * https://howtodoinjava.com/java/xml/java-xpath-tutorial-example/
 * </p>
 */
public class SimbadCatalog {
	// local enums to configure url
//	private enum SimbadDataType {
//		USER_TARGET_NAME, RA_HR, DEC_DEG, MAG_B, MAG_V, MAG_Rc, MAG_Ic;
//	}

	private String inputTargetName;
	private DocumentBuilder builder;
	private final String NO_DATA = "*****";

	public SimbadCatalog() {
		// XPath rituals, refer to XPath reference above
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
	 * Runs a sequence of SIMBAD queries to download coordinate and magnitude data for the user specified target name
	 * <p>
	 * Each parameter is an individual query with a 250 ms buffer delay between successive queries.
	 * </p>
	 * 
	 * @param inputTargetName object identifier entered in the Catalog form
	 * @return SimbadId SimbbadQueryData object encapsulating SimbadID, coordinates and available magnitudes 
	 * @throws SimbadNotFoundException thrown if user input name is not identified in the Simbad database
	 */
	public SimbadQueryData runQuery(String inputTargetName) throws SimbadNotFoundException {

		// initialise SimbadData object
		this.inputTargetName = inputTargetName;
		SimbadQueryData simbadData = new SimbadQueryData(inputTargetName);

		// search database for user input object name
		// throws SimbadNotFoundException if no match found
		String data = downloadSimbadItem(SimbadDataType.USER_TARGET_NAME);
		simbadData.setIdentifier(data);
		
		// no checks on coordinate data, assumed good
		// object J2000 RA converted deg -> hrs
		data = downloadSimbadItem(SimbadDataType.RA_HR);
		Double num = Double.parseDouble(data) / 15.0;
		simbadData.setRaHr(num);

		// object J2000 Dec in deg
		data = downloadSimbadItem(SimbadDataType.DEC_DEG);
		num = Double.parseDouble(data);
		simbadData.setDecDeg(num);

		// object magnitude for filters B, V, Rc and Ic.
		// return null if no magnitude data for this filter
		data = downloadSimbadItem(SimbadDataType.MAG_B);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadData.setMagB(num);

		data = downloadSimbadItem(SimbadDataType.MAG_V);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadData.setMagV(num);

		data = downloadSimbadItem(SimbadDataType.MAG_Rc);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadData.setMagRc(num);

		data = downloadSimbadItem(SimbadDataType.MAG_Ic);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadData.setMagIc(num);
		
		return simbadData;
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
	private String getUrl(SimbadDataType dataType) {
		String url = "http://simbad.u-strasbg.fr/simbad/sim-id?output.format=votable";
		url += String.format("&Ident=%s&output.params=main_id,", inputTargetName);
		// url += urlMap.get(dataType);
		url += dataType.getUrlFragment();

		return url;
	}	
	
	/*
	 * Queries the SIMBAD database for single SimbadDataType data item. Applies 250 ms delay after 
	 * query returns to buffer successive queries
	 * <p>
	 * Refer reference above for details Xpath xml parser
	 * </p>
	 * 
	 * @param dataType query data type, SimbadId, coordinates or filter magnitudes
	 * @return text data value
	 * @throws SimbadNotFoundException thrown if specified object name is not found in SIMBAD database
	 */
	private String downloadSimbadItem(SimbadDataType dataType) throws SimbadNotFoundException {
		NodeList nodes = null;
		String data = null;
		
		// compile SIMBAD url for current SinbadDataType
		String url = getUrl(dataType);
		
		// Creat Xpath and run xml query for dataType-specified item
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
		if (dataType == SimbadDataType.USER_TARGET_NAME) {
			try {
				data = nodes.item(0).getNodeValue();
			} catch (NullPointerException npe) {
				String message = String.format("Identifier not found in the SIMBAD database: %s ", inputTargetName);
				throw new SimbadNotFoundException(message);
			}
			// other data is node item 1. Null check manages missing magnitude data  
		} else {
			data = (nodes.getLength() == 1) ? NO_DATA : nodes.item(1).getNodeValue();
		}
		return data;
	}

	public static void main(String[] args) throws Exception {

		String inputTargetName = "wasp12";
		SimbadCatalog simbad = new SimbadCatalog();

		try {
			SimbadQueryData sData = simbad.runQuery(inputTargetName);
			System.out.println(sData.toString());
		} catch (SimbadNotFoundException se) {
			JOptionPane.showMessageDialog(null, se.getMessage(), "SIMBAD Query Error", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

